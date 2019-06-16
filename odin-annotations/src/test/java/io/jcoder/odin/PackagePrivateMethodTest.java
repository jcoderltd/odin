/*
 * Copyright 2019 - JCoder Ltd
 */
package io.jcoder.odin;

import static io.jcoder.odin.annotation.builder.AnnotationAwareRegistrationBuilder.annotated;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.jcoder.odin.inner.GardenDoor;

/**
 * This test covers the case of "overridden" package private methods that are injected in class hierarchies where the
 * parent and child classes are in different packages. In this case, overriding doesn't actually occur and both methods
 * should be injected.
 * 
 * @author Camilo Gonzalez
 */
public class PackagePrivateMethodTest {

    @Test
    public void testPackagePrivate() throws Exception {
        InjectionContext context = new DefaultInjectionContext();
        context.register(annotated(GardenDoor.class));
        context.initialize();

        GardenDoor gardenDoor = context.get(GardenDoor.class);
        assertTrue(gardenDoor.isSuperInjected());
        assertTrue(gardenDoor.isSubInjected());
    }

}
