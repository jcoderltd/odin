/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin.annotation.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Stream;

import javax.inject.Named;
import javax.inject.Qualifier;

import io.jcoder.odin.reference.InjectableReference;
import io.jcoder.odin.reference.NamedInjectableReference;
import io.jcoder.odin.reference.TypedInjectableReference;
import io.jcoder.odin.reference.TypedMultiInjectableReference;

/**
 *
 * @author Camilo Gonzalez
 */
public class AnnotationUtils {

    public static InjectableReference<?>[] processParameterReferences(final Parameter[] parameters) {
        return Stream.of(parameters).map(param -> {
            final Class<?> qualifierType = qualifierFromAnnotations(param.getAnnotations());

            final Class<?> paramType = param.getType();
            if (qualifierType != null && !Named.class.equals(qualifierType)) {
                return new NamedInjectableReference<>(qualifierType.getSimpleName(), paramType);
            } else {
                final Named namedAnnotation = param.getAnnotation(Named.class);
                if (namedAnnotation != null) {
                    return new NamedInjectableReference<>(namedAnnotation.value(), paramType);
                }
            }

            if (supportsMulti(paramType)) {
                return new TypedMultiInjectableReference<>(paramType);
            }

            return new TypedInjectableReference<>(paramType);
        }).toArray(InjectableReference[]::new);
    }

    public static boolean supportsMulti(Class<?> paramType) {
        return paramType.isArray() || paramType.equals(List.class) || paramType.equals(Set.class) || paramType.equals(SortedSet.class);
    }

    public static Class<?> qualifierFromAnnotations(Annotation[] annotations) {
        Class<?> qualifierType = null;
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().isAnnotationPresent(Qualifier.class)) {
                if (qualifierType != null) {
                    throw new IllegalArgumentException("There is more than one qualifier defined");
                }
                qualifierType = annotation.annotationType();
            }
        }
        return qualifierType;
    }

}
