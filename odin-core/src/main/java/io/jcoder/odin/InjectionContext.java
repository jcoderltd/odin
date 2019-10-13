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
