/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import io.jcoder.odin.InjectionContext;
import io.jcoder.odin.registration.InjectionRegistration;

/**
 * Represents a graph of dependencies across {@link InjectionRegistration} objects.
 *
 * @author Camilo Gonzalez
 */
public class DependencyGraph {

    private final InjectionContext context;

    private final Collection<InjectionRegistration<?>> registrations;

    private final Map<InjectionRegistration<?>, Node> nodeMap;

    public DependencyGraph(InjectionContext context, DependencyProvider provider) {
        Preconditions.checkNotNull(context, "The provided injection context must not be null");
        this.context = context;

        this.registrations = context.getRegistrations();
        this.nodeMap = new HashMap<>();
        buildGraph(provider);
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
            Preconditions.checkArgument(idx >= 0, "The provided node for the cycle wasn't found in the given path");

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

}
