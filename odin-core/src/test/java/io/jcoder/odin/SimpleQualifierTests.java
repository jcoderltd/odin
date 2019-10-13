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

import static io.jcoder.odin.builder.ReferenceBuilder.paramOfType;
import static io.jcoder.odin.builder.RegistrationBuilder.singleton;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/**
 *
 * @author Camilo Gonzalez
 */
public class SimpleQualifierTests {

    public static class A {
        private String value;

        public A() {
        }

        public A(String value) {
            this.value = value;
        }
    }

    public static class B {
        private final A unqualified;

        private final A qualified;

        public B(A unqualified, A qualified) {
            this.unqualified = unqualified;
            this.qualified = qualified;
        }

    }

    @Test
    public void test() throws Exception {
        String qualifierName = "QualifierName";
        String stringName = "StringName";
        String stringValue = "StringValue";
        final InjectionContext context = new DefaultInjectionContext();

        context.register(singleton(String.class).named(stringName).withFactory((args) -> stringValue, new Class[] {}));
        context.register(singleton(A.class).qualifiedBy(qualifierName).withConstructor(paramOfType(String.class).named(stringName)));
        context.register(singleton(A.class).withConstructor(new Class[] {}));
        context.register(singleton(B.class)
                .withConstructor(
                        paramOfType(A.class),
                        paramOfType(A.class).qualifiedBy(qualifierName)));

        context.initialize();

        final A a = context.get(A.class);
        assertNotNull(a, "Instance 'a' must not be null");

        final B b = context.get(B.class);
        assertNotNull(b, "Instance 'b' must not be null");

        assertEquals(stringValue, b.qualified.value);
        assertNull(b.unqualified.value);
    }

}
