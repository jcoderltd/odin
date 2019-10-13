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

import static io.jcoder.odin.builder.RegistrationBuilder.singleton;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 *
 * @author Camilo Gonzalez
 */
public class SimpleCycleWithSetter {
    public static class A {
        private final B b;

        public A(final B b) {
            this.b = b;
        }
    }

    public static class B {
        private A a;

        public void setA(final A a) {
            this.a = a;
        }
    }

    @Test
    public void testSimpleCycle() throws Exception {
        final InjectionContext context = new DefaultInjectionContext();
        context.register(singleton(A.class));
        context.register(singleton(B.class).withSetter(B::setA, A.class));

        context.initialize();

        final A a = context.get(A.class);
        assertNotNull(a, "Instance 'a' must not be null");
        assertNotNull(a.b, "Member 'b' of 'a' must not be null");

        final B b = context.get(B.class);
        assertNotNull(b, "Instance 'b' must not be null");
        assertNotNull(b.a, "Member 'a' of 'b' must not be null");

        assertTrue(a.b == b, "B is a singleton and only instance must have been created");
        assertTrue(b.a == a, "A is a singleton and only instance must have been created");
    }

}
