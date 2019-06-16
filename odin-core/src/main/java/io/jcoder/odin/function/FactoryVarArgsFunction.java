/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin.function;

@FunctionalInterface
public interface FactoryVarArgsFunction<T> {
    T apply(Object... params);
}