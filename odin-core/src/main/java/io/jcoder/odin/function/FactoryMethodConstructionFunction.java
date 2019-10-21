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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import io.jcoder.odin.ConstructionFunctionException;
import io.jcoder.odin.InjectionContext;
import io.jcoder.odin.base.Preconditions;
import io.jcoder.odin.reference.InjectableReference;

/**
 *
 * @author Camilo Gonzalez
 */
public class FactoryMethodConstructionFunction<T> implements ConstructionFunction<T> {

    private final InjectableReference<?> factoryReference;

    private final Method method;

    private final List<InjectableReference<?>> parameterReferences;

    private final List<InjectableReference<?>> dependencies;

    private final Class<?>[] parameterTypes;

    public FactoryMethodConstructionFunction(final Class<T> typeToConstruct, final InjectableReference<?> factoryReference,
            final String methodName, final List<InjectableReference<?>> parameterReferences) throws NoSuchMethodException {

        Preconditions.verifyNotNull(typeToConstruct, "The type to construct must not be null");
        Preconditions.verifyNotNull(factoryReference, "The factory reference must not be null");
        Preconditions.verifyNotNull(methodName, "The method name must not be null");

        this.factoryReference = factoryReference;
        this.parameterReferences = new ArrayList<>(parameterReferences);
        this.dependencies = new ArrayList<>(parameterReferences);
        this.dependencies.add(factoryReference);

        this.parameterTypes = parameterReferences.stream().map(ref -> ref.getInjectableType()).toArray(Class<?>[]::new);
        this.method = this.factoryReference.getInjectableType().getDeclaredMethod(methodName, parameterTypes);
        this.method.setAccessible(true);

        Preconditions.verifyArgumentCondition(typeToConstruct.isAssignableFrom(this.method.getReturnType()),
                "Cannot assign " + typeToConstruct.getName() + " from the method return type: " + this.method.getReturnType().getName());
    }

    @SuppressWarnings("unchecked")
    @Override
    public T newObject(final InjectionContext context)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        final Object factoryInstance = factoryReference.get(context);

        final Object[] args = this.parameterReferences.stream()
                .map(paramRef -> paramRef.get(context))
                .toArray(Object[]::new);

        try {
            return (T) method.invoke(factoryInstance, args);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new ConstructionFunctionException(this,
                    "Method " + method.getName() + " of class " + factoryReference.getInjectableType().getName(), e);
        }
    }

    @Override
    public List<InjectableReference<?>> dependencies() {
        return dependencies;
    }

}
