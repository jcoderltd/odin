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
package io.jcoder.odin.reference;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Supplier;

import io.jcoder.odin.InjectionContext;
import io.jcoder.odin.base.Preconditions;
import io.jcoder.odin.registration.InjectionRegistration;

/**
 *
 * @author Camilo Gonzalez
 */
public class TypedMultiInjectableReference<T> extends NullableInjectableReference<T> {

    private final Class<T> referencedClass;

    private final Class<?> genericType;

    private final Supplier<T> collectionSupplier;

    public TypedMultiInjectableReference(Class<T> referencedClass) {
        this(referencedClass, null);
    }

    public TypedMultiInjectableReference(Class<T> referencedClass, Class<?> genericType) {
        this(referencedClass, genericType, defaultSupplierFor(referencedClass), false);
    }

    public TypedMultiInjectableReference(Class<T> referencedClass, Class<?> genericType, Supplier<T> collectionSupplier,
            boolean nullable) {
        super(nullable);
        boolean isArray = referencedClass.isArray();
        boolean isCollection = Collection.class.isAssignableFrom(referencedClass);

        if (!isArray) {
            Preconditions.verifyArgumentCondition(isCollection,
                    "The provided class must be a Collection or an Array to be able to inject multiple instances");
            Preconditions.verifyArgumentCondition(isCollection && genericType != null,
                    "A generic type must be provided for Collection types");
            Preconditions.verifyArgumentCondition(isCollection && collectionSupplier != null,
                    "A supplier must be provided for Collection types");
        }

        this.referencedClass = referencedClass;
        this.genericType = genericType;
        this.collectionSupplier = collectionSupplier;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public T doGet(InjectionContext context) {
        if (Collection.class.isAssignableFrom(referencedClass)) {
            List<?> objectList = context.getMulti(genericType);

            T collection = collectionSupplier.get();
            if (collection == null) {
                throw new IllegalStateException(
                        "Couldn't create instance of collection type: " + referencedClass + " using the current supplier");
            }

            ((Collection) collection).addAll(objectList);

            return collection;
        } else if (referencedClass.isArray()) {
            if (!context.exists(referencedClass)) {
                return (T) context.getMulti(referencedClass.getComponentType());
            }
        }
        return context.get(referencedClass);
    }

    @Override
    public List<InjectionRegistration<T>> getRegistrations(InjectionContext context) {
        if (Collection.class.isAssignableFrom(referencedClass)) {
            return context.getMultiRegistration(referencedClass);
        } else if (referencedClass.isArray()) {
            if (!context.exists(referencedClass)) {
                return context.getMultiRegistration(referencedClass);
            }
        }
        return Collections.singletonList(context.getRegistration(referencedClass));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static <E> Supplier<E> defaultSupplierFor(Class<E> referencedClass) {
        if (!Collection.class.isAssignableFrom(referencedClass)) {
            return null;
        }

        if (referencedClass.equals(List.class)) {
            return () -> ((E) new ArrayList());
        } else if (referencedClass.equals(Set.class)) {
            return () -> ((E) new HashSet());
        } else if (referencedClass.equals(SortedSet.class)) {
            return () -> ((E) new TreeSet());
        }

        if (!referencedClass.isInterface() && !Modifier.isAbstract(referencedClass.getModifiers())) {
            return () -> {
                try {
                    return (referencedClass.newInstance());
                } catch (InstantiationException | IllegalAccessException e) {
                    return null;
                }
            };
        }

        return () -> null;
    }

    @Override
    public boolean isNullable() {
        return false;
    }

    @Override
    public Class<T> getInjectableType() {
        return referencedClass;
    }

}
