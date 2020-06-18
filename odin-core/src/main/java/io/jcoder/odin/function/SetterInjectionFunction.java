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
import java.util.Collections;
import java.util.Optional;

import io.jcoder.odin.InjectionContext;
import io.jcoder.odin.base.Preconditions;
import io.jcoder.odin.reference.InjectableReference;

/**
 *
 * @author Camilo Gonzalez
 */
public final class SetterInjectionFunction<T, O> implements InjectionFunction<T> {
    private final SetterFunction<T, O> setter;

    private final InjectableReference<O> referenceToInject;

    public SetterInjectionFunction(Class<T> classToInvoke, SetterFunction<T, O> setter, InjectableReference<O> referenceToInject) {

        Preconditions.verifyNotNull(classToInvoke, "The provided class to invoke must not be null");
        Preconditions.verifyNotNull(setter, "The provided setter must not be null");
        Preconditions.verifyNotNull(referenceToInject, "The provided reference to inject must not be null");

        this.setter = setter;
        this.referenceToInject = referenceToInject;
    }

    @Override
    public void apply(InjectionContext context, T instance) {
        setter.set(instance, referenceToInject.get(context));
    }

    @Override
    public Collection<? extends InjectableReference<?>> dependencies() {
        return Collections.singleton(referenceToInject);
    }

    @Override
    public Optional<Member> member() {
        return Optional.empty();
    }

}