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

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.jcoder.odin.DefaultInjectionContext.UnscopedInstanceScope;
import io.jcoder.odin.InjectionContext;
import io.jcoder.odin.base.Preconditions;
import io.jcoder.odin.function.ClassConstructorFunction;
import io.jcoder.odin.function.ConstructionFunction;
import io.jcoder.odin.function.FactoryMethodConstructionFunction;
import io.jcoder.odin.function.StaticFactoryMethodConstructionFunction;
import io.jcoder.odin.function.FactoryVarArgsConstructorFunction;
import io.jcoder.odin.function.FactoryVarArgsFunction;
import io.jcoder.odin.function.FieldInjectionFunction;
import io.jcoder.odin.function.InjectionFunction;
import io.jcoder.odin.function.MethodInjectionFunction;
import io.jcoder.odin.function.PostConstructionFunction;
import io.jcoder.odin.function.PreDestroyFunction;
import io.jcoder.odin.function.SetterFunction;
import io.jcoder.odin.function.SetterInjectionFunction;
import io.jcoder.odin.reference.InjectableReference;
import io.jcoder.odin.reference.NamedInjectableReference;
import io.jcoder.odin.reference.TypedInjectableReference;
import io.jcoder.odin.reference.TypedMultiInjectableReference;
import io.jcoder.odin.registration.InjectionRegistration;
import io.jcoder.odin.scope.InstanceScope;
import io.jcoder.odin.scope.SingletonScope;

/**
 * Used to create {@link InjectionRegistration} objects that can be used to register classes or objects into an
 * {@link InjectionContext}.
 *
 * @author Camilo Gonzalez
 */
public class RegistrationBuilder<T> {
    private final Class<T> classToRegister;

    private final Optional<T> objectToRegister;

    private final List<InjectionFunction<T>> injectionFunctions = new ArrayList<>();

    private String name;

    private String qualifierName;

    private ConstructionFunction<T> constructor;

    private PostConstructionFunction<T> postConstructor;

    private PreDestroyFunction<T> preDestroy;

    private Class<? extends InstanceScope> scopeType;

