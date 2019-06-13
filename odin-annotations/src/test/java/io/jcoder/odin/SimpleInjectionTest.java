/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin;

import static io.jcoder.odin.annotation.builder.AnnotationAwareRegistrationBuilder.annotated;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

import org.junit.jupiter.api.Test;

/**
 *
 * @author Camilo Gonzalez
 */
public class SimpleInjectionTest {

    @Singleton
    public static class A {
        private final B b;

        public A(B b) {
            this.b = b;
        }
        
        @PostConstruct
        void initialize() {
            System.out.println("A is initialized");
        }
    }

    @Singleton
    public static class B {
        @PostConstruct
        void initialize() {
            System.out.println("B is initialized");
        }
    }

    @Test
    public void test() throws Exception {
        InjectionContext context = new DefaultInjectionContext();
        context.register(annotated(A.class));
        context.register(annotated(B.class));
        context.initialize();

        A a = context.get(A.class);
        
        System.out.println("A has a reference to B: " + a.b);
    }
}
