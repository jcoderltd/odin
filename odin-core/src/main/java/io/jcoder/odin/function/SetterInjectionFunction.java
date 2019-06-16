/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin.function;

import java.lang.reflect.Member;
import java.util.Optional;

import com.google.common.base.Preconditions;

import io.jcoder.odin.InjectionContext;
import io.jcoder.odin.reference.InjectableReference;

/**
 *
 * @author Camilo Gonzalez
 */
public final class SetterInjectionFunction<T, O> implements InjectionFunction<T> {
    private final SetterFunction<T, O> setter;

    private final InjectableReference<O> referenceToInject;

    public SetterInjectionFunction(Class<T> classToInvoke, SetterFunction<T, O> setter, InjectableReference<O> referenceToInject) {

        Preconditions.checkNotNull(classToInvoke, "The provided class to invoke must not be null");
        Preconditions.checkNotNull(setter, "The provided setter must not be null");
        Preconditions.checkNotNull(referenceToInject, "The provided reference to inject must not be null");

        this.setter = setter;
        this.referenceToInject = referenceToInject;
    }

    @Override
    public void apply(InjectionContext context, T instance) {
        setter.set(instance, referenceToInject.get(context));
    }

    @Override
    public Optional<Member> member() {
        return Optional.empty();
    }

}