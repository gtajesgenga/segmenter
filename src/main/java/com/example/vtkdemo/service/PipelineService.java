package com.example.vtkdemo.service;

import com.example.vtkdemo.model.Filter;
import com.example.vtkdemo.model.Method;
import com.example.vtkdemo.model.Parameter;
import com.example.vtkdemo.model.Pipeline;
import lombok.extern.slf4j.Slf4j;
import org.itk.simple.*;
import org.reflections.Reflections;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class PipelineService {

    private Stack<Image> images = new Stack<>();


    public void execute(Pipeline pipeline) {

        ImageSeriesReader imageSeriesReader = new ImageSeriesReader();
        final VectorString dicomNames = ImageSeriesReader.getGDCMSeriesFileNames(pipeline.getInputPath());
        imageSeriesReader.setFileNames(dicomNames);

        images.push(imageSeriesReader.execute());

        SimpleITK.show(images.peek(), String.valueOf(images.size()), false);

        pipeline.getFilters()
                .forEach(this::processFilter);

        SimpleITK.writeImage(images.peek(), pipeline.getOutputFile());
    }

    private void processFilter(Filter filter) {
        try {
            Object instance = Class.forName(filter.getFilterClass()).getConstructor().newInstance();

            log.debug("Processing filter: {}", filter.toString());
            filter.getMethods()
                    .forEach(method -> processMethod(instance, method));

            images.push((Image) instance.getClass().getMethod("execute", images.peek().getClass()).invoke(instance, images.peek()));
            SimpleITK.show(images.peek(), String.valueOf(images.size()), false);
        } catch (InstantiationException e) {
            log.error("Error creating new instance of '{}'", filter.getFilterClass(), e);
        } catch (InvocationTargetException e) {
            log.error("Error on invocation", e);
        } catch (NoSuchMethodException e) {
            log.error("Error getting method", e);
        } catch (IllegalAccessException e) {
            log.error("Error on access to method", e);
        } catch (ClassNotFoundException e) {
            log.error("Error getting class", e);
        } finally {
            System.gc();
        }
    }

    private static void processMethod(Object instance, Method method) {
        try {
            log.debug("Processing method: {}", method.toString());
            instance.getClass().getMethod(method.getName(), getParamsTypes(method.getParameters())).invoke(instance, getParamsValues(method.getParameters()));
        } catch (NoSuchMethodException e) {
            log.error("Error getting method", e);
        } catch (IllegalAccessException e) {
            log.error("Error on access to method", e);
        } catch (InvocationTargetException e) {
            log.error("Error on invocation", e);
        }
    }

    private static Object[] getParamsValues(List<Parameter> parameters) {
        return parameters.stream()
                .map(parameter -> {
                    try {
                        log.debug("Getting param value for parameter: {}", parameter.toString());

                        if (ClassUtils.isPrimitiveOrWrapper(parameter.getDefaultCasting())) {

                            return parameter.getDefaultCasting().getMethod("parse" + parameter.getDefaultCasting().getSimpleName(), String.class)
                                    .invoke(null, parameter.getValue());
                        }
                    } catch (NoSuchMethodException e) {
                        log.error("Error getting method", e);
                    } catch (IllegalAccessException e) {
                        log.error("Error on access to method", e);
                    } catch (InvocationTargetException e) {
                        log.error("Error on invocation", e);
                    }

                    return parameter.getDefaultCasting().cast(processValue(parameter));
                }).toArray();
    }

    private static Object processValue(Parameter parameter) {
        if (parameter.getMultidimensional() != null) {
            try {
                log.debug("Processing values for parameter: {}", parameter.toString());
                String[] strArray = parameter.getValue().replace("[", "").replace("]", "").split(",");

                Object instance = parameter.getDefaultCasting().getConstructor(long.class).newInstance(strArray.length);

                var ref = new Object() {
                    int i = 0;
                };
                Stream.of(strArray)
                        .forEach(s -> {
                            try {
                                instance.getClass().getMethod("set", int.class, parameter.getMultidimensionalClass()
                                        .getMethod(parameter.getMultidimensionalClass().getSimpleName().toLowerCase().replace("integer", "int") + "Value").getReturnType())
                                        .invoke(instance, ref.i, parameter.getMultidimensionalClass().getMethod("valueOf", String.class).invoke(null, s));
                            ref.i++;
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                log.error("Error on invocation", e);
                            } catch (NoSuchMethodException e) {
                                log.error("Error getting method", e);
                            }
                        });

//                for (int i = 0; i < strArray.length; i++) {
//                    Object caster = parameter.getMultidimensionalClass().getConstructor(String.class)
//                            .newInstance(strArray[i]);
//
//                    Object[] args = new Object[]{i, caster.getClass().getMethod(caster.getClass().getSimpleName().toLowerCase().replace("integer", "int") + "Value")
//                            .invoke(caster)};
//                    instance.getClass().getMethod("set", int.class, caster.getClass().getMethod(caster.getClass().getSimpleName().toLowerCase().replace("integer", "int") + "Value").getReturnType()).invoke(instance, args);
//                }

                return instance;

            } catch (NoSuchMethodException e) {
                log.error("Error getting method", e);
            } catch (IllegalAccessException e) {
                log.error("Error on access to method", e);
            } catch (InvocationTargetException e) {
                log.error("Error on invocation", e);
            } catch (InstantiationException e) {
                log.error("Error creating new instance of '{}'", parameter.getDefaultCasting().getCanonicalName(), e);
            }
        }
        return parameter.getValue();
    }

    private static Class<?>[] getParamsTypes(List<Parameter> parameters) {
        return parameters.stream()
                .map(Parameter::getDefaultCasting)
                .map(clazz -> {
                    try {

                        if (ClassUtils.isPrimitiveOrWrapper(clazz))
                            return clazz.getMethod(clazz.getSimpleName().toLowerCase().replace("integer", "int") + "Value").getReturnType();
                    } catch (NoSuchMethodException e) {
                        log.error("Error getting method", e);
                    }
                    return clazz;
                })
                .collect(Collectors.toUnmodifiableList()).toArray(new Class[0]);
    }

    public static List<Filter> getFilters() {
        List<Filter> results;

        Reflections reflections = new Reflections("org.itk.simple");
        Set<Class<? extends ImageFilter_1>> subTypes = reflections.getSubTypesOf(ImageFilter_1.class);

        results = subTypes.stream()
                .map(clazz -> Filter.builder()
                        .filterClass(clazz.getCanonicalName())
                        .methods(getMethods(clazz.getMethods()))
                ).map(Filter.FilterBuilder::build).collect(Collectors.toList());

        return results;
    }

    private static List<Method> getMethods(java.lang.reflect.Method[] methods) {
        return Stream.of(methods)
                .filter(method -> method.getName().startsWith("set"))
                .map(method ->
                        Method.builder()
                                .name(method.getName())
                                .parameters(getParameters(method.getParameters()))
                ).map(Method.MethodBuilder::build).collect(Collectors.toList());
    }

    private static List<Parameter> getParameters(java.lang.reflect.Parameter[] parameters) {
        return Stream.of(parameters)
                .map(parameter -> {
                            Parameter.ParameterBuilder parambuilder = Parameter.builder()
                                    .casting(ClassUtils.isPrimitiveOrWrapper(parameter.getType()) ? "java.lang." + StringUtils.capitalize(parameter.getType().getSimpleName()) :
                                            parameter.getType().getCanonicalName());

                            if (ClassUtils.hasMethod(parameter.getType(), "size")) {
                                java.lang.reflect.Method setMethod = Arrays.stream(parameter.getType().getMethods())
                                        .filter(paramMethod -> "set".equals(paramMethod.getName()))
                                        .findFirst()
                                        .orElse(null);

                                if (Objects.nonNull(setMethod)) {
                                    Class paramClazz = setMethod.getParameterTypes()[1];
                                    parambuilder.multidimensional(ClassUtils.isPrimitiveOrWrapper(paramClazz) ? "java.lang." + StringUtils.capitalize(paramClazz.getSimpleName()) :
                                            paramClazz.getCanonicalName());
                                }
                            }
                            return parambuilder;
                        }
                )
                .map(Parameter.ParameterBuilder::build)
                .collect(Collectors.toList());
    }
}
