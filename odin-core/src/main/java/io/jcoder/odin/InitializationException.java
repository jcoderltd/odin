/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin;

/**
 * Exception thrown when an {@link InjectionContext} fails to initialize.
 *
 * @author Camilo Gonzalez
 */
public class InitializationException extends RuntimeException {

    private static final long serialVersionUID = -5736015836438911591L;

    public InitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InitializationException(String message) {
        super(message);
    }

    public InitializationException(Throwable cause) {
        super(cause);
    }

}
