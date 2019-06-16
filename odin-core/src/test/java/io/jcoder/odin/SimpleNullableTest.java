/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin;

import static io.jcoder.odin.builder.ReferenceBuilder.paramOfType;
import static io.jcoder.odin.builder.RegistrationBuilder.singleton;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/**
 *
 * @author Camilo Gonzalez
 */
public class SimpleNullableTest {

    public static class A {
    }

    public static class B {
        private final A a;

        public B(final A a) {
            this.a = a;
        }
    }

    @Test
    public void test() throws Exception {
        final InjectionContext context = new DefaultInjectionContext();

        context.register(singleton(B.class).withConstructor(paramOfType(A.class).nullable()));

        context.initialize();

        final B b = context.get(B.class);
        assertNotNull(b, "Instance 'b' must not be null");
        assertNull(b.a, "Member 'a' of b must be null");
    }

}
