/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin;

import static io.jcoder.odin.builder.RegistrationBuilder.singleton;
import static io.jcoder.odin.builder.RegistrationBuilder.type;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 *
 * @author Camilo Gonzalez
 */
public class CycleTests {

    public static class A {
        public A(final D d) {
        }
    }

    public static class B {
        public B(final A a) {
        }
    }

    public static class C {
        public C(final B b) {
        }
    }

    public static class D {
        public D(final C c) {
        }
    }

    @Test
    public void testCycle() {
        assertThrows(IllegalStateException.class, () -> {
            final InjectionContext context = new DefaultInjectionContext();

            context.register(singleton(A.class));
            context.register(type(B.class));
            context.register(singleton(C.class));
            context.register(singleton(D.class));

            context.initialize();
        }, "An IllegalStateException was expected due to the cyclic dependency");
    }

}
