/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin;

/**
 * Exception thrown when an {@link InjectionContext} fails to destroy an object.
 *
 * @author Camilo Gonzalez
 */
public class DestructionException extends RuntimeException {

    private static final long serialVersionUID = -7953593596238564165L;

    public DestructionException(String message, Throwable cause) {
        super(message, cause);
    }

    public DestructionException(String message) {
        super(message);
    }

    public DestructionException(Throwable cause) {
        super(cause);
    }

}
