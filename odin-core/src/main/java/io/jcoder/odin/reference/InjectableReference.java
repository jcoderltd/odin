/**
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
 * <p>
 * {@link InjectableReference} objects are usually used in {@link InjectionFunction} or {@link ConstructionFunction} as
 * a way of representing what to inject.
 *
 * @author Camilo Gonzalez
 */
public interface InjectableReference<T> {

    List<InjectionRegistration<T>> getRegistrations(InjectionContext context);

    Class<?> getInjectableType();

    T get(InjectionContext context);

    boolean isNullable();

}
