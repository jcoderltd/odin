/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin;

import static io.jcoder.odin.builder.ReferenceBuilder.ofType;
import static io.jcoder.odin.builder.RegistrationBuilder.singleton;
import static io.jcoder.odin.builder.RegistrationBuilder.type;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 *
 * @author Camilo Gonzalez
 */
public class SimpleFieldTests {

    public static class A {
        private B b1;

        private B b2;

        private void setB2(B b) {
            assertNotNull(b1, "b1 shouldn't be null at this stage");
            this.b2 = b;
        }
    }

    public static class B {
    }

    @Test
    public void test() throws Exception {
        InjectionContext context = new DefaultInjectionContext();
        context.register(type(B.class));
        context.register(singleton(A.class)
                .withField("b1", ofType(B.class))
                .withSetter(A::setB2, B.class));
        context.initialize();

        A a = context.get(A.class);
        assertNotNull(a, "a must not be null");
        assertNotNull(a.b1, "a.b1 must not be null");
        assertNotNull(a.b2, "a.b2 must not be null");
        assertTrue(a.b1 != a.b2, "a.b1 must be different than a.b2 (b isn't singleton)");
    }
}
