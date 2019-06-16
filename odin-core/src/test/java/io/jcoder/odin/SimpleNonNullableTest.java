/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin;

import static io.jcoder.odin.builder.ReferenceBuilder.paramOfType;
import static io.jcoder.odin.builder.RegistrationBuilder.singleton;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 *
 * @author Camilo Gonzalez
 */
public class SimpleNonNullableTest {

    public static class A {
        public void setB(final B b) {
        }
    }

    public static class B {
        private A a;

        public B(final A a) {
            this.a = a;
        }

        public void setA(final A a) {
            this.a = a;
        }

    }

    @Test
    public void testNotNullableDependencyNotExists() {
        assertThrows(IllegalStateException.class, () -> {
            final InjectionContext context = new DefaultInjectionContext();

            context.register(singleton(B.class)
                    .withConstructor(paramOfType(A.class))
                    .withSetter(B::setA, A.class));

            context.initialize();

            final B b = context.get(B.class);
            System.out.println(b);
        }, "Exception expected as A hasn't been registered and B requires it (not-nullable dep)");
    }

    @Test
    public void testNotNullableDependencyExists() throws Exception {
        final InjectionContext context = new DefaultInjectionContext();

        context.register(singleton(A.class));
        context.register(singleton(B.class).withConstructor(paramOfType(A.class)));

        context.initialize();

        final B b = context.get(B.class);
        assertNotNull(b, "Instance 'b' must not be null");
        assertNotNull(b.a, "Member 'a' of 'b' must not be null");
    }

}