    public RegistrationBuilder(Class<T> classToRegister) {
        Preconditions.verifyNotNull(classToRegister, "The provided class must not be null");
        this.name = classToRegister.getName();
        this.classToRegister = classToRegister;
        this.scopeType = UnscopedInstanceScope.class;
        this.objectToRegister = Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public RegistrationBuilder(T registeredObject) {
        Preconditions.verifyNotNull(registeredObject, "The provided object must not be null");
        this.objectToRegister = Optional.of(registeredObject);
        this.scopeType = SingletonScope.class;
        this.classToRegister = (Class<T>) registeredObject.getClass();
        this.name = this.classToRegister.getName();
    }

    public static <T> RegistrationBuilder<T> type(Class<T> classToRegister) {
        return new RegistrationBuilder<T>(classToRegister);
    }

    public static <T> RegistrationBuilder<T> singleton(Class<T> classToRegister) {
        return new RegistrationBuilder<T>(classToRegister).asSingleton();
    }

    public static <T> RegistrationBuilder<T> object(T objectToRegister) {
        return new RegistrationBuilder<T>(objectToRegister);
    }

    public RegistrationBuilder<T> withConstructor() throws NoSuchMethodException {
        if (this.objectToRegister.isPresent()) {
            throw new IllegalStateException("Can't define constructor injection when an object reference is already provided");
        }

        Constructor<?> constructorToUse = null;
        final Constructor<?>[] constructors = this.classToRegister.getDeclaredConstructors();
        if (constructors.length == 1) {
            constructorToUse = constructors[0];
        } else {
            constructorToUse = findUniquePublicConstructor(constructors);
        }

        if (constructorToUse == null) {
            throw new IllegalStateException(
                    "There is no public constructor or more than one has been defined for class: " + classToRegister.getName()
                            + ". Try specifying which constructor using withConstructor(Class<?> paramTypes...)");
        }

        constructorToUse.setAccessible(true);

        final Class<?>[] parameterTypes = constructorToUse.getParameterTypes();
        this.withConstructor(parameterTypes);

        return this;
    }

    public RegistrationBuilder<T> withConstructor(Class<?>... parameterTypes) throws NoSuchMethodException {
        if (constructor != null) {
            throw new IllegalStateException(
                    "A constructor reference has already been defined. Are you calling withConstructor or withFactory more than once?");
        }

        final Constructor<T> typedConstructor = this.classToRegister.getDeclaredConstructor(parameterTypes);
        this.constructor = new ClassConstructorFunction<>(typedConstructor);
        return this;
    }

    public RegistrationBuilder<T> withConstructor(InjectableReference<?>... parameterReferences) throws NoSuchMethodException {
        if (constructor != null) {
            throw new IllegalStateException(
                    "A constructor reference has already been defined. Are you calling withConstructor or withFactory more than once?");
        }
        final Class<?>[] parameterTypes = Stream.of(parameterReferences).map(ref -> ref.getInjectableType()).toArray(Class<?>[]::new);

        final Constructor<T> typedConstructor = this.classToRegister.getDeclaredConstructor(parameterTypes);
        this.constructor = new ClassConstructorFunction<>(typedConstructor, Arrays.asList(parameterReferences));
        return this;
    }

    public RegistrationBuilder<T> withConstructor(ReferenceBuilder<?>... parameterReferences) throws NoSuchMethodException {
        return this.withConstructor(Stream.of(parameterReferences).map(ReferenceBuilder::build).toArray(InjectableReference<?>[]::new));
    }

    public RegistrationBuilder<T> withFactory(FactoryVarArgsFunction<T> factory, Class<?>... parameterTypes) {
        if (constructor != null) {
            throw new IllegalStateException(
                    "A constructor reference has already been defined. Are you calling withConstructor or withFactory more than once?");
        }

        this.constructor = new FactoryVarArgsConstructorFunction<>(factory,
                Stream.of(parameterTypes).map(type -> new TypedInjectableReference<>(type)).collect(Collectors.toList()));
        return this;
    }

    public RegistrationBuilder<T> withFactory(FactoryVarArgsFunction<T> factory, InjectableReference<?>... parameterReferences) {
        if (constructor != null) {
            throw new IllegalStateException(
                    "A constructor reference has already been defined. Are you calling withConstructor or withFactory more than once?");
        }
        this.constructor = new FactoryVarArgsConstructorFunction<>(factory, Arrays.asList(parameterReferences));
        return this;
    }

    public RegistrationBuilder<T> withFactory(FactoryVarArgsFunction<T> factory, ReferenceBuilder<?>... parameterReferences) {
        return this.withFactory(factory,
                Stream.of(parameterReferences).map(ReferenceBuilder::build).toArray(InjectableReference<?>[]::new));
    }
    
    public RegistrationBuilder<T> withFactory(InjectableReference<?> factoryReference, String methodName,
            InjectableReference<?>... parameterReferences) throws NoSuchMethodException {
        if (constructor != null) {
            throw new IllegalStateException(
                    "A constructor reference has already been defined. Are you calling withConstructor or withFactory more than once?");
        }
        this.constructor = new FactoryMethodConstructionFunction<>(this.classToRegister, factoryReference, methodName,
                Arrays.asList(parameterReferences));
        return this;
    }

    public RegistrationBuilder<T> withFactory(ReferenceBuilder<?> factoryReference, String methodName,
            ReferenceBuilder<?>... parameterReferences) throws NoSuchMethodException {
        this.withFactory(factoryReference.build(), methodName, parameterReferences);
        return this;
    }

    public RegistrationBuilder<T> withFactory(InjectableReference<?> factoryReference, String methodName,
            ReferenceBuilder<?>... parameterReferences) throws NoSuchMethodException {

        return this.withFactory(factoryReference, methodName,
                Stream.of(parameterReferences).map(ReferenceBuilder::build).toArray(InjectableReference<?>[]::new));
    }
    
    public RegistrationBuilder<T> withStaticFactory(Class<?> factoryClass, String staticMethodName, InjectableReference<?>... parameterReferences)
            throws NoSuchMethodException {
        if (constructor != null) {
            throw new IllegalStateException(
                    "A constructor reference has already been defined. Are you calling withConstructor or withFactory more than once?");
        }
        this.constructor = new StaticFactoryMethodConstructionFunction<>(this.classToRegister, factoryClass, staticMethodName,
                Arrays.asList(parameterReferences));
        return this;
    }

    public RegistrationBuilder<T> withStaticFactory(Class<?> factoryClass, String staticMethodName, ReferenceBuilder<?>... parameterReferences)
            throws NoSuchMethodException {

        return this.withStaticFactory(factoryClass, staticMethodName,
                Stream.of(parameterReferences).map(ReferenceBuilder::build).toArray(InjectableReference<?>[]::new));
    }

    public RegistrationBuilder<T> asSingleton() {
        this.scopeType = SingletonScope.class;
        return this;
    }

    public RegistrationBuilder<T> scopedTo(Class<? extends InstanceScope> scopeType) {
        this.scopeType = scopeType;
        return this;
    }

    public <O> RegistrationBuilder<T> withField(String fieldName, InjectableReference<?> parameterReference)
            throws NoSuchFieldException {

        this.injectionFunctions.add(new FieldInjectionFunction<>(this.classToRegister, fieldName, parameterReference));
        return this;
    }

    public <O> RegistrationBuilder<T> withField(String fieldName, ReferenceBuilder<?> parameterReference)
            throws NoSuchFieldException {

        return this.withField(fieldName, parameterReference.build());
    }

    public <O> RegistrationBuilder<T> withField(Class<? super T> baseClass, String fieldName, InjectableReference<?> parameterReference)
            throws NoSuchFieldException {

        this.injectionFunctions.add(new FieldInjectionFunction<>(baseClass, fieldName, parameterReference));
        return this;
    }

    public <O> RegistrationBuilder<T> withField(Class<? super T> baseClass, String fieldName, ReferenceBuilder<?> parameterReference)
            throws NoSuchFieldException {

        return this.withField(baseClass, fieldName, parameterReference.build());
    }

    public <O> RegistrationBuilder<T> withMethod(String methodName, InjectableReference<?>... parameterReferences)
            throws NoSuchMethodException {
        this.injectionFunctions.add(new MethodInjectionFunction<>(this.classToRegister, methodName, Arrays.asList(parameterReferences)));
        return this;
    }

    public <O> RegistrationBuilder<T> withMethod(String methodName, ReferenceBuilder<?>... parameterReferences)
            throws NoSuchMethodException {

        return this.withMethod(methodName,
                Stream.of(parameterReferences).map(ReferenceBuilder::build).toArray(InjectableReference<?>[]::new));
    }

    public <O> RegistrationBuilder<T> withMethod(Class<? super T> baseClass, String methodName,
            InjectableReference<?>... parameterReferences) throws NoSuchMethodException {

        this.injectionFunctions.add(new MethodInjectionFunction<>(baseClass, methodName, Arrays.asList(parameterReferences)));
        return this;
    }

    public <O> RegistrationBuilder<T> withMethod(Class<? super T> baseClass, String methodName, ReferenceBuilder<?>... parameterReferences)
            throws NoSuchMethodException {

        return this.withMethod(baseClass, methodName,
                Stream.of(parameterReferences).map(ReferenceBuilder::build).toArray(InjectableReference<?>[]::new));
    }

    public <O> RegistrationBuilder<T> withSetter(SetterFunction<T, O> setter, Class<O> classToInject) {
        this.injectionFunctions.add(new SetterInjectionFunction<>(this.classToRegister, setter,
                new TypedInjectableReference<>(classToInject)));
        return this;
    }

    public <O> RegistrationBuilder<T> withSetterByName(SetterFunction<T, O> setter, String name, Class<O> classToInject) {
        this.injectionFunctions.add(new SetterInjectionFunction<>(this.classToRegister, setter,
                new NamedInjectableReference<>(name, classToInject)));
        return this;
    }

    public <O> RegistrationBuilder<T> withSetterMulti(SetterFunction<T, O> setter, Class<O> classToInject,
            Class<?> genericType) {
        this.injectionFunctions.add(new SetterInjectionFunction<>(this.classToRegister, setter,
                new TypedMultiInjectableReference<>(classToInject, genericType)));
        return this;
    }

    public <O> RegistrationBuilder<T> withSetter(SetterFunction<T, O> setter, final ReferenceBuilder<O> ref) {
        this.injectionFunctions.add(new SetterInjectionFunction<>(this.classToRegister, setter, ref.build()));
        return this;
    }

    public <O> RegistrationBuilder<T> withSetter(SetterFunction<T, O> setter, final InjectableReference<O> ref) {
        this.injectionFunctions.add(new SetterInjectionFunction<>(this.classToRegister, setter, ref));
        return this;
    }

    public RegistrationBuilder<T> withPostConstructor(PostConstructionFunction<T> postConstructor) {
        if (this.postConstructor != null) {
            throw new IllegalStateException(
                    "A post constructor reference has already been defined. Are you calling withPostConstructor more than once?");
        }

        this.postConstructor = postConstructor;
        return this;
    }

    public RegistrationBuilder<T> withPreDestroy(PreDestroyFunction<T> preDestroy) {
        if (this.preDestroy != null) {
            throw new IllegalStateException(
                    "A pre-destroy reference has already been defined. Are you calling withPreDestroy more than once?");
        }

        this.preDestroy = preDestroy;
        return this;
    }

    public RegistrationBuilder<T> named(String name) {
        Preconditions.verifyNotNull(name, "Registration name must not be null");
        this.name = name;
        return this;
    }

    public RegistrationBuilder<T> qualifiedBy(String qualifierName) {
        Preconditions.verifyNotNull(qualifierName, "When provided, the qualifier name must not be null");
        this.qualifierName = qualifierName;
        return this;
    }

    public InjectionRegistration<T> build() throws NoSuchMethodException {
        if (this.constructor == null && !this.objectToRegister.isPresent()) {
            withConstructor();
        }

        return this.objectToRegister
                .map(object -> new InjectionRegistration<T>(name, qualifierName, object,
                        injectionFunctions, postConstructor, preDestroy))
                .orElse(new InjectionRegistration<T>(scopeType, name, qualifierName, classToRegister, constructor,
                        injectionFunctions, postConstructor, preDestroy));
    }

    private Constructor<?> findUniquePublicConstructor(final Constructor<?>[] constructors) {
        Constructor<?> constructorToUse = null;
        for (final Constructor<?> c : constructors) {
            if (Modifier.isPublic(c.getModifiers())) {
                if (constructorToUse == null) {
                    constructorToUse = c;
                } else {
                    // there is more than 1 public constructor
                    constructorToUse = null;
                    break;
                }
            }
        }
        return constructorToUse;
    }

    @Override
    public String toString() {
        return "RegistrationBuilder [classToRegister=" + classToRegister + ", name=" + name + "]";
    }

}
