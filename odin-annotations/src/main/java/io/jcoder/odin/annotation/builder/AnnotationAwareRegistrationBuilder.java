/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin.annotation.builder;

import static io.jcoder.odin.annotation.reflection.AnnotationUtils.processParameterReferences;
import static io.jcoder.odin.annotation.reflection.AnnotationUtils.qualifierFromAnnotations;
import static io.jcoder.odin.annotation.reflection.AnnotationUtils.supportsMulti;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.base.Preconditions;

import io.jcoder.odin.DestructionException;
import io.jcoder.odin.InitializationException;
import io.jcoder.odin.annotation.RequestScoped;
import io.jcoder.odin.annotation.ScopedTo;
import io.jcoder.odin.annotation.component.Component;
import io.jcoder.odin.builder.RegistrationBuilder;
import io.jcoder.odin.reference.InjectableReference;
import io.jcoder.odin.reference.NamedInjectableReference;
import io.jcoder.odin.reference.TypedInjectableReference;
import io.jcoder.odin.reference.TypedMultiInjectableReference;
import io.jcoder.odin.web.RequestScope;

/**
 *
 * @author Camilo Gonzalez
 */
public class AnnotationAwareRegistrationBuilder<T> extends RegistrationBuilder<T> {

    private final Class<T> classToRegister;

    public AnnotationAwareRegistrationBuilder(final Class<T> classToRegister) {
        super(classToRegister);
        this.classToRegister = classToRegister;
    }

    public static <T> RegistrationBuilder<T> annotated(final Class<T> classToRegister) {
        final AnnotationAwareRegistrationBuilder<T> builder = new AnnotationAwareRegistrationBuilder<>(classToRegister);

        builder.processAnnotations();

        return builder;
    }

    private void processAnnotations() {
        processScopeAnnotations();
        processNameAnnotation();
        processConstructorAnnotations();
        processFieldAnnotations(this.classToRegister);
        processMethodAnnotations(this.classToRegister, new ArrayList<>(), new ArrayList<>());
        processPostConstructAnnotation();
        processPreDestroyAnnotation();
    }

    private void processScopeAnnotations() {
        if (classToRegister.isAnnotationPresent(Singleton.class) || classToRegister.isAnnotationPresent(Component.class)) {
            asSingleton();
        } else if (classToRegister.isAnnotationPresent(RequestScoped.class)) {
            scopedTo(RequestScope.class);
        } else if (classToRegister.isAnnotationPresent(ScopedTo.class)) {
            final ScopedTo scopedToAnnotation = classToRegister.getAnnotation(ScopedTo.class);
            Preconditions.checkNotNull("A scope type must be declared in the ScopedTo annotation of class: " + classToRegister);
            scopedTo(scopedToAnnotation.value());
        }
    }

    private void processNameAnnotation() {
        Named namedAnnotation = classToRegister.getAnnotation(Named.class);
        if (namedAnnotation != null) {
            named(namedAnnotation.value());
        }
    }

    private void processConstructorAnnotations() {
        Constructor<?> constructorToUse = null;
        final Constructor<?>[] constructors = classToRegister.getDeclaredConstructors();
        if (constructors.length == 1) {
            constructorToUse = constructors[0];
        } else {
            constructorToUse = findAnnotatedConstructor(constructors);
        }

        if (constructorToUse == null) {
            throw new IllegalStateException(
                    "There is more than one constructor and none are annotated with @Inject for: " + classToRegister.getName()
                            + ". Try specifying which constructor to use with the @Inject annotation");
        }

        try {
            constructorToUse.setAccessible(true);
            final InjectableReference<?>[] parameterReferences = processParameterReferences(constructorToUse.getParameters());
            withConstructor(parameterReferences);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(
                    "Couldn't find a constructor that we had a reference for in class: " + classToRegister.getName());
        }
    }

