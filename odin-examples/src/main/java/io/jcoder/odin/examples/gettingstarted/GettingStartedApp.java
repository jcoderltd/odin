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
package io.jcoder.odin.examples.gettingstarted;

import static io.jcoder.odin.annotation.builder.AnnotationAwareRegistrationBuilder.annotated;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

import io.jcoder.odin.DefaultInjectionContext;
import io.jcoder.odin.InjectionContext;

/**
 * This is the first example shown in the Getting Started wiki page.
 * 
 * <p>
 * <a href="https://github.com/jcoderltd/odin/wiki/Getting-Started">Getting Started Wiki Page</a>
 * 
 * @author Camilo Gonzalez
 */
public class GettingStartedApp {
    @Singleton
    public static class A {
        private final B b;

        public A(B b) {
            this.b = b;
        }

        @PostConstruct
        void initialize() {
            System.out.println("A is initialized");
        }
    }

    @Singleton
    public static class B {
        @PostConstruct
        void initialize() {
            System.out.println("B is initialized");
        }
    }

    public static void main(String[] args) throws Exception {
        InjectionContext context = new DefaultInjectionContext();
        context.register(annotated(A.class));
        context.register(annotated(B.class));
        context.initialize();

        A a = context.get(A.class);
    }
}
