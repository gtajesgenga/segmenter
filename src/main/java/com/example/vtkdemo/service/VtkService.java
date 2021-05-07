package com.example.vtkdemo.service;

import javax.annotation.PostConstruct;

import com.example.vtkdemo.client.OrthancClient;
import com.example.vtkdemo.config.ApplicationConfig;
import com.example.vtkdemo.entity.Pipeline;
import com.example.vtkdemo.entity.Filter;
import com.example.vtkdemo.entity.Method;
import com.example.vtkdemo.entity.Parameter;
import com.example.vtkdemo.repository.PipelineRepository;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.itk.simple.Image;
import org.itk.simple.ImageSeriesReader;
import org.itk.simple.SimpleITK;
import org.itk.simple.VectorString;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import vtk.*;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

@Service
@Slf4j
public class VtkService {

    private static final String ERROR_GETTING_METHOD = "Error getting method";
    private static final String ERROR_ON_ACCESS_TO_METHOD = "Error on access to method";
    private static final String ERROR_ON_INVOCATION = "Error on invocation";
    private final Deque<Image> images = new LinkedList<>();

    private final ApplicationConfig applicationConfig;

    private final OrthancClient orthancClient;

    private final PipelineRepository pipelineRepository;


    public VtkService(ApplicationConfig applicationConfig, OrthancClient orthancClient, PipelineRepository pipelineRepository) {
        this.applicationConfig = applicationConfig;
        this.orthancClient = orthancClient;
        this.pipelineRepository = pipelineRepository;
    }

    @PostConstruct
    private void initialize() {
        if (!vtkNativeLibrary.LoadAllNativeLibraries())
        {
            for (vtkNativeLibrary lib : vtkNativeLibrary.values())
            {
                if (!lib.IsLoaded())
                {
                    log.warn("{} not loaded", lib.GetLibraryName());
                }
            }
        }
        vtkNativeLibrary.DisableOutputWindow(null);
    }

