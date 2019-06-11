/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin;

/**
 * Exception thrown when an underlying constructor or factory method fails to create the requested object.
 *
 * @author Camilo Gonzalez
 */
public class ObjectCreationException extends RuntimeException {

    private static final long serialVersionUID = 6014896594660845737L;

    public ObjectCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ObjectCreationException(String message) {
        super(message);
    }

    public ObjectCreationException(Throwable cause) {
        this("Couldn't create object due to exception at construction time", cause);
    }

}
