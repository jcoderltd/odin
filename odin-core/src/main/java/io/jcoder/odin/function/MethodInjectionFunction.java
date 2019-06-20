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
package io.jcoder.odin.function;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import io.jcoder.odin.InjectionContext;
import io.jcoder.odin.InjectionFunctionException;
import io.jcoder.odin.reference.InjectableReference;
import io.jcoder.odin.reflection.MemberFinder;

/**
 *
 * @author Camilo Gonzalez
 */
public class MethodInjectionFunction<T> implements InjectionFunction<T> {

    private final Class<? super T> type;

    private final Method method;

    private final List<InjectableReference<?>> parameterReferences;

    private final Class<?>[] parameterTypes;

    public MethodInjectionFunction(Class<? super T> type, String methodName, List<InjectableReference<?>> parameterReferences)
            throws NoSuchMethodException {

        this.type = type;
        this.parameterReferences = parameterReferences;

        this.parameterTypes = parameterReferences.stream().map(ref -> ref.getInjectableType()).toArray(Class<?>[]::new);
        this.method = MemberFinder.getMethod(type, methodName, parameterTypes);
        this.method.setAccessible(true);
    }

    @Override
    public void apply(InjectionContext context, T injectionReceiver) {
        Object[] args = this.parameterReferences.stream()
                .map(paramRef -> paramRef.get(context))
                .toArray(Object[]::new);

        try {
            method.invoke(injectionReceiver, args);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new InjectionFunctionException(this, "Method " + method.getName() + " of class " + type.getName(), e);
        }
    }

    @Override
    public Optional<Member> member() {
        return Optional.of(method);
    }

}
