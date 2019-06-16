/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin.annotation.builder;

import static io.jcoder.odin.annotation.reflection.AnnotationUtils.buildInjectableReference;
import static io.jcoder.odin.annotation.reflection.AnnotationUtils.getProviderGenericType;
import static io.jcoder.odin.annotation.reflection.AnnotationUtils.processParameterReferences;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.google.common.base.Preconditions;

import io.jcoder.odin.DestructionException;
import io.jcoder.odin.InitializationException;
import io.jcoder.odin.annotation.RequestScoped;
import io.jcoder.odin.annotation.ScopedTo;
import io.jcoder.odin.annotation.component.Component;
import io.jcoder.odin.builder.RegistrationBuilder;
import io.jcoder.odin.reference.InjectableReference;
import io.jcoder.odin.web.RequestScope;

/**
 *
 * @author Camilo Gonzalez
 */
public class AnnotationAwareRegistrationBuilder<T> extends RegistrationBuilder<T> {

    private final Class<T> classToRegister;

    public AnnotationAwareRegistrationBuilder(Class<T> classToRegister) {
        super(classToRegister);
        this.classToRegister = classToRegister;
    }

    public static <T> RegistrationBuilder<T> annotated(Class<T> classToRegister) {
        final AnnotationAwareRegistrationBuilder<T> builder = new AnnotationAwareRegistrationBuilder<>(classToRegister);

        builder.processAnnotations();

        return builder;
    }

    private void processAnnotations() {
        processScopeAnnotations();
        processNameAnnotation();
        processConstructorAnnotations();
        processFieldAnnotations(this.classToRegister);
        processMethodAnnotations(this.classToRegister);
        processPostConstructAnnotation();
        processPreDestroyAnnotation();
    }

    private void processScopeAnnotations() {
        if (classToRegister.isAnnotationPresent(Singleton.class) || classToRegister.isAnnotationPresent(Component.class)) {
            asSingleton();
        } else if (classToRegister.isAnnotationPresent(RequestScoped.class)) {
            scopedTo(RequestScope.class);
        } else if (classToRegister.isAnnotationPresent(ScopedTo.class)) {
            ScopedTo scopedToAnnotation = classToRegister.getAnnotation(ScopedTo.class);
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

    private void processFieldAnnotations(Class<? super T> classToProcess) {
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

    private void processMethodAnnotations(Class<T> classToProcess) {
        List<MethodDetails<T>> injectedMethods = new ArrayList<>();
        processMethodAnnotations(classToProcess, injectedMethods, new ArrayList<>());

        for (MethodDetails<T> injectedMethod : injectedMethods) {
            Method method = injectedMethod.method;
            InjectableReference<?>[] parameterReferences = processParameterReferences(method.getParameters());
            try {
                withMethod(injectedMethod.declaringClass, method.getName(), parameterReferences);
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException(
                        "Couldn't find method " + method.getName() + " that we had a reference for in class: "
                                + classToProcess.getName());
            }
        }
    }

    private void processMethodAnnotations(Class<? super T> classToProcess, List<MethodDetails<T>> injectedMethods,
            List<MethodDetails<T>> nonInjectedMethods) {

        if (classToProcess == null) {
            return;
        }

        for (final Method method : classToProcess.getDeclaredMethods()) {
            final MethodDetails<T> md = new MethodDetails<T>(classToProcess, method, method.getParameterTypes());
            if (injectedMethods.contains(md) || nonInjectedMethods.contains(md)) {
                continue;
            }

            if (method.isAnnotationPresent(Inject.class)) {
                injectedMethods.add(md);
                method.setAccessible(true);
            } else {
                nonInjectedMethods.add(md);
            }
        }
        processMethodAnnotations(classToProcess.getSuperclass(), injectedMethods, nonInjectedMethods);
    }

    private InjectableReference<?> processFieldParameterReference(Field field) {
        Annotation[] annotations = field.getAnnotations();
        Named namedAnnotation = field.getAnnotation(Named.class);
        Class<?> fieldType = field.getType();
        boolean isProvider = fieldType.equals(Provider.class);
        if (isProvider) {
            fieldType = getProviderGenericType(field);
        }

        return buildInjectableReference(fieldType, annotations, namedAnnotation, isProvider);
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

    private Constructor<?> findAnnotatedConstructor(Constructor<?>[] constructors) {
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

    private static class MethodDetails<T> {
        private final Class<? super T> declaringClass;

        private final String methodName;

        private final Class<?>[] methodParameters;

        private final String visibilityLevelName;

        private final Method method;

        public MethodDetails(Class<? super T> declaringClass, Method method, Class<?>[] methodParameters) {
            this.declaringClass = declaringClass;
            this.method = method;
            this.methodName = method.getName();
            this.methodParameters = methodParameters;
            int modifiers = method.getModifiers();
            // Package private methods in different packages don't override each other :/
            boolean methodHasDefaultVisibility = !Modifier.isPublic(modifiers) && !Modifier.isProtected(modifiers)
                    && !Modifier.isPrivate(modifiers);
            if (Modifier.isPrivate(modifiers)) {
                this.visibilityLevelName = declaringClass.getName();
            } else if (methodHasDefaultVisibility) {
                this.visibilityLevelName = declaringClass.getPackage().getName();
            } else {
                this.visibilityLevelName = null;
            }
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + Objects.hash(methodName, visibilityLevelName);
            result = prime * result + Arrays.hashCode(methodParameters);
            return result;
        }

        @SuppressWarnings("rawtypes")
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof MethodDetails)) {
                return false;
            }
            MethodDetails other = (MethodDetails) obj;
            return Objects.equals(methodName, other.methodName) && Arrays.equals(methodParameters, other.methodParameters)
                    && Objects.equals(visibilityLevelName, other.visibilityLevelName);
        }

    }
}
