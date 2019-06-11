/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin.function;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import io.jcoder.odin.InjectionContext;
import io.jcoder.odin.InjectionFunctionException;
import io.jcoder.odin.reference.InjectableReference;
import io.jcoder.odin.reflection.MemberFinder;

/**
 *
 * @author Camilo Gonzalez
 */
public class MethodInjectionFunction<T> implements InjectionFunction<T> {

    private final Class<T> type;

    private final Method method;

    private final List<InjectableReference<?>> parameterReferences;

    private final Class<?>[] parameterTypes;

    public MethodInjectionFunction(final Class<T> type, final String methodName, final List<InjectableReference<?>> parameterReferences)
            throws NoSuchMethodException {
        this.type = type;
        this.parameterReferences = parameterReferences;

        this.parameterTypes = parameterReferences.stream().map(ref -> ref.getInjectableType()).toArray(Class<?>[]::new);
        this.method = MemberFinder.getMethod(type, methodName, parameterTypes);
        this.method.setAccessible(true);
    }

    @Override
    public void apply(final InjectionContext context, final T injectionReceiver) {
        final Object[] args = this.parameterReferences.stream()
                .map(paramRef -> paramRef.get(context))
                .toArray(Object[]::new);

        try {
            method.invoke(injectionReceiver, args);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new InjectionFunctionException(this, "Method " + method.getName() + " of class " + type.getName(), e);
        }
    }

    @Override
    public int priority() {
        return 3;
    }

}
