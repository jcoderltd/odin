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
package io.jcoder.odin.function;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import io.jcoder.odin.InjectionContext;
import io.jcoder.odin.base.Preconditions;
import io.jcoder.odin.reference.InjectableReference;
import io.jcoder.odin.reference.TypedInjectableReference;
import io.jcoder.odin.reference.TypedMultiInjectableReference;

/**
 *
 * @author Camilo Gonzalez
 */
public class ClassConstructorFunction<T> implements ConstructionFunction<T> {

    private final Constructor<T> constructorReference;

    private final List<InjectableReference<?>> parameterReferences;

    public ClassConstructorFunction(final Constructor<T> constructorReference) {
        Preconditions.verifyNotNull(constructorReference, "The constructor reference must not be null");
        this.constructorReference = constructorReference;
        this.parameterReferences = Stream.of(constructorReference.getParameterTypes())
                .map(paramType -> new TypedInjectableReference<>(paramType))
                .collect(Collectors.toList());
    }

    public ClassConstructorFunction(final Constructor<T> constructorReference, final Class<?>[] collectionGenericTypes) {
        Preconditions.verifyNotNull(constructorReference, "The constructor reference must not be null");
        Preconditions.verifyNotNull(collectionGenericTypes, "The collection generic types must not be null");

        this.constructorReference = constructorReference;
        this.parameterReferences = IntStream.range(0, constructorReference.getParameterCount())
                .mapToObj(idx -> {
                    final Class<?> paramType = constructorReference.getParameterTypes()[idx];
                    final Class<?> genericType = collectionGenericTypes[idx];
                    if (Collection.class.isAssignableFrom(paramType)) {
                        Preconditions.verifyNotNull(genericType, "The generic type for parameter " + idx + " must be specified");
                    }

                    if (genericType != null || paramType.isArray()) {
                        return new TypedMultiInjectableReference<>(paramType, genericType);
                    } else {
                        return new TypedInjectableReference<>(paramType);
                    }
                })
                .collect(Collectors.toList());
    }

    public ClassConstructorFunction(final Constructor<T> constructorReference, final List<InjectableReference<?>> parameterReferences) {
        Preconditions.verifyNotNull(constructorReference, "The constructor reference must not be null");
        Preconditions.verifyNotNull(parameterReferences, "The parameter references must not be null");
        Preconditions.verifyArgumentCondition(constructorReference.getParameterCount() == parameterReferences.size(),
                "The size of the parameterReferences array must be equal to the number of parameters of the constructor");

        int idx = 0;
        for (final InjectableReference<?> paramRef : parameterReferences) {
            Preconditions.verifyNotNull(paramRef, "The parameter reference for parameter " + idx + " must not be null");
            idx++;
        }

        this.constructorReference = constructorReference;
        this.constructorReference.setAccessible(true);
        this.parameterReferences = new ArrayList<>(parameterReferences);
    }

    @Override
    public T newObject(final InjectionContext context)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        final Object[] args = this.parameterReferences.stream()
                .map(paramRef -> paramRef.get(context))
                .toArray(Object[]::new);

        return constructorReference.newInstance(args);
    }

    @Override
    public List<InjectableReference<?>> dependencies() {
        return new ArrayList<>(parameterReferences);
    }
}