    @Counted(value = "execute.count")
    @Timed(value = "execute.time")
    public byte[] execute(String studyId, String serieId, Long pipelineId) throws InvocationTargetException {

        images.clear();
        List<String> instances = Optional.ofNullable(orthancClient.getInstances(studyId, serieId)).orElse(Collections.emptyList());

        Optional<Pipeline> pipeline = pipelineRepository.findById(pipelineId);

        if (pipeline.isPresent()) {
            Path path = null;

            if (!instances.isEmpty()) {
                path = Paths.get(applicationConfig.getTempFolder(), studyId, serieId);
                log.debug("Path to create: {}", path.toAbsolutePath());

                if (path.toFile().mkdirs()) {

                    log.debug("Path created");
                    var finalPath = path;
                    instances.forEach(instance -> {
                        var file = Paths.get(finalPath.toString(), instance).toFile();
                        try {
                            OutputStream os = new FileOutputStream(file);
                            os.write(orthancClient.fetchInstance(instance));
                            os.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }

            var imageSeriesReader = new ImageSeriesReader();
            final VectorString dicomNames = ImageSeriesReader.getGDCMSeriesFileNames(Objects.requireNonNull(path).toString());
            imageSeriesReader.setFileNames(dicomNames);

            images.addFirst(imageSeriesReader.execute());

            if (applicationConfig.getEnablePreview()) {
                SimpleITK.show(images.peek(), String.valueOf(images.size()), false);
            }

            for (Filter filter : pipeline.get().getFilters()) {
                processFilter(filter);
            }

            SimpleITK.writeImage(images.peekFirst(), Paths.get(path.toString(), "generated.vtk").toString());

            // Read the file
            var reader = new vtkStructuredPointsReader();
            reader.SetFileName(Paths.get(path.toString(), "generated.vtk").toString());
            reader.Update();

            var contourFilter = new vtkContourFilter();
            contourFilter.SetInputData(reader.GetOutput());
            contourFilter.SetValue(0, 1.0);

            // Create a mapper and actor
            var mapper = new vtkPolyDataMapper();
            mapper.SetInputConnection(contourFilter.GetOutputPort());
            mapper.ScalarVisibilityOff();

            var writer = new vtkPolyDataWriter();
            writer.SetFileName(Paths.get(path.toString(), "out.vtk").toString());
            writer.SetInputConnection(contourFilter.GetOutputPort());
            writer.SetFileTypeToBinary();
            writer.Write();

            images.clear();
            try {
                return IOUtils.toByteArray(new FileInputStream(Paths.get(path.toString(), "out.vtk").toString()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new byte[0];
    }

    private void processFilter(Filter filter) throws InvocationTargetException {

        try {
            Object instance = Class.forName(filter.getFilterClass()).getConstructor().newInstance();

            log.debug("Processing filter: {}", filter);
            filter.getMethods()
                    .forEach(method -> processMethod(instance, method));

            assert images.peekFirst() != null;
            images.addFirst((Image) instance.getClass().getMethod("execute", images.peekFirst().getClass()).invoke(instance, images.peekFirst()));

            if (applicationConfig.getEnablePreview()) {
                SimpleITK.show(images.peek(), String.valueOf(images.size()), false);
            }
        } catch (InstantiationException e) {
            log.error("Error creating new instance of '{}'", filter.getFilterClass(), e);
        } catch (NoSuchMethodException e) {
            log.error(ERROR_GETTING_METHOD, e);
        } catch (IllegalAccessException e) {
            log.error(ERROR_ON_ACCESS_TO_METHOD, e);
        } catch (ClassNotFoundException e) {
            log.error("Error getting class", e);
        }
    }

    private void processMethod(Object instance, Method method) {
        try {
            log.info("Processing method: {}", method.toString());
            instance.getClass().getMethod(method.getName(), getParamsTypes(method.getParameters())).invoke(instance, getParamsValues(method.getParameters()));
        } catch (NoSuchMethodException e) {
            log.error(ERROR_GETTING_METHOD, e);
        } catch (IllegalAccessException e) {
            log.error(ERROR_ON_ACCESS_TO_METHOD, e);
        } catch (InvocationTargetException e) {
            log.error(ERROR_ON_INVOCATION, e);
        }
    }

    private Object[] getParamsValues(List<Parameter> parameters) {
        Object[] result = parameters.stream()
                .map(parameter -> {
                    try {
                        log.info("Getting param value for parameter: {}", parameter.toString());

                        if (ClassUtils.isPrimitiveOrWrapper(parameter.getDefaultCasting())) {

                            return parameter.getDefaultCasting().getMethod("parse" + parameter.getDefaultCasting().getSimpleName(), String.class)
                                    .invoke(null, parameter.getValue());
                        }
                    } catch (NoSuchMethodException e) {
                        log.error(ERROR_GETTING_METHOD, e);
                    } catch (IllegalAccessException e) {
                        log.error(ERROR_ON_ACCESS_TO_METHOD, e);
                    } catch (InvocationTargetException e) {
                        log.error(ERROR_ON_INVOCATION, e);
                    }

                    return parameter.getDefaultCasting().cast(processValue(parameter));
                }).toArray();

        log.info("Processed params values: {}", result);
        return result;
    }

    private Object processValue(Parameter parameter) {
        if (parameter.getMultidimensional() != null) {
            try {
                log.debug("Processing values for parameter: {}", parameter);
                String[] strArray = parameter.getValue().replace("[", "").replace("]", "").split(",");


                Object instance = parameter.getDefaultCasting().getConstructor().newInstance();

                final var i = new int[] {0};
                Stream.of(strArray)
                        .forEach(s -> {
                            try {
                                Object value;
                                if (s.endsWith("%")) {
                                    var doublePercent = Double.parseDouble(s.replace("%", ""));
                                    assert images.peekFirst() != null;
                                    var doubleValue = Double.parseDouble(images.peekFirst().getSize().get(i[0]).toString()) * doublePercent / 100D;
                                    value = parameter.getMultidimensionalClass().getMethod("valueOf", String.class).invoke(null, "0");

                                    if (value instanceof Integer) {
                                        value = (int) doubleValue;
                                    } else {
                                        value = Math.round(doubleValue);
                                    }
                                } else {
                                    value = parameter.getMultidimensionalClass().getMethod("valueOf", String.class).invoke(null, s);
                                }
                                instance.getClass().getMethod("add", org.apache.commons.lang3.ClassUtils.primitiveToWrapper(parameter.getMultidimensionalClass()
                                        .getMethod(parameter.getMultidimensionalClass().getSimpleName().toLowerCase().replace("integer", "int") + "Value")
                                        .getReturnType()))
                                        .invoke(instance, value);
                            i[0]++;
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                log.error(ERROR_ON_INVOCATION, e);
                            } catch (NoSuchMethodException e) {
                                log.error(ERROR_GETTING_METHOD, e);
                            }
                        });

                return instance;

            } catch (NoSuchMethodException e) {
                log.error(ERROR_GETTING_METHOD, e);
            } catch (IllegalAccessException e) {
                log.error(ERROR_ON_ACCESS_TO_METHOD, e);
            } catch (InvocationTargetException e) {
                log.error(ERROR_ON_INVOCATION, e);
            } catch (InstantiationException e) {
                log.error("Error creating new instance of '{}'", parameter.getDefaultCasting().getCanonicalName(), e);
            }
        } else if (Boolean.TRUE.equals(parameter.getHasValues())) {
            try {
                return parameter.getDefaultCasting().getMethod("swigToEnum", int.class).invoke(null, Integer.valueOf(parameter.getValue()));
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return parameter.getValue();
    }

    private Class<?>[] getParamsTypes(List<Parameter> parameters) {
        return parameters.stream()
                .map(Parameter::getDefaultCasting)
                .map(clazz -> {
                    try {

                        if (ClassUtils.isPrimitiveOrWrapper(clazz))
                            return clazz.getMethod(clazz.getSimpleName().toLowerCase().replace("integer", "int") + "Value").getReturnType();
                    } catch (NoSuchMethodException e) {
                        log.error(ERROR_GETTING_METHOD, e);
                    }

                    return clazz;
                }).toArray(Class[]::new);
    }
}
