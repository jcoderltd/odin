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

import io.jcoder.odin.InjectionContext;

/**
 *
 * @author Camilo Gonzalez
 */
public abstract class NullableInjectableReference<T> implements InjectableReference<T> {

    private final boolean nullable;

    public NullableInjectableReference(boolean nullable) {
        this.nullable = nullable;
    }

    @Override
    public final T get(InjectionContext context) {
        final T instance = doGet(context);
        if (instance == null && !nullable) {
            throw new IllegalStateException("No instance found for: " + getRegistrations(context) + " of " + getInjectableType());
        }
        return instance;
    }

    @Override
    public boolean isNullable() {
        return nullable;
    }

    protected abstract T doGet(InjectionContext context);

}
