/*
 * Copyright 2019 - JCoder Ltd
 */
package io.jcoder.odin.inner;

import javax.inject.Inject;

import io.jcoder.odin.Door;

/**
 * 
 * @author Camilo Gonzalez
 */
public class GardenDoor extends Door {

    private boolean subInjected;

    @Inject
    void setProperties() {
        this.subInjected = true;
    }

    public boolean isSubInjected() {
        return subInjected;
    }

}
