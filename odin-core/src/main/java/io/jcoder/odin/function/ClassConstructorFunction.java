/*
 * Copyright 2018 - JCoder Ltd
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

import com.google.common.base.Preconditions;

import io.jcoder.odin.InjectionContext;
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
        Preconditions.checkNotNull(constructorReference, "The constructor reference must not be null");
        this.constructorReference = constructorReference;
        this.parameterReferences = Stream.of(constructorReference.getParameterTypes())
                .map(paramType -> new TypedInjectableReference<>(paramType))
                .collect(Collectors.toList());
    }

    public ClassConstructorFunction(final Constructor<T> constructorReference, final Class<?>[] collectionGenericTypes) {
        Preconditions.checkNotNull(constructorReference, "The constructor reference must not be null");
        Preconditions.checkNotNull(collectionGenericTypes, "The collection generic types must not be null");

        this.constructorReference = constructorReference;
        this.parameterReferences = IntStream.range(0, constructorReference.getParameterCount())
                .mapToObj(idx -> {
                    final Class<?> paramType = constructorReference.getParameterTypes()[idx];
                    final Class<?> genericType = collectionGenericTypes[idx];
                    if (Collection.class.isAssignableFrom(paramType)) {
                        Preconditions.checkNotNull(genericType, "The generic type for parameter " + idx + " must be specified");
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
        Preconditions.checkNotNull(constructorReference, "The constructor reference must not be null");
        Preconditions.checkNotNull(parameterReferences, "The parameter references must not be null");
        Preconditions.checkArgument(constructorReference.getParameterCount() == parameterReferences.size(),
                "The size of the parameterReferences array must be equal to the number of parameters of the constructor");

        int idx = 0;
        for (final InjectableReference<?> paramRef : parameterReferences) {
            Preconditions.checkNotNull(paramRef, "The parameter reference for parameter " + idx + " must not be null");
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
    public List<InjectableReference<?>> parameters() {
        return new ArrayList<>(parameterReferences);
    }
}
