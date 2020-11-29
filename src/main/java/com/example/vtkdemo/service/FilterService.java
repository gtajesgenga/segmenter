package com.example.vtkdemo.service;

import com.example.vtkdemo.entity.Filter;
import com.example.vtkdemo.entity.Method;
import com.example.vtkdemo.entity.Parameter;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import javassist.Modifier;
import org.apache.commons.lang3.ClassUtils;
import org.itk.simple.ImageFilter;
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

    @Counted
    @Timed
    public List<Filter> findAll() {
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

    @Counted
    @Timed
    public Optional<Filter> find(String className) {
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
                .filter(method -> method.getName().startsWith("set") || method.getName().startsWith("get") || method.getName().startsWith("add"))
                .map(method -> {
                    List<Parameter> parameters = getParameters(method.getParameters());
                    return Method.builder()
                            .name(method.getName())
                            .parameters(parameters);
                })
                .map(Method.MethodBuilder::build).collect(Collectors.toList());
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
                                        .dropWhile(paramMethod -> paramMethod.getParameterTypes()[1].getCanonicalName().equals("java.lang.Object"))
                                        .findFirst()
                                        .orElse(null);

                                if (Objects.nonNull(setMethod)) {
                                    Class<?> paramClazz = setMethod.getParameterTypes()[1];
                                    parambuilder.multidimensional(paramClazz.isPrimitive() ? ClassUtils.primitiveToWrapper(paramClazz).getCanonicalName() : paramClazz.getCanonicalName());
                                }
                            }

                            if (org.springframework.util.ClassUtils.hasMethod(parameter.getType(), "swigValue")) {
                                Map<String, Integer> _map = Arrays.stream(parameter.getType().getDeclaredFields())
                                        .filter(field -> Modifier.isPublic(field.getModifiers()))
                                        .filter(field -> Modifier.isFinal(field.getModifiers()))
                                        .filter(field -> Modifier.isStatic(field.getModifiers()))
                                        .collect(Collectors.toMap(Field::getName, field -> {
                                            Integer _value = -1;
                                            try {
                                                Object _instance = field.get(null);
                                                _value = (Integer) field.getType().getDeclaredMethod("swigValue").invoke(_instance);
                                            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                                                e.printStackTrace();
                                            }
                                            return _value;
                                        }));
                                parambuilder.hasValues(true);
                                parambuilder.values(_map);
                            }
                            return parambuilder.build();
                        }
                )
                .collect(Collectors.toList());
    }

    private static Set<Class<? extends ImageFilter>> getSubtypes() {
        Reflections reflections = new Reflections("org.itk.simple");
        return reflections.getSubTypesOf(ImageFilter.class);
    }
}
