/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.inject.Provider;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import io.jcoder.odin.builder.RegistrationBuilder;
import io.jcoder.odin.graph.ConstructionDependencyProvider;
import io.jcoder.odin.graph.DependencyGraph;
import io.jcoder.odin.graph.DependencyGraph.Cycle;
import io.jcoder.odin.registration.InjectionRegistration;
import io.jcoder.odin.scope.InstanceScope;
import io.jcoder.odin.scope.SingletonScope;

/**
 *
 * @author Camilo Gonzalez
 */
public class DefaultInjectionContext implements InjectionContext {

    private final Set<InjectionRegistration<?>> registry;

    private final Map<InjectionRegistration<?>, InjectionRegistration<Provider<?>>> providersRegistry;

    private final ConcurrentMap<Class<? extends InstanceScope>, InstanceScope> registeredScopes;

    private volatile boolean initialized;

    public DefaultInjectionContext() {
        this.registry = new ConcurrentSkipListSet<>();
        this.providersRegistry = new ConcurrentHashMap<>();
        this.registeredScopes = new ConcurrentHashMap<>();
        registerScope(new UnscopedInstanceScope());
        registerScope(new SingletonScope());
    }

    @Override
    public boolean initialized() {
        return initialized;
    }

    @Override
    public <T> InjectionRegistration<Provider<?>> getProviderRegistration(InjectionRegistration<T> registration) {
        InjectionRegistration<Provider<?>> providerRegistration = providersRegistry.computeIfAbsent(registration,
                reg -> {
                    Provider<T> provider = new Provider<T>() {
                        @Override
                        public T get() {
                            return registration.get(DefaultInjectionContext.this);
                        }
                    };
                    return new InjectionRegistration<>(Provider.class.getName() + "-" + registration.getName(), registration.getQualifierName(), provider, null, null, null);
                });
        return providerRegistration;
    }

    @Override
    public <T> InjectionRegistration<T> getRegistration(Class<T> objectClass) {
        Preconditions.checkNotNull(objectClass, "The requested class must not be null");

        Predicate<InjectionRegistration<T>> registrationFilter = reg -> {
            return reg.getQualifierName() == null && objectClass.isAssignableFrom(reg.getRegisteredClass());
        };
        Predicate<InjectionRegistration<T>> tieFilter = reg -> {
            return reg.getName().equals(reg.getRegisteredClass().getName());
        };
        return registrationFor(objectClass, registrationFilter, tieFilter);
    }

    @Override
    public <T> InjectionRegistration<T> getNamedRegistration(Class<T> objectClass, String name) {
        Preconditions.checkNotNull(objectClass, "The requested class must not be null");
        Preconditions.checkNotNull(name, "The requested name must not be null");

        Predicate<InjectionRegistration<T>> registrationFilter = reg -> {
            return reg.getQualifierName() == null && name.equals(reg.getName()) && objectClass.isAssignableFrom(reg.getRegisteredClass());
        };
        return registrationFor(objectClass, registrationFilter);
    }

    @Override
    public <T> InjectionRegistration<T> getQualifiedRegistration(Class<T> objectClass, String qualifierName) {
        Preconditions.checkNotNull(objectClass, "The requested class must not be null");
        Preconditions.checkNotNull(qualifierName, "The requested qualifier name must not be null");

        Predicate<InjectionRegistration<T>> registrationFilter = reg -> {
            return qualifierName.equals(reg.getQualifierName()) && objectClass.isAssignableFrom(reg.getRegisteredClass());
        };
        return registrationFor(objectClass, registrationFilter);
    }

    @Override
    public <T> List<InjectionRegistration<T>> getMultiRegistration(Class<T> objectClass) {
        Preconditions.checkNotNull(objectClass, "The requested class must not be null");

        Predicate<InjectionRegistration<T>> registrationFilter = reg -> {
            return objectClass.isAssignableFrom(reg.getRegisteredClass());
        };

        return registrationsFor(objectClass, registrationFilter);
    }

    @Override
    public <T> T get(Class<T> objectClass) {
        checkInitialized();

        InjectionRegistration<T> registration = getRegistration(objectClass);
        return registration == null ? null : registration.get(this);
    }

    @Override
    public <T> T getNamed(Class<T> objectClass, String name) {
        checkInitialized();

        InjectionRegistration<T> registration = getNamedRegistration(objectClass, name);
        return registration == null ? null : registration.get(this);
    }

    @Override
    public <T> T getWithQualifier(Class<T> objectClass, String qualifierName) {
        checkInitialized();

        InjectionRegistration<T> registration = getQualifiedRegistration(objectClass, qualifierName);
        return registration == null ? null : registration.get(this);
    }

    @Override
    public <T> List<T> getMulti(Class<T> objectClass) {
        checkInitialized();

        List<InjectionRegistration<T>> registrations = getMultiRegistration(objectClass);
        return registrations.stream().map(reg -> reg.get(this)).collect(Collectors.toList());
    }

