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
import static io.jcoder.odin.builder.RegistrationBuilder.type;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 *
 * @author Camilo Gonzalez
 */
public class CycleTests2 {

    public static class A {
        public A(final B b) {
        }
    }

    public static class B {
        public B(final A a) {
        }
    }

    @Test
    public void testCycle() throws Exception {
        assertThrows(IllegalStateException.class, () -> {
            final InjectionContext context = new DefaultInjectionContext();

            context.register(singleton(A.class));
            context.register(type(B.class));

            context.initialize();
        }, "Exception expected due to the dependency cycle");
    }

}
