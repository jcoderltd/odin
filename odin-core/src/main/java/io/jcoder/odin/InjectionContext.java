/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin;

import java.util.List;

import javax.inject.Provider;

import io.jcoder.odin.builder.RegistrationBuilder;
import io.jcoder.odin.registration.InjectionRegistration;
import io.jcoder.odin.scope.InstanceScope;

/**
 *
 * @author Camilo Gonzalez
 */
public interface InjectionContext {

    boolean initialized();

    <T> void register(RegistrationBuilder<T> registrationBuilder) throws NoSuchMethodException;

    <T> void register(InjectionRegistration<T> registration);

    void initialize();

    <T> List<InjectionRegistration<?>> getRegistrations();

    <T> boolean exists(Class<T> objectClass);

    <T> T get(Class<T> objectClass);

    <T> T getNamed(Class<T> objectClass, String name);
    
    <T> T getWithQualifier(Class<T> objectClass, String qualifierName);

    <T> List<T> getMulti(Class<T> objectClass);

    <T> InjectionRegistration<T> getRegistration(Class<T> objectClass);

    <T> InjectionRegistration<T> getNamedRegistration(Class<T> objectClass, String name);
    
    <T> InjectionRegistration<T> getQualifiedRegistration(Class<T> objectClass, String qualifierName);

    <T> List<InjectionRegistration<T>> getMultiRegistration(Class<T> objectClass);

    InstanceScope getScope(Class<? extends InstanceScope> scopeClass);

    <S extends InstanceScope> void registerScope(S scope);

    boolean hasScope(Class<? extends InstanceScope> scopeClass);
    
    void destroy();

    <T> InjectionRegistration<Provider<?>> getProviderRegistration(InjectionRegistration<T> registration);
}
