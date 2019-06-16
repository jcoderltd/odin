/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin.function;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

import io.jcoder.odin.InjectionContext;
import io.jcoder.odin.reference.InjectableReference;

/**
 *
 * @author Camilo Gonzalez
 */
public class FactoryVarArgsConstructorFunction<T> implements ConstructionFunction<T> {

    private final FactoryVarArgsFunction<T> constructorFunction;

    private final List<InjectableReference<?>> parameterReferences;

    public FactoryVarArgsConstructorFunction(final FactoryVarArgsFunction<T> constructorFunction,
            final List<InjectableReference<?>> parameterReferences) {

        Preconditions.checkNotNull(constructorFunction, "The constructor function must not be null");
        Preconditions.checkNotNull(parameterReferences, "The parameter references must not be null");

        this.constructorFunction = constructorFunction;
        this.parameterReferences = new ArrayList<>(parameterReferences);
    }

    @Override
    public T newObject(final InjectionContext context) {

        final Object[] args = this.parameterReferences.stream()
                .map(paramRef -> paramRef.get(context))
                .toArray(Object[]::new);

        return constructorFunction.apply(args);
    }

    @Override
    public List<InjectableReference<?>> parameters() {
        return new ArrayList<>(parameterReferences);
    }
}
