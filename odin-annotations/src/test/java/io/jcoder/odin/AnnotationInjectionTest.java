/*
 *  Copyright 2019 JCoder Ltd.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.jcoder.odin;

import static io.jcoder.odin.annotation.builder.AnnotationAwareRegistrationBuilder.annotated;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.junit.jupiter.api.Test;

import io.jcoder.odin.annotation.PostConstruct;
import io.jcoder.odin.base.Preconditions;

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
            Preconditions.verifyNotNull(this.a, "A's references hasn't been set!");
            Preconditions.verifyNotNull(this.a2, "A's references hasn't been set!");
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
