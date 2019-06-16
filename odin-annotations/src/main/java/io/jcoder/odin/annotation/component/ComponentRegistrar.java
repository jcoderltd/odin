/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin.annotation.component;

import io.jcoder.odin.InjectionContext;

/**
 *
 * @author Camilo Gonzalez
 */
public interface ComponentRegistrar {

    void addComponent(Class<?> componentToRegister);

    InjectionContext injectionContext();

    void initialize();

}
