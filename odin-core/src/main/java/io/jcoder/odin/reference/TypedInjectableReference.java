/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin.reference;

import java.util.Collections;
import java.util.List;

import io.jcoder.odin.InjectionContext;
import io.jcoder.odin.registration.InjectionRegistration;

/**
 *
 * @author Camilo Gonzalez
 */
public class TypedInjectableReference<T> extends NullableInjectableReference<T> {

    private final Class<T> referencedClass;

    public TypedInjectableReference(Class<T> referencedClass) {
        this(referencedClass, false);
    }

    public TypedInjectableReference(Class<T> referencedClass, boolean nullable) {
        super(nullable);
        this.referencedClass = referencedClass;
    }

    @Override
    public T doGet(InjectionContext context) {
        return context.get(referencedClass);
    }

    @Override
    public List<InjectionRegistration<T>> getRegistrations(InjectionContext context) {
        return Collections.singletonList(context.getRegistration(referencedClass));
    }

    @Override
    public Class<T> getInjectableType() {
        return referencedClass;
    }
}
