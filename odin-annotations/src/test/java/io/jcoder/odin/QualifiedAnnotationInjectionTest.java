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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.junit.jupiter.api.Test;

/**
 *
 * @author Camilo Gonzalez
 */
public class QualifiedAnnotationInjectionTest {

    @Singleton
    public static class A {
    }

    @Singleton
    public static class SubA extends A {
    }

    @Singleton
    public static class B {
        @Inject
        @SampleQualifier
        private A a;

        @Inject
        private A a2;
    }

    @Test
    public void test() throws Exception {
        InjectionContext context = new DefaultInjectionContext();
        context.register(annotated(SubA.class).qualifiedBy(SampleQualifier.class.getName()));
        context.register(annotated(A.class));
        context.register(annotated(B.class));
        context.initialize();

        A a = context.get(A.class);
        B b = context.get(B.class);

        assertNotNull(a);
        assertNotNull(b);

        assertTrue(b.a instanceof SubA);
        assertFalse(b.a2 instanceof SubA);
    }
}
