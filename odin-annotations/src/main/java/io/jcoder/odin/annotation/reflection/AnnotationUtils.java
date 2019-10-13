/*
 *  Copyright 2019 JCoder Ltd.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.jcoder.odin.annotation.reflection;

import static io.jcoder.odin.builder.ReferenceBuilder.ofType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Stream;

import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Qualifier;

import io.jcoder.odin.builder.ReferenceBuilder;
import io.jcoder.odin.reference.InjectableReference;

/**
 *
 * @author Camilo Gonzalez
 */
public class AnnotationUtils {

    public static InjectableReference<?>[] processParameterReferences(final Parameter[] parameters) {
        return Stream.of(parameters).map(param -> {
            Annotation[] annotations = param.getAnnotations();
            Named namedAnnotation = param.getAnnotation(Named.class);
            Class<?> paramType = param.getType();
            boolean isProvider = paramType.equals(Provider.class);
            if (isProvider) {
                paramType = getProviderGenericType(param);
            }
            return buildInjectableReference(paramType, annotations, namedAnnotation, isProvider);
        }).toArray(InjectableReference[]::new);
    }

    public static InjectableReference<?> buildInjectableReference(Class<?> paramType, Annotation[] annotations, Named namedAnnotation,
            boolean isProvider) {

        ReferenceBuilder<?> refBuilder = ofType(paramType);
        Class<?> qualifierType = qualifierFromAnnotations(annotations);
        if (qualifierType != null && !Named.class.equals(qualifierType)) {
            refBuilder.qualifiedBy(qualifierType.getName());
        } else {
            if (namedAnnotation != null) {
                refBuilder.named(qualifiedNameFrom(namedAnnotation));
            }
        }

        if (supportsMulti(paramType)) {
            refBuilder.multi();
        }

        return isProvider ? refBuilder.asProvider() : refBuilder.build();
    }

    public static Class<?> getProviderGenericType(Parameter param) {
        Type type = param.getParameterizedType();
        if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getActualTypeArguments()[0];
        }
        throw new UnknownGenericTypeException("Can't handle type: " + type);
    }

    public static Class<?> getProviderGenericType(Field field) {
        Type type = field.getGenericType();
        if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getActualTypeArguments()[0];
        }
        throw new UnknownGenericTypeException("Can't handle type: " + type);
    }

    public static String qualifiedNameFrom(Named namedAnnotation) {
        return namedAnnotation.value();
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
