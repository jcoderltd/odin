/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin.function;

import java.io.Serializable;

import javax.annotation.PostConstruct;

/**
 * Represents a PostConstruct function, adapted as possible from the {@link PostConstruct} annotation definition.
 *
 * <p>
 * This method is called on the instance after its constructor and defined setter injection methods have been invoked.
 *
 * @author Camilo Gonzalez
 */
@FunctionalInterface
public interface PostConstructionFunction<T> extends Serializable {

    void postConstruct(T instance);

}
