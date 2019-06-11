/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin.function;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import com.google.common.base.Preconditions;

import io.jcoder.odin.ConstructionFunctionException;
import io.jcoder.odin.InjectionContext;
import io.jcoder.odin.reference.InjectableReference;

/**
 *
 * @author Camilo Gonzalez
 */
public class FactoryMethodConstructionFunction<T> implements ConstructionFunction<T> {

    private final Class<?> factoryType;

    private final Method method;

    private final List<InjectableReference<?>> parameterReferences;

    private final Class<?>[] parameterTypes;

    public FactoryMethodConstructionFunction(final Class<T> typeToConstruct, final Class<?> factoryType, final String staticMethodName,
            final List<InjectableReference<?>> parameterReferences)
            throws NoSuchMethodException {
        Preconditions.checkNotNull(typeToConstruct, "The type to construct must not be null");
        Preconditions.checkNotNull(factoryType, "The factory type must not be null");
        Preconditions.checkNotNull(staticMethodName, "The static method name must not be null");

        this.factoryType = factoryType;
        this.parameterReferences = parameterReferences;

        this.parameterTypes = parameterReferences.stream().map(ref -> ref.getInjectableType()).toArray(Class<?>[]::new);
        this.method = this.factoryType.getMethod(staticMethodName, parameterTypes);
        this.method.setAccessible(true);

        Preconditions.checkArgument(Modifier.isStatic(this.method.getModifiers()), "The provided method must be static");
        Preconditions.checkArgument(typeToConstruct.isAssignableFrom(this.method.getReturnType()),
                "Cannot assign " + typeToConstruct.getName() + " from the method return type: " + this.method.getReturnType().getName());
    }

    @SuppressWarnings("unchecked")
    @Override
    public T newObject(final InjectionContext context)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        final Object[] args = this.parameterReferences.stream()
                .map(paramRef -> paramRef.get(context))
                .toArray(Object[]::new);

        try {
            return (T) method.invoke(null, args);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new ConstructionFunctionException(this, "Method " + method.getName() + " of class " + factoryType.getName(), e);
        }
    }

    @Override
    public List<InjectableReference<?>> parameters() {
        return parameterReferences;
    }

}
