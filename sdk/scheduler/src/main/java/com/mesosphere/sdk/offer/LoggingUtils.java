package com.mesosphere.sdk.offer;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility methods around construction of loggers.
 */
public class LoggingUtils {

    private LoggingUtils() {
        // do not instantiate
    }

    /**
     * Creates a logger which is tagged with the provided class.
     *
     * @param clazz the class using this logger
     */
    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(getClassName(clazz));
    }

    /**
     * Creates a logger which is tagged with the provided class and the provided custom label.
     *
     * @param clazz the class using this logger
     * @param name  an additional context label detailing e.g. the name of the service being managed
     */
    public static Logger getLogger(Class<?> clazz, String name) {
        if (StringUtils.isBlank(name)) {
            return getLogger(clazz);
        } else {
            return LoggerFactory.getLogger(String.format("(%s) %s", name, getClassName(clazz)));
        }
    }

    /**
     * Returns a class name suitable for using in logs.
     *
     * <p>At the moment this results in a class name of just e.g. "{@code LoggingUtils}".
     */
    private static String getClassName(Class<?> clazz) {
        return clazz.getSimpleName();
    }
}
