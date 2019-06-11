/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin.graph;

import java.util.Collection;

import io.jcoder.odin.InjectionContext;
import io.jcoder.odin.registration.InjectionRegistration;

/**
 *
 * @author Camilo Gonzalez
 */
@FunctionalInterface
public interface DependencyProvider {

    /**
     * Provides a collection of dependencies for the given registration.
     *
     * @param context
     *            the {@link InjectionContext} linked to the dependencies
     * @param registration
     *            the registration to analyze.
     * @return a collection of dependencies for the given registration. If no dependencies exists for this registration,
     *         then an empty collection must be returned.
     */
    public Collection<InjectionRegistration<?>> dependencies(InjectionContext context, InjectionRegistration<?> registration);

}
