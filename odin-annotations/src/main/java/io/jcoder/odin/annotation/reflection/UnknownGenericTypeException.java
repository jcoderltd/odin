/*
 * Copyright 2019 - JCoder Ltd
 */
package io.jcoder.odin.annotation.reflection;

/**
 * 
 * @author Camilo Gonzalez
 */
public class UnknownGenericTypeException extends RuntimeException {

    private static final long serialVersionUID = -3687316042494619899L;

    public UnknownGenericTypeException(String message) {
        super(message);
    }

}
