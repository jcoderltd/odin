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
package io.jcoder.odin.graph;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import io.jcoder.odin.InjectionContext;
import io.jcoder.odin.base.Preconditions;
import io.jcoder.odin.registration.InjectionRegistration;

/**
 * Represents a graph of dependencies across {@link InjectionRegistration} objects.
 *
 * @author Camilo Gonzalez
 */
public class DependencyGraph {

    private final AtomicInteger nextId = new AtomicInteger();

    private final InjectionContext context;

    private final Collection<InjectionRegistration<?>> registrations;

    private final Map<InjectionRegistration<?>, Node> nodeMap;

    public DependencyGraph(InjectionContext context, DependencyProvider provider) {
        Preconditions.verifyNotNull(context, "The provided injection context must not be null");
        this.context = context;

        this.registrations = context.getRegistrations();
        this.nodeMap = new HashMap<>();
        buildGraph(provider);
    }

    /**
     * Provides the list of detected cycles in the graph, or an empty list if there are no cycles.
     *
     * <p>
     * Each element of the returned list is a cycle in the dependency graph.
     *
     * <p>
     * This method doesn't guarantee finding all cycles in the graph, but guarantees to find at least one cycle if one
     * or more exist.
     */
    public List<Cycle> getCycles() {
        final List<Cycle> cycles = new ArrayList<>();
        final List<Node> currentPath = new ArrayList<>();
        final Set<Node> checked = new HashSet<>();

        for (final Node node : nodeMap.values()) {
            currentPath.add(node);
            checkForCycles(checked, node, currentPath, cycles);
            currentPath.remove(node);
        }

        return cycles;
    }

    public Set<Node> getNodes() {
        return new HashSet<>(nodeMap.values());
    }

    private void buildGraph(DependencyProvider provider) {
        for (final InjectionRegistration<?> reg : registrations) {
            final Node regNode = nodeMap.computeIfAbsent(reg, Node::new);
            final Collection<InjectionRegistration<?>> dependencies = provider.dependencies(context, reg);

            for (final InjectionRegistration<?> dep : dependencies) {
                final Node depNode = nodeMap.computeIfAbsent(dep, Node::new);
                regNode.outgoing.add(depNode);
                depNode.incoming.add(regNode);
            }

        }
    }

    private void checkForCycles(Set<Node> checked, Node node, List<Node> current, List<Cycle> cyclesFound) {
        if (checked.contains(node)) {
            return;
        }
        checked.add(node);

        for (final Node dep : node.outgoing) {
            if (current.contains(dep)) {
                cyclesFound.add(new Cycle(current, dep));
            } else {
                current.add(dep);
                checkForCycles(checked, dep, current, cyclesFound);
                current.remove(dep);
            }
        }

    }

    public class Cycle {
        private final List<Node> cyclePath = new ArrayList<>();

        public Cycle(List<Node> path, Node lastNode) {
            final int idx = path.indexOf(lastNode);
            Preconditions.verifyArgumentCondition(idx >= 0, "The provided node for the cycle wasn't found in the given path");

            cyclePath.addAll(path.subList(idx, path.size()));
            cyclePath.add(lastNode);
        }

        /**
         * Returns a list of {@link InjectionRegistration} objects that represent this dependency cycle.
         *
         * The first and last element of the list are the same {@link InjectionRegistration}.
         *
         * @return the list of registrations that represent the cycle.
         */
        public List<InjectionRegistration<?>> getInjectionRegistrations() {
            return cyclePath.stream().map(n -> n.registration).collect(Collectors.toList());
        }

        @Override
        public String toString() {
            boolean first = true;
            final StringBuilder builder = new StringBuilder("Cycle: [");
            for (final Node n : cyclePath) {
                if (!first) {
                    builder.append(", ");
                }
                builder.append(n.registration.getName());
                first = false;
            }
            builder.append("]");
            return builder.toString();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((cyclePath == null) ? 0 : cyclePath.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Cycle other = (Cycle) obj;
            if (cyclePath == null) {
                if (other.cyclePath != null) {
                    return false;
                }
            } else if (!cyclePath.equals(other.cyclePath)) {
                return false;
            }
            return true;
        }

    }

    public class Node {
        private final String id;

        private final String label;

        /**
         * The {@link InjectionRegistration} that this node represents.
         */
        private final InjectionRegistration<?> registration;

        /**
         * The incoming dependencies - as in, what registrations depend on this one.
         */
        private final List<Node> incoming;

        /**
         * The outgoing dependencies - as in, the registrations that this registration depends on.
         */
        private final List<Node> outgoing;

        public Node(InjectionRegistration<?> registration) {
            this.id = "node" + nextId.getAndIncrement();
            if (registration != null) {
                this.label = registration.getName();
            } else {
                this.label = "nullable";
            }
            this.registration = registration;
            this.incoming = new ArrayList<>();
            this.outgoing = new ArrayList<>();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((registration == null) ? 0 : registration.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Node other = (Node) obj;
            if (registration == null) {
                if (other.registration != null) {
                    return false;
                }
            } else if (!registration.equals(other.registration)) {
                return false;
            }
            return true;
        }

    }

    public void generateDotFile(OutputStream output) {
        BufferedWriter buffOut = new BufferedWriter(new OutputStreamWriter(output));
        PrintWriter out = new PrintWriter(buffOut);

        out.println("digraph injectionContext {");
        for (Node node : nodeMap.values()) {
            out.println(String.format("    /* Start of node: %s */", node.label));
            out.println(String.format("    %s [label=\"%s\"];", node.id, node.label));
            for (Node dep : node.outgoing) {
                out.println(String.format("    %s -> %s;", node.id, dep.id));
            }
            out.println(String.format("    /* End of node: %s */", node.label));
            out.println();
            out.flush();
        }
        out.println("}");
        out.flush();
    }

}
