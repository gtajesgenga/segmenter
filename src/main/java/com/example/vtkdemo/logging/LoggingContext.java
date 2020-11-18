package com.example.vtkdemo.logging;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import com.example.vtkdemo.controller.HomeController;

public final class LoggingContext {
    private static final String CONTROLLER_NAME = "controllerName";
    private static final String CONTROLLER_METHOD_NAME = "controllerMethodName";
    private static final String HOME_CONTROLLER_NAME = HomeController.class.getSimpleName();

    private LoggingContext() {
// Hide default constructor for utility method
    }

    static String getControllerName() {
        return MDC.get(CONTROLLER_NAME);
    }

    static void setControllerName(String controllerName) {
        MDC.put(CONTROLLER_NAME, controllerName);
    }

    static String getControllerMethodName() {
        return MDC.get(CONTROLLER_METHOD_NAME);
    }

    static void setControllerMethodName(String controllerMethodName) {
        MDC.put(CONTROLLER_METHOD_NAME, controllerMethodName);
    }

    static boolean shouldLogAccess() {
        String controllerName = MDC.get(CONTROLLER_NAME);
        return StringUtils.isNotEmpty(controllerName)
                && !controllerName.equals(HOME_CONTROLLER_NAME);
    }

    static void clear() {
        MDC.clear();
    }
}