    private <T> List<InjectionRegistration<T>> registrationsFor(Class<T> objectClass,
            Predicate<InjectionRegistration<T>> registrationFilter) {

        return getMatchingRegistrations(objectClass, registrationFilter);
    }

    private <T> InjectionRegistration<T> registrationFor(Class<T> requestedClass,
            Predicate<InjectionRegistration<T>> registrationFilter,
            Predicate<InjectionRegistration<T>> secondFilter) {

        List<InjectionRegistration<T>> matchingRegistrations = getMatchingRegistrations(requestedClass, registrationFilter);
        if (matchingRegistrations.size() > 1) {
            matchingRegistrations = matchingRegistrations.stream().filter(secondFilter).collect(Collectors.toList());
        }

        if (matchingRegistrations.size() > 1) {
            throw new IllegalArgumentException("Multiple registered instances/classes match the requested class: " + matchingRegistrations);
        }

        if (matchingRegistrations.size() == 1) {
            return matchingRegistrations.get(0);
        }

        return null;
    }

    private <T> InjectionRegistration<T> registrationFor(Class<T> requestedClass,
            Predicate<InjectionRegistration<T>> registrationFilter) {

        List<InjectionRegistration<T>> matchingRegistrations = getMatchingRegistrations(requestedClass, registrationFilter);
        if (matchingRegistrations.size() > 1) {
            throw new IllegalArgumentException("Multiple registered instances/classes match the requested class: " + matchingRegistrations);
        }

        if (matchingRegistrations.size() == 1) {
            return matchingRegistrations.get(0);
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private <T> List<InjectionRegistration<T>> getMatchingRegistrations(Class<T> requestedClass,
            Predicate<InjectionRegistration<T>> predicate) {

        return registry.stream()
                .filter(reg -> requestedClass.isAssignableFrom(reg.getRegisteredClass()))
                .map(reg -> (InjectionRegistration<T>) reg)
                .filter(predicate)
                .collect(Collectors.toList());
    }

    private void checkInitialized() {
        Preconditions.checkArgument(initialized, "This DefaultInjectionContext hasn't been initialized.");
    }

    @Override
    public synchronized void initialize() {
        if (initialized) {
            return;
        }

        if (!exists(InjectionContext.class)) {
            this.registry.add(new InjectionRegistration<>(InjectionContext.class.getName(), null, this, null, null, null));
        }

        DependencyGraph dependencyGraph = new DependencyGraph(this, new ConstructionDependencyProvider());
        List<Cycle> cycles = dependencyGraph.getCycles();
        if (!cycles.isEmpty()) {
            throw new IllegalStateException("Dependency cycles detected: " + cycles);
        }

        this.initialized = true;

        for (InjectionRegistration<?> reg : registry) {
            if (SingletonScope.class.equals(reg.getScopeType())) {
                reg.get(this);
            }
        }
    }

    @Override
    public <T> void register(InjectionRegistration<T> registration) {
        this.registry.add(registration);
    }

    @Override
    public <T> void register(RegistrationBuilder<T> registrationBuilder) throws NoSuchMethodException {
        this.registry.add(registrationBuilder.build());
    }

    @Override
    public <T> boolean exists(Class<T> objectClass) {
        return registry.stream().anyMatch(reg -> objectClass.isAssignableFrom(reg.getRegisteredClass()));
    }

    @Override
    public <T> List<InjectionRegistration<?>> getRegistrations() {
        return ImmutableList.<InjectionRegistration<?>>builder().addAll(registry).build();
    }

    @Override
    public <S extends InstanceScope> void registerScope(S scope) {
        Class<? extends InstanceScope> scopeType = scope.getClass();
        if (registeredScopes.containsKey(scopeType)) {
            throw new IllegalArgumentException("A scope of class: " + scopeType + " has already been registered.");
        }
        registeredScopes.put(scopeType, scope);
    }

    @Override
    public InstanceScope getScope(Class<? extends InstanceScope> scopeClass) {
        InstanceScope scope = registeredScopes.get(scopeClass);
        Preconditions.checkNotNull(scope, "No registered scope of type: " + scopeClass);
        return scope;
    }

    @Override
    public boolean hasScope(Class<? extends InstanceScope> scopeClass) {
        return registeredScopes.containsKey(scopeClass);
    }

    /**
     * A no-op {@link InstanceScope} implementation that represents an unmanaged scope of an {@link InjectionContext}.
     * 
     * <p>
     * For {@link InjectionRegistration} objects that make use of this scope, the {@link InjectionContext} will create a
     * new instance every time a <code>get</code> operation is invoked on them (in contrast to a Singleton scope).
     * 
     * @author Camilo Gonzalez
     */
    public final class UnscopedInstanceScope implements InstanceScope {

        private UnscopedInstanceScope() {
        }

        @Override
        public <T> T getInstance(InjectionRegistration<T> registration) {
            return null;
        }

        @Override
        public <T> boolean setInstance(InjectionRegistration<T> registration, T object) {
            return false;
        }

        @Override
        public void destroy() {
        }

    }

    @Override
    public void destroy() {
        registeredScopes.values().forEach(scope -> scope.destroy());
    }
}
