/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 *
 * @author Camilo Gonzalez
 */
public class MemberFinder {
    public static Field getField(Class<?> classToProcess, String fieldName) throws NoSuchFieldException {
        if (classToProcess == null) {
            throw new NoSuchFieldException("No field " + fieldName + " found");
        }

        try {
            return classToProcess.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            return getField(classToProcess.getSuperclass(), fieldName);
        }
    }

    public static Method getMethod(Class<?> classToProcess, String methodName, Class<?>... parameters) throws NoSuchMethodException {
        if (classToProcess == null) {
            throw new NoSuchMethodException("No method " + methodName + " found");
        }

        try {
            return classToProcess.getDeclaredMethod(methodName, parameters);
        } catch (NoSuchMethodException e) {
            return getMethod(classToProcess.getSuperclass(), methodName, parameters);
        }
    }
}
