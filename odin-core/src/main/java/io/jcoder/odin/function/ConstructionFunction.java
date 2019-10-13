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
