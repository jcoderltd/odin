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
package io.jcoder.odin.builder;

import io.jcoder.odin.base.Preconditions;
import io.jcoder.odin.reference.InjectableReference;
import io.jcoder.odin.reference.NamedInjectableReference;
import io.jcoder.odin.reference.ProviderInjectableReference;
import io.jcoder.odin.reference.QualifiedInjectableReference;
import io.jcoder.odin.reference.TypedInjectableReference;
import io.jcoder.odin.reference.TypedMultiInjectableReference;

/**
 * Used to create {@link InjectableReference} instances.
 *
 * @author Camilo Gonzalez
 */
public class ReferenceBuilder<T> {

    private final Class<T> referencedType;

    private Class<?> genericType;

    private String name;

    private String qualifierName;

    private boolean multi;

    private boolean nullable;

    public ReferenceBuilder(final Class<T> type) {
        this.referencedType = type;
    }

    public static <E> ReferenceBuilder<E> paramOfType(final Class<E> parameterType) {
        return new ReferenceBuilder<>(parameterType);
    }

    public static <E> ReferenceBuilder<E> ofType(final Class<E> parameterType) {
        return new ReferenceBuilder<>(parameterType);
    }

    public ReferenceBuilder<T> named(final String name) {
        this.name = name;
        return this;
    }

    public ReferenceBuilder<T> qualifiedBy(final String qualifierName) {
        this.qualifierName = qualifierName;
        return this;
    }

    public ReferenceBuilder<T> multi() {
        this.multi = true;
        return this;
    }

    public ReferenceBuilder<T> single() {
        this.multi = false;
        this.genericType = null;
        return this;
    }

    public ReferenceBuilder<T> nullable() {
        this.nullable = true;
        return this;
    }

    public ReferenceBuilder<T> notNullable() {
        this.nullable = false;
        return this;
    }

    public ReferenceBuilder<T> ofGenericType(final Class<?> genericType) {
        Preconditions.verifyArgumentCondition(multi, "Only multi reference can be assigned a generic type. Have you invoked multi()?");

        this.genericType = genericType;
        return this;
    }

    public InjectableReference<T> build() {
        if (multi) {
            return new TypedMultiInjectableReference<>(referencedType, genericType);
        }

        if (name != null) {
            return new NamedInjectableReference<>(name, referencedType, nullable);
        }

        if (qualifierName != null) {
            return new QualifiedInjectableReference<>(qualifierName, referencedType, nullable);
        }

        return new TypedInjectableReference<>(referencedType, nullable);
    }

    public ProviderInjectableReference<T> asProvider() {
        InjectableReference<T> delegate = build();
        return new ProviderInjectableReference<>(delegate);
    }
}
