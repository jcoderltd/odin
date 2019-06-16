/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin.function;

import java.io.Serializable;

import javax.annotation.PreDestroy;

/**
 * Represents a PreDestroy function, adapted as possible from the {@link PreDestroy} annotation definition.
 *
 * <p>
 * This method is called on the instance after its being detached from the InjectionContext.
 *
 * @author Camilo Gonzalez
 */
@FunctionalInterface
public interface PreDestroyFunction<T> extends Serializable {

    void preDestroy(T instance);

}
