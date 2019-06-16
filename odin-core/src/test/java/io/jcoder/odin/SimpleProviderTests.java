/*
 * Copyright 2019 - JCoder Ltd
 */
package io.jcoder.odin;

import static io.jcoder.odin.builder.ReferenceBuilder.paramOfType;
import static io.jcoder.odin.builder.RegistrationBuilder.singleton;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.inject.Provider;

import org.junit.jupiter.api.Test;

/**
 * 
 * @author Camilo Gonzalez
 */
public class SimpleProviderTests {
    public static class A {
    }

    public static class B {
        private Provider<A> providerOfA;

        public B(Provider<A> providerOfA) {
            this.providerOfA = providerOfA;
        }
    }

    @Test
    public void providerTest() throws Exception {
        final InjectionContext context = new DefaultInjectionContext();

        context.register(singleton(A.class));
        context.register(singleton(B.class)
                .withConstructor(paramOfType(A.class).asProvider()));

        context.initialize();

        B b = context.get(B.class);
        assertNotNull(b);
        assertNotNull(b.providerOfA.get());
        assertEquals(context.get(A.class), b.providerOfA.get());
    }
}
