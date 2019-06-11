/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin.reference;

import io.jcoder.odin.InjectionContext;

/**
 *
 * @author Camilo Gonzalez
 */
public abstract class NullableInjectableReference<T> implements InjectableReference<T> {

    private final boolean nullable;

    public NullableInjectableReference(boolean nullable) {
        this.nullable = nullable;
    }

    @Override
    public final T get(InjectionContext context) {
        final T instance = doGet(context);
        if (instance == null && !nullable) {
            throw new IllegalStateException("No instance found for: " + getRegistrations(context) + " of " + getInjectableType());
        }
        return instance;
    }

    @Override
    public boolean isNullable() {
        return nullable;
    }

    protected abstract T doGet(InjectionContext context);

}
