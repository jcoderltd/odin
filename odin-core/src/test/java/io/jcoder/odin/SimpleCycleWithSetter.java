/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin;

import static io.jcoder.odin.builder.RegistrationBuilder.singleton;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 *
 * @author Camilo Gonzalez
 */
public class SimpleCycleWithSetter {
    public static class A {
        private final B b;

        public A(final B b) {
            this.b = b;
        }
    }

    public static class B {
        private A a;

        public void setA(final A a) {
            this.a = a;
        }
    }

    @Test
    public void testSimpleCycle() throws Exception {
        final InjectionContext context = new DefaultInjectionContext();
        context.register(singleton(A.class));
        context.register(singleton(B.class).withSetter(B::setA, A.class));

        context.initialize();

        final A a = context.get(A.class);
        assertNotNull(a, "Instance 'a' must not be null");
        assertNotNull(a.b, "Member 'b' of 'a' must not be null");

        final B b = context.get(B.class);
        assertNotNull(b, "Instance 'b' must not be null");
        assertNotNull(b.a, "Member 'a' of 'b' must not be null");

        assertTrue(a.b == b, "B is a singleton and only instance must have been created");
        assertTrue(b.a == a, "A is a singleton and only instance must have been created");
    }

}
