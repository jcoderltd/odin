/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin.registration;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.google.common.collect.ImmutableList;

import io.jcoder.odin.DefaultInjectionContext.UnscopedInstanceScope;
import io.jcoder.odin.InjectionContext;
import io.jcoder.odin.ObjectCreationException;
import io.jcoder.odin.builder.RegistrationBuilder;
import io.jcoder.odin.function.ConstructionFunction;
import io.jcoder.odin.function.InjectionFunction;
import io.jcoder.odin.function.PostConstructionFunction;
import io.jcoder.odin.function.PreDestroyFunction;
import io.jcoder.odin.reference.InjectableReference;
import io.jcoder.odin.scope.InstanceScope;
import io.jcoder.odin.scope.SingletonScope;

/**
 * Represents the registration of a class or object inside an {@link InjectionContext}.
 * 
 * <p>
 * In general, users can build an {@link InjectionRegistration} making use of the {@link RegistrationBuilder} helper
 * class.
 * 
 * <p>
 * An {@link InjectionRegistration} contains the different details that should be managed by the
 * {@link InjectionContext} on behalf of the user, including {@link InjectionRegistration} should detail:
 * 
 * <ul>
 * <li><b>Construction:</b> How to construct an object if this registration is represented by a <code>class</code>.
 * Construction details are not required when the {@link InjectionRegistration} is for an object that has already been
 * created outside of the {@link InjectionContext}</li>
 * <li><b>Setters:</b> [Optional] Additional injections that should be performed to created objects</li>
 * <li><b>Post Construction Function:</b> [Optional] Function that should be invoked in the created objects after
 * they've been constructed and all injections have been performed.</li>
 * <li><b>Pre Destroy Function:</b> [Optional] Function that should be invoked in a managed object before they're
 * destroyed by the container. In a similar way to Java's <code>finalize</code> method, users shouldn't rely on this
 * object being invoked.</li>
 * </ul>
 * 
 * <p>{@link InjectionRegistration} objects are immutable.
 *
 * @author Camilo Gonzalez
 */
public final class InjectionRegistration<T> implements Comparable<InjectionRegistration<T>> {
    private final String name;

    private final Class<T> registeredClass;

    private final T registeredObject;

    private final List<InjectionFunction<T>> setters;

    private final PostConstructionFunction<T> postConstructor;

    private final PreDestroyFunction<T> preDestroy;

    private final ConstructionFunction<T> constructor;

    private final Class<? extends InstanceScope> scopeType;

    private final boolean provided;

    public InjectionRegistration(Class<? extends InstanceScope> scopeType, String name, Class<T> registeredClass,
            ConstructionFunction<T> constructor, List<InjectionFunction<T>> setters,
            PostConstructionFunction<T> postConstructor,
            PreDestroyFunction<T> preDestroy) {

        this.name = name;
        this.registeredClass = registeredClass;
        this.scopeType = scopeType;
        this.constructor = constructor;
        if (setters != null) {
            this.setters = ImmutableList.sortedCopyOf((s1, s2) -> s1.priority() - s2.priority(), setters);
        } else {
            this.setters = ImmutableList.of();
        }
        this.registeredObject = null;
        this.provided = false;
        this.postConstructor = postConstructor;
        this.preDestroy = preDestroy;
    }

    @SuppressWarnings("unchecked")
    public InjectionRegistration(String name, T registeredObject, List<InjectionFunction<T>> setters,
            PostConstructionFunction<T> postConstructor,
            PreDestroyFunction<T> preDestroy) {

        this.name = name;
        this.registeredObject = registeredObject;
        this.scopeType = SingletonScope.class;
        this.constructor = null;
        if (setters != null) {
            this.setters = ImmutableList.copyOf(setters);
        } else {
            this.setters = ImmutableList.of();
        }
        this.registeredClass = (Class<T>) registeredObject.getClass();
        this.provided = true;
        this.postConstructor = postConstructor;
        this.preDestroy = preDestroy;
    }

    public String getName() {
        return name;
    }

    public Class<T> getRegisteredClass() {
        return registeredClass;
    }

    public ConstructionFunction<T> getConstructor() {
        return constructor;
    }

    public Class<? extends InstanceScope> getScopeType() {
        return scopeType;
    }

    public void destroy(T value) {
        if (value != null && preDestroy != null) {
            preDestroy.preDestroy(value);
        }
    }

    /**
     * Indicates if the object to be used has already been provided.
     *
     * @return true if the object has been provided for this registration, false if the object will need to be
     *         constructed.
     */
    public boolean isProvided() {
        return provided;
    }

    public T get(final InjectionContext context) {
        boolean isUnscoped = UnscopedInstanceScope.class.equals(scopeType);
        InstanceScope scope = context.getScope(scopeType);
        try {
            T scopeInstance = scope.getInstance(this);
            if (registeredObject != null) {
                if (scopeInstance == null) {
                    scope.setInstance(this, registeredObject);
                    invokeSetters(context, registeredObject);
                }
                return registeredObject;
            }

            // this resolves the simple cycle resolution strategy where some classes use constructor injection and
            // others use setter injection to bypass the construction cycle
            prepareScopedDependencies(context);

            scopeInstance = scope.getInstance(this);

            if (!isUnscoped && scopeInstance != null) {
                return scopeInstance;
            }

            T object = constructor.newObject(context);
            scope.setInstance(this, object);
            invokeSetters(context, object);

            if (postConstructor != null) {
                postConstructor.postConstruct(object);
            }
            return object;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            scope.setInstance(this, null);
            throw new ObjectCreationException("Exception creating instance of type: " + registeredClass, ex);
        }
    }

    private void prepareScopedDependencies(InjectionContext context) {
        if (constructor == null) {
            throw new IllegalStateException("Trying to get construction parameters when no constructor is available: " + this.toString());
        }

        for (InjectableReference<?> param : constructor.parameters()) {
            for (InjectionRegistration<?> registration : param.getRegistrations(context)) {
                if (param.isNullable() && registration == null) {
                    continue;
                }

                boolean isUnscoped = UnscopedInstanceScope.class.equals(registration.getScopeType());

                if (!isUnscoped) {
                    registration.get(context);
                }
            }
        }
    }

    private void invokeSetters(InjectionContext context, T instance) {
        for (InjectionFunction<T> ref : setters) {
            ref.apply(context, instance);
        }
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((registeredClass == null) ? 0 : registeredClass.hashCode());
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
        InjectionRegistration<?> other = (InjectionRegistration<?>) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (registeredClass == null) {
            if (other.registeredClass != null) {
                return false;
            }
        } else if (!registeredClass.equals(other.registeredClass)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(InjectionRegistration<T> o) {
        if (!registeredClass.equals(o.registeredClass)) {
            return name.compareTo(o.name);
        }
        return 0;
    }

    @Override
    public String toString() {
        return "[name=" + name + ", registeredClass=" + registeredClass + "]";
    }

}
