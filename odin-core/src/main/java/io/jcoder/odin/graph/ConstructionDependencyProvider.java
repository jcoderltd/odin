/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import io.jcoder.odin.InjectionContext;
import io.jcoder.odin.function.ConstructionFunction;
import io.jcoder.odin.reference.InjectableReference;
import io.jcoder.odin.registration.InjectionRegistration;

/**
 * A {@link DependencyProvider} that returns the dependencies required for construction of an object.
 *
 * @author Camilo Gonzalez
 */
public class ConstructionDependencyProvider implements DependencyProvider {

    @Override
    public Collection<InjectionRegistration<?>> dependencies(InjectionContext context, InjectionRegistration<?> registration) {
        if (registration.isProvided()) {
            return Collections.emptyList();
        }

        List<InjectionRegistration<?>> dependencies = new ArrayList<>();
        ConstructionFunction<?> constructor = registration.getConstructor();
        if (constructor != null) {
            int idx = 0;
            for (final InjectableReference<?> paramRef : constructor.parameters()) {
                for (final InjectionRegistration<?> reg : paramRef.getRegistrations(context)) {
                    if (!paramRef.isNullable() && reg == null) {
                        throw new IllegalStateException("No registration found for constructor parameter " + idx + " of "
                                + registration.getRegisteredClass() + " of type " + paramRef.getInjectableType().getName());
                    }
                    dependencies.add(reg);
                }
                idx++;
            }
        }

        return dependencies;
    }

}