    private void processFieldAnnotations(final Class<? super T> classToProcess) {
        if (classToProcess == null) {
            return;
        }
        processFieldAnnotations(classToProcess.getSuperclass());

        for (final Field field : classToProcess.getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                final InjectableReference<?> parameterReference = processFieldParameterReference(field);
                try {
                    field.setAccessible(true);
                    withField(classToProcess, field.getName(), parameterReference);
                } catch (NoSuchFieldException e) {
                    throw new IllegalStateException(
                            "Couldn't find field " + field.getName() + " that we had a reference for in class: "
                                    + classToProcess.getName());
                }
            }
        }
    }

    private void processMethodAnnotations(final Class<? super T> classToProcess, final List<MethodDetails> injectedMethods,
            final List<MethodDetails> nonInjectedMethods) {

        if (classToProcess == null) {
            return;
        }

        for (final Method method : classToProcess.getDeclaredMethods()) {
            final MethodDetails md = new MethodDetails(method.getName(), method.getParameterTypes());
            if (injectedMethods.contains(md) || nonInjectedMethods.contains(md)) {
                continue;
            }

            if (method.isAnnotationPresent(Inject.class)) {
                final InjectableReference<?>[] parameterReferences = processParameterReferences(method.getParameters());
                try {
                    injectedMethods.add(md);
                    method.setAccessible(true);
                    withMethod(method.getName(), parameterReferences);
                } catch (NoSuchMethodException e) {
                    throw new IllegalStateException(
                            "Couldn't find method " + method.getName() + " that we had a reference for in class: "
                                    + classToProcess.getName());
                }
            } else {
                nonInjectedMethods.add(md);
            }
        }
        processMethodAnnotations(classToProcess.getSuperclass(), injectedMethods, nonInjectedMethods);
    }

    private InjectableReference<?> processFieldParameterReference(final Field field) {
        final Class<?> qualifierType = qualifierFromAnnotations(field.getAnnotations());

        final Class<?> fieldType = field.getType();
        if (qualifierType != null && !Named.class.equals(qualifierType)) {
            return new NamedInjectableReference<>(qualifierType.getSimpleName(), fieldType);
        } else {
            final Named namedAnnotation = field.getAnnotation(Named.class);
            if (namedAnnotation != null) {
                return new NamedInjectableReference<>(namedAnnotation.value(), fieldType);
            }
        }

        if (supportsMulti(fieldType)) {
            return new TypedMultiInjectableReference<>(fieldType);
        }

        return new TypedInjectableReference<>(fieldType);
    }

    private void processPostConstructAnnotation() {
        boolean postConstructMethodFound = false;
        for (final Method method : classToRegister.getDeclaredMethods()) {
            if (method.isAnnotationPresent(PostConstruct.class)) {
                if (postConstructMethodFound) {
                    throw new IllegalArgumentException("The provided class has more than one method annotated with PostConstruct");
                }
                postConstructMethodFound = true;

                Preconditions.checkArgument(method.getParameterCount() == 0, "The PostConstruct method must not have parameters");
                Preconditions.checkArgument(method.getReturnType().equals(void.class),
                        "The PostConstruct method must have a void return type");

                method.setAccessible(true);
                withPostConstructor(t -> {
                    try {
                        method.invoke(t);
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        throw new InitializationException(
                                "Exception executing PostConstruct method '" + method.getName() + "' of class '" + classToRegister + "'",
                                e);
                    }
                });
            }
        }
    }

    private void processPreDestroyAnnotation() {
        boolean preDestroyMethodFound = false;
        for (final Method method : classToRegister.getDeclaredMethods()) {
            if (method.isAnnotationPresent(PreDestroy.class)) {
                if (preDestroyMethodFound) {
                    throw new IllegalArgumentException("The provided class has more than one method annotated with PreDestroy");
                }
                preDestroyMethodFound = true;

                Preconditions.checkArgument(method.getParameterCount() == 0, "The PreDestroy method must not have parameters");
                Preconditions.checkArgument(method.getReturnType().equals(void.class),
                        "The PreDestroy method must have a void return type");

                method.setAccessible(true);
                withPreDestroy(t -> {
                    try {
                        method.invoke(t);
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        throw new DestructionException(
                                "Exception executing PreDestroy method '" + method.getName() + "' of class '" + classToRegister + "'",
                                e);
                    }
                });
            }
        }
    }

    private Constructor<?> findAnnotatedConstructor(final Constructor<?>[] constructors) {
        Constructor<?> constructorToUse = null;
        for (final Constructor<?> c : constructors) {
            if (c.isAnnotationPresent(Inject.class)) {
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

    private static class MethodDetails {
        private final String methodName;

        private final Class<?>[] methodParameters;

        public MethodDetails(String methodName, Class<?>[] methodParameters) {
            this.methodName = methodName;
            this.methodParameters = methodParameters;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());
            result = prime * result + Arrays.hashCode(methodParameters);
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            MethodDetails other = (MethodDetails) obj;
            if (methodName == null) {
                if (other.methodName != null) {
                    return false;
                }
            } else if (!methodName.equals(other.methodName)) {
                return false;
            }
            if (!Arrays.equals(methodParameters, other.methodParameters)) {
                return false;
            }
            return true;
        }

    }
}
