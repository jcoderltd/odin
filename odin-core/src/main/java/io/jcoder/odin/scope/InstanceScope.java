/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin.scope;

import io.jcoder.odin.registration.InjectionRegistration;

/**
 *
 * @author Camilo Gonzalez
 */
public interface InstanceScope {

    <T> T getInstance(InjectionRegistration<T> registration);

    <T> boolean setInstance(InjectionRegistration<T> registration, T object);

    void destroy();
}
