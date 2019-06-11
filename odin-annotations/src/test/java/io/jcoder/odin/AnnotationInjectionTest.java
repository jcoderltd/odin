/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin;

import static io.jcoder.odin.annotation.builder.AnnotationAwareRegistrationBuilder.annotated;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.junit.jupiter.api.Test;

import com.google.common.base.Preconditions;

/**
 *
 * @author Camilo Gonzalez
 */
public class AnnotationInjectionTest {

    @Singleton
    public static class A {
        private final B b;

        public A(B b) {
            this.b = b;
        }
    }

    @Singleton
    public static class B {
        private A a;

        @Inject
        private A a2;

        private boolean postInvoked = false;

        @Inject
        public void setA(A a) {
            assertNotNull(a2, "a2 must not be null at this stage");
            this.a = a;
        }

        @PostConstruct
        void initialize() {
            Preconditions.checkNotNull(this.a, "A's references hasn't been set!");
            Preconditions.checkNotNull(this.a2, "A's references hasn't been set!");
            this.postInvoked = true;
        }
    }

    @Test
    public void test() throws Exception {
        InjectionContext context = new DefaultInjectionContext();
        context.register(annotated(A.class));
        context.register(annotated(B.class));
        context.initialize();

        A a = context.get(A.class);
        B b = context.get(B.class);

        assertEquals(a, b.a);
        assertEquals(b, a.b);
        assertEquals(b.a, b.a2);
        assertTrue(b.postInvoked);
    }
}
