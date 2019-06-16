/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin;

import static io.jcoder.odin.annotation.builder.AnnotationAwareRegistrationBuilder.annotated;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.junit.jupiter.api.Test;

/**
 *
 * @author Camilo Gonzalez
 */
public class QualifiedAnnotationInjectionTest {

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
        private A a;

        @Inject
        private A a2;
    }

    @Test
    public void test() throws Exception {
        InjectionContext context = new DefaultInjectionContext();
        context.register(annotated(SubA.class).qualifiedBy(SampleQualifier.class.getName()));
        context.register(annotated(A.class));
        context.register(annotated(B.class));
        context.initialize();

        A a = context.get(A.class);
        B b = context.get(B.class);

        assertNotNull(a);
        assertNotNull(b);

        assertTrue(b.a instanceof SubA);
        assertFalse(b.a2 instanceof SubA);
    }
}
