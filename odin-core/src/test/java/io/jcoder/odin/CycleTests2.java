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
public class CycleTests2 {

    public static class A {
        public A(final B b) {
        }
    }

    public static class B {
        public B(final A a) {
        }
    }

    @Test
    public void testCycle() throws Exception {
        assertThrows(IllegalStateException.class, () -> {
            final InjectionContext context = new DefaultInjectionContext();

            context.register(singleton(A.class));
            context.register(type(B.class));

            context.initialize();
        }, "Exception expected due to the dependency cycle");
    }

}
