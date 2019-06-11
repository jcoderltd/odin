/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin.function;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import io.jcoder.odin.InjectionContext;
import io.jcoder.odin.reference.InjectableReference;

/**
 * A Construction function that is responsible of creating a new instance given an {@link InjectionContext}.
 * 
 * <p>
 * Implementations of this class should take the required dependencies for the construction of the instance from the
 * provided {@link InjectionContext}
 *
 * @author Camilo Gonzalez
 */
public interface ConstructionFunction<T> {
    T newObject(InjectionContext context)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException;

    List<InjectableReference<?>> parameters();
}
