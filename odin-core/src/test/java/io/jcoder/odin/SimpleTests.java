/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin;

import static io.jcoder.odin.builder.ReferenceBuilder.paramOfType;
import static io.jcoder.odin.builder.RegistrationBuilder.singleton;
import static io.jcoder.odin.builder.RegistrationBuilder.type;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 *
 * @author Camilo Gonzalez
 */
public class SimpleTests {

    public interface Base {
    }

    public static class A implements Base {
    }

    public static class B implements Base {
    }

    public static class C {
    }

    public static class D {
        private final List<Base> baseObjects;

        private final C cObject;

        public D(final List<Base> baseObjects, final C cObject) {
            this.baseObjects = baseObjects;
            this.cObject = cObject;
        }
    }

    @Test
    public void test() throws Exception {
        final InjectionContext context = new DefaultInjectionContext();

        context.register(singleton(A.class));
        context.register(type(B.class));
        context.register(singleton(C.class));
        context.register(singleton(D.class)
                .withConstructor(
                        paramOfType(List.class).multi().ofGenericType(Base.class),
                        paramOfType(C.class)));

        context.initialize();

        final A a = context.get(A.class);
        assertNotNull(a, "Instance 'a' must not be null");

        final B b = context.get(B.class);
        assertNotNull(b, "Instance 'b' must not be null");

        final D d = context.get(D.class);
        assertNotNull(d, "Instance 'd' must not be null");

        final D d2 = context.get(D.class);
        assertNotNull(d2, "Instance 'd2' must not be null");

        assertTrue(d == d2, "D is a singleton - only one instance must have been created");
        assertNotNull(d.cObject, "Member 'cObject' of 'd' must not be null");
        assertEquals(2, d.baseObjects.size(), "List of Base objects in D must have two elements");

        assertTrue(d.baseObjects.contains(a), "D's list must contain a reference to 'a' (A is a singleton)");
        assertFalse(d.baseObjects.contains(b), "D's list must not contain a reference to 'b' (B is not a singleton)");
    }

}
