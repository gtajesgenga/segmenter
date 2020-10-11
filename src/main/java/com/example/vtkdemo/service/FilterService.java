package com.example.vtkdemo.service;

import com.example.vtkdemo.entity.Filter;
import com.example.vtkdemo.entity.Method;
import com.example.vtkdemo.entity.Parameter;
import javassist.Modifier;
import org.apache.commons.lang3.ClassUtils;
import org.itk.simple.ImageFilter_1;
import org.reflections.Reflections;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FilterService {

    private static final Map<String, List<Method>> filtersMap;

    static {
        filtersMap = getSubtypes().stream()
                .collect(Collectors.toMap(Class::getCanonicalName, clazz -> getMethods(clazz.getMethods())));
    }

    public static List<Filter> findAll() {
        List<Filter> results;

        results = filtersMap.entrySet().stream()
                .map(filter -> Filter.builder()
                        .filterClass(filter.getKey())
                        .methods(filter.getValue())
                        .build())
                .collect(Collectors.toList());

        results.sort(Filter::compareTo);

        return results;
    }

    public static Optional<Filter> find(String className) {
        return filtersMap.entrySet().stream()
                .filter(filter -> filter.getKey().equals(className) || filter.getKey().substring(filter.getKey().lastIndexOf('.') + 1).equals(className))
                .map(filter -> Filter.builder()
                        .filterClass(filter.getKey())
                        .methods(filter.getValue())
                        .build())
                .findFirst();
    }

    private static List<Method> getMethods(java.lang.reflect.Method[] methods) {
        return Stream.of(methods)
                .filter(method -> Modifier.isPublic(method.getModifiers()))
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

                            if (org.springframework.util.ClassUtils.hasMethod(parameter.getType(), "size")) {
                                java.lang.reflect.Method setMethod = Arrays.stream(parameter.getType().getMethods())
                                        .filter(paramMethod -> "set".equals(paramMethod.getName()))
                                        .findFirst()
                                        .orElse(null);

                                if (Objects.nonNull(setMethod)) {
                                    Class paramClazz = setMethod.getParameterTypes()[1];
                                    parambuilder.multidimensional(paramClazz.isPrimitive() ? ClassUtils.primitiveToWrapper(paramClazz).getCanonicalName() : paramClazz.getCanonicalName());
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
