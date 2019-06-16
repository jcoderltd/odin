/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin;

import static io.jcoder.odin.annotation.builder.AnnotationAwareRegistrationBuilder.annotated;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.junit.jupiter.api.Test;

/**
 *
 * @author Camilo Gonzalez
 */
public class ProviderAnnotationInjectionTest {

    @Singleton
    public static class A {
    }

    @Singleton
    public static class SubA extends A {
    }

    @Singleton
    public static class B {
        @Inject
        @SampleQualifier
        private Provider<A> a;
    }

    @Singleton
    public static class C {
        private final Provider<A> providerOfA;

        public C(Provider<A> providerOfA) {
            this.providerOfA = providerOfA;
        }
    }

    @Test
    public void test() throws Exception {
        InjectionContext context = new DefaultInjectionContext();
        context.register(annotated(SubA.class).qualifiedBy(SampleQualifier.class.getName()));
        context.register(annotated(A.class));
        context.register(annotated(B.class));
        context.register(annotated(C.class));
        context.initialize();

        A a = context.get(A.class);
        B b = context.get(B.class);
        C c = context.get(C.class);

        assertNotNull(a);
        assertNotNull(b);
        assertNotNull(c);

        assertTrue(b.a.get() instanceof SubA);
        assertFalse(c.providerOfA.get() instanceof SubA);
    }
}
