/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin;

import io.jcoder.odin.function.ConstructionFunction;

/**
 *
 * @author Camilo Gonzalez
 */
public class ConstructionFunctionException extends RuntimeException {

    private static final long serialVersionUID = -713806228328644210L;

    private final ConstructionFunction<?> failedFunction;

    public ConstructionFunctionException(final ConstructionFunction<?> failedFunction, final String message, final Throwable cause) {
        super("Exception of InjectionFunction: " + failedFunction + " - " + message, cause);
        this.failedFunction = failedFunction;
    }

    public ConstructionFunctionException(final ConstructionFunction<?> failedFunction, final String message) {
        this(failedFunction, message, null);
    }

    public ConstructionFunction<?> getFailedFunction() {
        return failedFunction;
    }

}
