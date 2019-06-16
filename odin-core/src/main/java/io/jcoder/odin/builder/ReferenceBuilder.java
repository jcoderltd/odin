/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin.builder;

import com.google.common.base.Preconditions;

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
        Preconditions.checkArgument(multi, "Only multi reference can be assigned a generic type. Have you invoked multi()?");

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
