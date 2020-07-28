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

import java.util.Set;

import javax.inject.Provider;

import org.junit.jupiter.api.Test;

import io.jcoder.odin.graph.CompleteDependencyProvider;
import io.jcoder.odin.graph.DependencyGraph;
import io.jcoder.odin.graph.DependencyGraph.Node;

/**
 *
 * @author Camilo Gonzalez
 */
public class SimpleProviderTests {
    public static class A {
    }

    public static class B {
        private final Provider<A> providerOfA;

        public B(Provider<A> providerOfA) {
            this.providerOfA = providerOfA;
        }
    }

    @Test
    public void providerTest() throws Exception {
        final InjectionContext context = new DefaultInjectionContext();

        context.register(singleton(A.class));
        context.register(singleton(B.class)
                .withConstructor(paramOfType(A.class).asProvider()));

        context.initialize();

        B b = context.get(B.class);
        assertNotNull(b);
        assertNotNull(b.providerOfA.get());
        assertEquals(context.get(A.class), b.providerOfA.get());

        DependencyGraph graph = new DependencyGraph(context, new CompleteDependencyProvider());
        Set<Node> nodes = graph.getNodes();
        assertEquals(4, nodes.size());
    }
}
