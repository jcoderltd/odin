/*
 *  Copyright 2019 JCoder Ltd.
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
