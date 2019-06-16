/*
 * Copyright 2019 - JCoder Ltd
 */
package io.jcoder.odin;

import javax.inject.Inject;

/**
 * 
 * @author Camilo Gonzalez
 */
public class Door {

    private boolean superInjected;

    @Inject
    void setProperties() {
        this.superInjected = true;
    }

    public boolean isSuperInjected() {
        return superInjected;
    }

}
