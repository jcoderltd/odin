/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin.function;

import java.lang.reflect.Field;

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

    public FieldInjectionFunction(final Class<? super T> type, final String fieldName, final InjectableReference<?> parameterReference)
            throws NoSuchFieldException {

        this.type = type;
        this.parameterReference = parameterReference;

        this.field = MemberFinder.getField(type, fieldName);
        this.field.setAccessible(true);
    }

    @Override
    public void apply(final InjectionContext context, final T injectionReceiver) {
        final Object value = this.parameterReference.get(context);

        try {
            field.set(injectionReceiver, value);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new InjectionFunctionException(this, "Field " + field.getName() + " of class " + type.getName(), e);
        }
    }

    @Override
    public int priority() {
        return 1;
    }

}
