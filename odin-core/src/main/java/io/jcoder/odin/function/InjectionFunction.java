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
package io.jcoder.odin.function;

import java.lang.reflect.Member;
import java.util.Collection;
import java.util.Optional;

import io.jcoder.odin.InjectionContext;
import io.jcoder.odin.reference.InjectableReference;

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

    /**
     * Provides the {@link InjectableReference} that make up the dependencies of this {@link InjectionFunction}
     */
    Collection<? extends InjectableReference<?>> dependencies();
}