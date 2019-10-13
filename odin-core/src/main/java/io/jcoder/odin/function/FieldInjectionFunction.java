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

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.util.Optional;

import io.jcoder.odin.InjectionContext;
import io.jcoder.odin.InjectionFunctionException;
import io.jcoder.odin.reference.InjectableReference;
import io.jcoder.odin.reflection.MemberFinder;

/**
 *
 * @author Camilo Gonzalez
 */
public class FieldInjectionFunction<T> implements InjectionFunction<T> {

    private final Class<? super T> type;

    private final Field field;

    private final InjectableReference<?> parameterReference;

    public FieldInjectionFunction(Class<? super T> type, String fieldName, InjectableReference<?> parameterReference)
            throws NoSuchFieldException {

        this.type = type;
        this.parameterReference = parameterReference;

        this.field = MemberFinder.getField(type, fieldName);
        this.field.setAccessible(true);
    }

    @Override
    public void apply(InjectionContext context, T injectionReceiver) {
        Object value = this.parameterReference.get(context);

        try {
            field.set(injectionReceiver, value);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new InjectionFunctionException(this, "Field " + field.getName() + " of class " + type.getName(), e);
        }
    }

    @Override
    public Optional<Member> member() {
        return Optional.of(field);
    }

}
