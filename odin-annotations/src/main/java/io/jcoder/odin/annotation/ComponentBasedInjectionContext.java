/*
 *  Copyright 2020 JCoder Ltd.
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
package io.jcoder.odin.annotation;

import java.util.List;

import javax.inject.Provider;

import io.jcoder.odin.DefaultInjectionContext;
import io.jcoder.odin.InjectionContext;
import io.jcoder.odin.annotation.builder.AnnotationAwareRegistrationBuilder;
import io.jcoder.odin.annotation.component.ComponentRegistrar;
import io.jcoder.odin.annotation.component.DefaultComponentRegistrar;
import io.jcoder.odin.builder.RegistrationBuilder;
import io.jcoder.odin.registration.InjectionRegistration;
import io.jcoder.odin.scope.InstanceScope;

/**
 * A delegating {@link InjectionContext} that provides helper methods to register annotated classes and components.
 * 
 * @author Camilo Gonzalez
 */
public class ComponentBasedInjectionContext implements InjectionContext {

    private final InjectionContext delegate;

    private final ComponentRegistrar componentRegistrar;

    /**
     * Creates a new {@link ComponentBasedInjectionContext} that delegates down to a {@link DefaultInjectionContext}
     */
    public ComponentBasedInjectionContext() {
        this(new DefaultInjectionContext());
    }
    
    /**
     * Creates a new {@link ComponentBasedInjectionContext} that delegates down all the {@link InjectionContext}
     * operations to the context used by the given registrar.
     * 
     * @param registrar
     */
    public ComponentBasedInjectionContext(ComponentRegistrar registrar) {
        this.delegate = registrar.injectionContext();
        this.componentRegistrar = registrar;
    }

    /**
     * Creates a new {@link ComponentBasedInjectionContext} that delegates down all the {@link InjectionContext}
     * operations to the provided delegate.
     * 
     * @param delegate
     */
    public ComponentBasedInjectionContext(InjectionContext delegate) {
        this.delegate = delegate;
        this.componentRegistrar = new DefaultComponentRegistrar(delegate);
    }

    public boolean initialized() {
        return delegate.initialized();
    }

    public <T> void registerAnnotated(Class<T> objectClass) throws NoSuchMethodException {
        this.register(AnnotationAwareRegistrationBuilder.annotated(objectClass));
    }

    public void addComponent(Class<?> componentToRegister) {
        componentRegistrar.addComponent(componentToRegister);
    }

    public <T> void register(RegistrationBuilder<T> registrationBuilder) throws NoSuchMethodException {
        delegate.register(registrationBuilder);
    }

    public <T> void register(InjectionRegistration<T> registration) {
        delegate.register(registration);
    }

    public void initialize() {
        componentRegistrar.initialize();
    }

    public <T> List<InjectionRegistration<?>> getRegistrations() {
        return delegate.getRegistrations();
    }

    public <T> boolean exists(Class<T> objectClass) {
        return delegate.exists(objectClass);
    }

    public <T> T get(Class<T> objectClass) {
        return delegate.get(objectClass);
    }

    public <T> T getNamed(Class<T> objectClass, String name) {
        return delegate.getNamed(objectClass, name);
    }

    public <T> T getWithQualifier(Class<T> objectClass, String qualifierName) {
        return delegate.getWithQualifier(objectClass, qualifierName);
    }

    public <T> List<T> getMulti(Class<T> objectClass) {
        return delegate.getMulti(objectClass);
    }

    public <T> InjectionRegistration<T> getRegistration(Class<T> objectClass) {
        return delegate.getRegistration(objectClass);
    }

    public <T> InjectionRegistration<T> getNamedRegistration(Class<T> objectClass, String name) {
        return delegate.getNamedRegistration(objectClass, name);
    }

    public <T> InjectionRegistration<T> getQualifiedRegistration(Class<T> objectClass, String qualifierName) {
        return delegate.getQualifiedRegistration(objectClass, qualifierName);
    }

    public <T> List<InjectionRegistration<T>> getMultiRegistration(Class<T> objectClass) {
        return delegate.getMultiRegistration(objectClass);
    }

    public InstanceScope getScope(Class<? extends InstanceScope> scopeClass) {
        return delegate.getScope(scopeClass);
    }

    public <S extends InstanceScope> void registerScope(S scope) {
        delegate.registerScope(scope);
    }

    public boolean hasScope(Class<? extends InstanceScope> scopeClass) {
        return delegate.hasScope(scopeClass);
    }

    public void destroy() {
        delegate.destroy();
    }

    public <T> InjectionRegistration<Provider<?>> getProviderRegistration(InjectionRegistration<T> registration) {
        return delegate.getProviderRegistration(registration);
    }

}
