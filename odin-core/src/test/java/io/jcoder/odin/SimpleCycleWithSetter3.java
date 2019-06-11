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
public class SimpleCycleWithSetter3 {
    public static class A {
        private final B b;

        public A(final B b) {
            this.b = b;
            System.out.println(this.b);
        }
    }

    public static class B {
        private A a;

        public void setA(final A a) {
            this.a = a;
            System.out.println(this.a);
        }
    }

    @Test
    public void test() throws Exception {
        assertThrows(StackOverflowError.class, () -> {
            final InjectionContext context = new DefaultInjectionContext();

            context.register(singleton(A.class));
            context.register(type(B.class).withSetter(B::setA, A.class));

            context.initialize();
        });
    }

}
