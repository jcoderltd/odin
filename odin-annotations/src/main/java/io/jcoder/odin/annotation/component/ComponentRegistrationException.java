/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin.annotation.component;

/**
 *
 * @author Camilo Gonzalez
 */
public class ComponentRegistrationException extends RuntimeException {

    private static final long serialVersionUID = -5280680199500996614L;

    public ComponentRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ComponentRegistrationException(String message) {
        super(message);
    }

    public ComponentRegistrationException(Throwable cause) {
        super(cause);
    }

}
