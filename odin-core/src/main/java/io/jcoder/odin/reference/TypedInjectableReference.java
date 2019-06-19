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

import java.util.Collections;
import java.util.List;

import io.jcoder.odin.InjectionContext;
import io.jcoder.odin.registration.InjectionRegistration;

/**
 *
 * @author Camilo Gonzalez
 */
public class TypedInjectableReference<T> extends NullableInjectableReference<T> {

    private final Class<T> referencedClass;

    public TypedInjectableReference(Class<T> referencedClass) {
        this(referencedClass, false);
    }

    public TypedInjectableReference(Class<T> referencedClass, boolean nullable) {
        super(nullable);
        this.referencedClass = referencedClass;
    }

    @Override
    public T doGet(InjectionContext context) {
        return context.get(referencedClass);
    }

    @Override
    public List<InjectionRegistration<T>> getRegistrations(InjectionContext context) {
        return Collections.singletonList(context.getRegistration(referencedClass));
    }

    @Override
    public Class<T> getInjectableType() {
        return referencedClass;
    }
}
