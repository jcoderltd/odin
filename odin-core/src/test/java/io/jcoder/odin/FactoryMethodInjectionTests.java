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

import static io.jcoder.odin.builder.ReferenceBuilder.ofType;
import static io.jcoder.odin.builder.RegistrationBuilder.singleton;
import static io.jcoder.odin.builder.RegistrationBuilder.type;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 *
 * @author Camilo Gonzalez
 */
public class FactoryMethodInjectionTests {

    public interface Base {
    }

    public static class A implements Base {
    }

    public static class B implements Base {
    }

    public static class C {
    }

    public static class D {
        private final List<Base> baseObjects;

        private final C cObject;

        public D(final List<Base> baseObjects, final C cObject) {
            this.baseObjects = baseObjects;
            this.cObject = cObject;
        }

    }

    public static class TestFactory {

        private final List<Base> baseObjects;

        public TestFactory(final List<Base> baseObjects) {
            this.baseObjects = baseObjects;
        }

        public D create(C cObject) {
            return new D(baseObjects, cObject);
        }
    }

    @Test
    public void test() throws Exception {
        final InjectionContext context = new DefaultInjectionContext();

        context.register(singleton(A.class));
        context.register(type(B.class));
        context.register(singleton(C.class));
        context.register(singleton(TestFactory.class).withConstructor(ofType(List.class).multi().ofGenericType(Base.class)));
        context.register(singleton(D.class).withFactory(ofType(TestFactory.class), "create", ofType(C.class)));

        context.initialize();

        final A a = context.get(A.class);
        assertNotNull(a, "Instance 'a' must not be null");

        final B b = context.get(B.class);
        assertNotNull(b, "Instance 'b' must not be null");

        final D d = context.get(D.class);
        assertNotNull(d, "Instance 'd' must not be null");

        final D d2 = context.get(D.class);
        assertNotNull(d2, "Instance 'd2' must not be null");

        assertTrue(d == d2, "D is a singleton - only one instance must have been created");
        assertNotNull(d.cObject, "Member 'cObject' of 'd' must not be null");
        assertEquals(2, d.baseObjects.size(), "List of Base objects in D must have two elements");

        assertTrue(d.baseObjects.contains(a), "D's list must contain a reference to 'a' (A is a singleton)");
        assertFalse(d.baseObjects.contains(b), "D's list must not contain a reference to 'b' (B is not a singleton)");
    }

}
