/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin;

import io.jcoder.odin.function.InjectionFunction;

/**
 *
 * @author Camilo Gonzalez
 */
public class InjectionFunctionException extends RuntimeException {

    private static final long serialVersionUID = 2835737866796725340L;

    private final InjectionFunction<?> failedFunction;

    public InjectionFunctionException(InjectionFunction<?> failedFunction, String message, Throwable cause) {
        super("Exception of InjectionFunction: " + failedFunction + " - " + message, cause);
        this.failedFunction = failedFunction;
    }

    public InjectionFunctionException(InjectionFunction<?> failedFunction, String message) {
        this(failedFunction, message, null);
    }

    public InjectionFunction<?> getFailedFunction() {
        return failedFunction;
    }

}
