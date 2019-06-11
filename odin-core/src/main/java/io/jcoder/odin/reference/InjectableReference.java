/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin.reference;

import java.util.List;

import io.jcoder.odin.InjectionContext;
import io.jcoder.odin.function.ConstructionFunction;
import io.jcoder.odin.function.InjectionFunction;
import io.jcoder.odin.registration.InjectionRegistration;

/**
 * A reference to an object or collection of objects that can be retrieved from an {@link InjectionContext} and be
 * injected.
 *
 * <p>{@link InjectableReference} objects are usually used in {@link InjectionFunction} or {@link ConstructionFunction} as
 * a way of representing what to inject.
 *
 * @author Camilo Gonzalez
 */
public interface InjectableReference<T> {

    List<InjectionRegistration<T>> getRegistrations(InjectionContext context);

    Class<T> getInjectableType();

    T get(InjectionContext context);

    boolean isNullable();

}
