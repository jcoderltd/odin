/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin;

import static io.jcoder.odin.annotation.builder.AnnotationAwareRegistrationBuilder.annotated;
import static io.jcoder.odin.builder.RegistrationBuilder.object;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.junit.jupiter.api.Test;

/**
 *
 * @author Camilo Gonzalez
 */
public class InheritanceInjectionTest {

    private static final String NAME_VALUE = "someStringValue123!$%";

    @Singleton
    public static class A {
        @Inject
        @Named("teststring")
        private String name;

        @PostConstruct
        public void post() {
            assertEquals(NAME_VALUE, name);
        }
    }

    public static class B {
        protected A a;

        protected A a2;

        @Inject
        private A a4;

        @Inject
        public void setA(A a) {
            this.a = a;
        }

        @Inject
        private void setA2(A a) {
            assertNotNull(a);
            this.a2 = a;
        }

        @Inject
        public void setA3(A a) {
            throw new IllegalStateException("This method shouldn't be invoked");
        }
    }

    @Singleton
    public static class SubB extends B {

        private A subA2;

        private A a3;

        @Inject
        private A a4;

        @Inject
        private void setA2(A a) {
            this.subA2 = a;
        }

        @Override
        @Inject
        public void setA3(A a) {
            this.a3 = a;
        }
    }

    @Test
    public void test() throws Exception {
        InjectionContext context = new DefaultInjectionContext();
        context.register(object(NAME_VALUE).named("teststring"));
        context.register(annotated(A.class));
        context.register(annotated(SubB.class));
        context.initialize();

        A a = context.get(A.class);
        assertNotNull(a);
        assertEquals(NAME_VALUE, a.name);

        SubB b = context.get(SubB.class);
        assertNotNull(b);

        // a2 shouldn't be set as that method is hidden by the subclass
        assertNull(b.a2);
        assertNotNull(b.subA2);

        assertNotNull(b.a);
        assertNotNull(b.a3);
        assertNotNull(b.a4);

        B b2 = context.get(B.class);
        assertSame(b, b2);

        assertNotNull(b2.a4);
    }
}
