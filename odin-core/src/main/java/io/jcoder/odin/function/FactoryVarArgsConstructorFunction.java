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

import java.util.ArrayList;
import java.util.List;

import io.jcoder.odin.InjectionContext;
import io.jcoder.odin.base.Preconditions;
import io.jcoder.odin.reference.InjectableReference;

/**
 *
 * @author Camilo Gonzalez
 */
public class FactoryVarArgsConstructorFunction<T> implements ConstructionFunction<T> {

    private final FactoryVarArgsFunction<T> constructorFunction;

    private final List<InjectableReference<?>> parameterReferences;

    public FactoryVarArgsConstructorFunction(final FactoryVarArgsFunction<T> constructorFunction,
            final List<InjectableReference<?>> parameterReferences) {

        Preconditions.verifyNotNull(constructorFunction, "The constructor function must not be null");
        Preconditions.verifyNotNull(parameterReferences, "The parameter references must not be null");

        this.constructorFunction = constructorFunction;
        this.parameterReferences = new ArrayList<>(parameterReferences);
    }

    @Override
    public T newObject(final InjectionContext context) {

        final Object[] args = this.parameterReferences.stream()
                .map(paramRef -> paramRef.get(context))
                .toArray(Object[]::new);

        return constructorFunction.apply(args);
    }

    @Override
    public List<InjectableReference<?>> parameters() {
        return new ArrayList<>(parameterReferences);
    }
}
