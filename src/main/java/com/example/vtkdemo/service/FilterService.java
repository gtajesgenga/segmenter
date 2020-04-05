package com.example.vtkdemo.service;

import com.example.vtkdemo.model.FilterDto;
import com.example.vtkdemo.model.Method;
import com.example.vtkdemo.model.Parameter;
import org.apache.commons.lang3.ClassUtils;
import org.itk.simple.ImageFilter_1;
import org.reflections.Reflections;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FilterService {

    private static Map<String, List<Method>> filtersMap;

    static {
        filtersMap = getSubtypes().stream()
                .collect(Collectors.toMap(Class::getCanonicalName, clazz -> getMethods(clazz.getMethods())));
    }

    public static List<FilterDto> findAll() {
        List<FilterDto> results;

        results = filtersMap.entrySet().stream()
                .map(filter -> FilterDto.builder()
                        .filterClass(filter.getKey())
                        .methods(filter.getValue())
                        .build())
                .collect(Collectors.toList());

        return results;
    }

    public static Optional<FilterDto> find(String className) {
        return filtersMap.entrySet().stream()
                .filter(filter -> filter.getKey().equals(className) || filter.getKey().substring(filter.getKey().lastIndexOf('.') +1 ).equals(className))
                .map(filter -> FilterDto.builder()
                        .filterClass(filter.getKey())
                        .methods(filter.getValue())
                        .build())
                .findFirst();
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
                                    .name(parameter.getName())
                                    .casting(parameter.getType().isPrimitive() ? ClassUtils.primitiveToWrapper(parameter.getType()).getCanonicalName() : parameter.getType().getCanonicalName());
//                                    .casting(ClassUtils.isPrimitiveOrWrapper(parameter.getType()) ? "java.lang." + StringUtils.capitalize(parameter.getType().getSimpleName()) :
//                                            parameter.getType().getCanonicalName());

                            if (org.springframework.util.ClassUtils.hasMethod(parameter.getType(), "size")) {
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
                            return parambuilder.build();
                        }
                )
                .collect(Collectors.toList());
    }

    private static Set<Class<? extends ImageFilter_1>> getSubtypes() {
        Reflections reflections = new Reflections("org.itk.simple");
        return reflections.getSubTypesOf(ImageFilter_1.class);
    }
}
