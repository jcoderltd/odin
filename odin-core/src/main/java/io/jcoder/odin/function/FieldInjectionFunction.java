/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin.function;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.util.Optional;

import io.jcoder.odin.InjectionContext;
import io.jcoder.odin.InjectionFunctionException;
import io.jcoder.odin.reference.InjectableReference;
import io.jcoder.odin.reflection.MemberFinder;

/**
 *
 * @author Camilo Gonzalez
 */
public class FieldInjectionFunction<T> implements InjectionFunction<T> {

    private final Class<? super T> type;

    private final Field field;

    private final InjectableReference<?> parameterReference;

    public FieldInjectionFunction(Class<? super T> type, String fieldName, InjectableReference<?> parameterReference)
            throws NoSuchFieldException {

        this.type = type;
        this.parameterReference = parameterReference;

        this.field = MemberFinder.getField(type, fieldName);
        this.field.setAccessible(true);
    }

    @Override
    public void apply(InjectionContext context, T injectionReceiver) {
        Object value = this.parameterReference.get(context);

        try {
            field.set(injectionReceiver, value);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new InjectionFunctionException(this, "Field " + field.getName() + " of class " + type.getName(), e);
        }
    }

    @Override
    public Optional<Member> member() {
        return Optional.of(field);
    }

}
