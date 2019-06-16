/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin.function;

import java.lang.reflect.Member;
import java.util.Optional;

import io.jcoder.odin.InjectionContext;

/**
 * Represents an Injection function that is responsible of injecting an object or set of objects into a provided
 * instance given an {@link InjectionContext}.
 *
 * @author Camilo Gonzalez
 */
public interface InjectionFunction<T> {
    /**
     * Applies the object injection into the injectionReceiver.
     *
     * @param context
     * @param injectionReceiver
     */
    void apply(InjectionContext context, T injectionReceiver);

    /**
     * The class member that'll be injected by this {@link InjectionFunction}
     * 
     * @return
     */
    Optional<Member> member();
}