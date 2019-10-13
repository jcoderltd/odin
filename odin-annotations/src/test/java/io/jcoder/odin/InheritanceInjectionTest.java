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
import static io.jcoder.odin.builder.RegistrationBuilder.object;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

        assertNotNull(b.a2);
        assertNotNull(b.subA2);

        assertNotNull(b.a);
        assertNotNull(b.a3);
        assertNotNull(b.a4);

        B b2 = context.get(B.class);
        assertSame(b, b2);

        assertNotNull(b2.a4);
    }
}
