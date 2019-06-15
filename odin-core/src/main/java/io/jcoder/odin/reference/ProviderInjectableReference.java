/*
 * Copyright 2019 - JCoder Ltd
 */
package io.jcoder.odin.reference;

import java.util.Collections;
import java.util.List;

import javax.inject.Provider;

import io.jcoder.odin.InjectionContext;
import io.jcoder.odin.registration.InjectionRegistration;

/**
 * 
 * @author Camilo Gonzalez
 */
@SuppressWarnings("rawtypes")
public class ProviderInjectableReference<T> implements InjectableReference<Provider<?>> {

    private final InjectableReference<T> delegate;
    
    public ProviderInjectableReference(InjectableReference<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<InjectionRegistration<Provider<?>>> getRegistrations(InjectionContext context) {
        return Collections.singletonList(context.getProviderRegistration(delegate.getRegistrations(context).get(0)));
    }

    @Override
    public Class<Provider> getInjectableType() {
        return Provider.class;
    }

    @Override
    public Provider<T> get(InjectionContext context) {
        return new Provider<T>() {
            @Override
            public T get() {
                return delegate.get(context);
            }
        };
    }

    @Override
    public boolean isNullable() {
        return false;
    }

}
