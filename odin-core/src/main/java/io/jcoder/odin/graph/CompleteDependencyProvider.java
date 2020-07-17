/*
 *  Copyright 2020 JCoder Ltd.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.jcoder.odin.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.inject.Provider;

import io.jcoder.odin.InjectionContext;
import io.jcoder.odin.reference.InjectableReference;
import io.jcoder.odin.reference.ProviderInjectableReference;
import io.jcoder.odin.registration.InjectionRegistration;

/**
 * A {@link DependencyProvider} that returns all the dependencies that an object has.
 * 
 * <p>
 * In contrast to the {@link ConstructionDependencyProvider} that only returns the dependencies required to construct an
 * object, this includes all dependencies even those injected after object construction (like setter-based injection)
 * and lazily injected dependencies via {@link Provider} instances.
 * </p>
 *
 * @author Camilo Gonzalez
 */
public class CompleteDependencyProvider implements DependencyProvider {

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Collection<InjectionRegistration<?>> dependencies(InjectionContext context, InjectionRegistration<?> registration) {
        if (registration.isProvided()) {
            return Collections.emptyList();
        }

        List<InjectionRegistration<?>> dependencies = new ArrayList<>();
        List<InjectableReference<?>> dependencyRefs = registration.dependencies();
        int idx = 0;
        for (InjectableReference<?> paramRef : dependencyRefs) {
            for (InjectionRegistration<?> reg : paramRef.getRegistrations(context)) {
                if (!paramRef.isNullable() && reg == null) {
                    throw new IllegalStateException("No registration found for constructor parameter " + idx + " of "
                            + registration.getRegisteredClass() + " of type " + paramRef.getInjectableType().getName());
                }
                dependencies.add(reg);
            }
            
            if(paramRef instanceof ProviderInjectableReference) {
                ProviderInjectableReference providerRef = (ProviderInjectableReference) paramRef;
                List<InjectionRegistration<?>> registrations = providerRef.getDelegate().getRegistrations(context);
                for (InjectionRegistration<?> reg : registrations) {
                    dependencies.add(reg);
                }
            }
            
            idx++;
        }

        return dependencies;
    }

}
