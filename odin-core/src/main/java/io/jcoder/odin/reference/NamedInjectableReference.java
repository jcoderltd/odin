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
public class NamedInjectableReference<T> extends NullableInjectableReference<T> {

    private final String name;

    private final Class<T> referencedClass;

    public NamedInjectableReference(String name, Class<T> referencedClass) {
        this(name, referencedClass, false);
    }

    public NamedInjectableReference(String name, Class<T> referencedClass, boolean nullable) {
        super(nullable);
        this.name = name;
        this.referencedClass = referencedClass;
    }

    @Override
    public T doGet(InjectionContext context) {
        return context.getNamed(referencedClass, name);
    }

    @Override
    public List<InjectionRegistration<T>> getRegistrations(InjectionContext context) {
        return Collections.singletonList(context.getNamedRegistration(referencedClass, name));
    }

    @Override
    public Class<T> getInjectableType() {
        return referencedClass;
    }

}
