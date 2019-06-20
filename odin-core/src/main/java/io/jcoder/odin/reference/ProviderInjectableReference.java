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

import javax.inject.Provider;

import io.jcoder.odin.InjectionContext;
import io.jcoder.odin.registration.InjectionRegistration;

/**
 *
 * @author Camilo Gonzalez
 */
@SuppressWarnings("rawtypes")
public class ProviderInjectableReference<T> implements InjectableReference<Provider<?>> {

    private final InjectableReference<T> delegate;

    public ProviderInjectableReference(InjectableReference<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<InjectionRegistration<Provider<?>>> getRegistrations(InjectionContext context) {
        return Collections.singletonList(context.getProviderRegistration(delegate.getRegistrations(context).get(0)));
    }

    @Override
    public Class<Provider> getInjectableType() {
        return Provider.class;
    }

    @Override
    public Provider<T> get(InjectionContext context) {
        return new Provider<T>() {
            @Override
            public T get() {
                return delegate.get(context);
            }
        };
    }

    @Override
    public boolean isNullable() {
        return false;
    }

}
