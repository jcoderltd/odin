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
package io.jcoder.odin.component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.junit.jupiter.api.Test;

import io.jcoder.odin.annotation.ComponentBasedInjectionContext;
import io.jcoder.odin.annotation.component.Component;
import io.jcoder.odin.annotation.component.Registration;

/**
 * 
 * @author Camilo Gonzalez
 */
public class ComponentWithFactoryTest {

    public static class A {
        private static int NUM_INSTANCE = 0;

        {
            NUM_INSTANCE++;
        }
    }

    public static class B {
        A a;

        public B(A a) {
            this.a = a;
        }
    }

    @Component
    public static class TestComponent {
        @Inject
        B b;

        @Registration
        @Singleton
        private A a() {
            return new A();
        }

        @Registration
        @Singleton
        private B b(A a) {
            return new B(a);
        }

    }

    @Test
    public void testSingleInstances() {
        ComponentBasedInjectionContext context = new ComponentBasedInjectionContext();
        context.addComponent(TestComponent.class);
        context.initialize();

        TestComponent c = context.get(TestComponent.class);
        assertNotNull(c);
        assertNotNull(c.b);
        assertNotNull(c.b.a);

        assertEquals(1, A.NUM_INSTANCE);
    }
}
