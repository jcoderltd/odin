/*
 * Copyright 2018 - JCoder Ltd
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
public class QualifiedInjectableReference<T> extends NullableInjectableReference<T> {

    private final String qualifierName;

    private final Class<T> referencedClass;

    public QualifiedInjectableReference(String qualifierName, Class<T> referencedClass) {
        this(qualifierName, referencedClass, false);
    }

    public QualifiedInjectableReference(String qualifierName, Class<T> referencedClass, boolean nullable) {
        super(nullable);
        this.qualifierName = qualifierName;
        this.referencedClass = referencedClass;
    }

    @Override
    public T doGet(InjectionContext context) {
        return context.getWithQualifier(referencedClass, qualifierName);
    }

    @Override
    public List<InjectionRegistration<T>> getRegistrations(InjectionContext context) {
        return Collections.singletonList(context.getQualifiedRegistration(referencedClass, qualifierName));
    }

    @Override
    public Class<T> getInjectableType() {
        return referencedClass;
    }

}
