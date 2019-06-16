/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin.annotation.component;

import static io.jcoder.odin.annotation.builder.AnnotationAwareRegistrationBuilder.annotated;
import static io.jcoder.odin.annotation.reflection.AnnotationUtils.processParameterReferences;
import static io.jcoder.odin.annotation.reflection.AnnotationUtils.qualifierFromAnnotations;
import static io.jcoder.odin.builder.RegistrationBuilder.type;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import io.jcoder.odin.DefaultInjectionContext;
import io.jcoder.odin.InjectionContext;
import io.jcoder.odin.builder.RegistrationBuilder;

/**
 *
 * @author Camilo Gonzalez
 */
public class DefaultComponentRegistrar implements ComponentRegistrar {

    private final static Logger logger = LoggerFactory.getLogger(DefaultComponentRegistrar.class);

    private final InjectionContext context;

    private final Set<Class<?>> components = new LinkedHashSet<>();

    private final Set<Class<?>> leaves = new LinkedHashSet<Class<?>>();

    private boolean initialized = false;

    public DefaultComponentRegistrar() {
        this(new DefaultInjectionContext());
    }

    public DefaultComponentRegistrar(final InjectionContext context) {
        Preconditions.checkNotNull(context, "The provided InjectionContext must not be null");

        this.context = context;
    }

    @Override
    public InjectionContext injectionContext() {
        return this.context;
    }

    @Override
    public void addComponent(Class<?> componentToRegister) {
        Preconditions.checkState(!initialized, "This registrar has already been initialized, no further components might be added");

        getDependencies(componentToRegister, true);

        // all leaves should be registered in the InjectionContext
        leaves.forEach(classToRegister -> {
            try {
                context.register(annotated(classToRegister));
            } catch (Exception e) {
                throw new ComponentRegistrationException("Exception registering component: " + classToRegister, e);
            }
        });
    }

    @Override
    public void initialize() {
        if (initialized) {
            return;
        }

        initialized = true;
        // now, process all components
        for (Class<?> component : components) {
            processRegistrations(component);
        }

        context.initialize();
    }

    private void processRegistrations(final Class<?> component) {
        for (Field field : component.getDeclaredFields()) {
            if (field.isAnnotationPresent(Registration.class)) {
                try {
                    logger.debug("Registering annotated: " + field.getType());
                    Class<?> qualifier = qualifierFromAnnotations(field.getDeclaredAnnotations());
                    Named namedAnnotation = field.getAnnotation(Named.class);
                    boolean isSingleton = field.isAnnotationPresent(Singleton.class);
                    RegistrationBuilder<?> builder = annotated(field.getType());
                    
                    processBuilder(builder, isSingleton, namedAnnotation, qualifier);
                    
                    context.register(builder);
                } catch (Exception e) {
                    throw new ComponentRegistrationException(
                            "Couldn't register field " + component + "." + field.getName() + " of type: " + field.getType(), e);
                }
            }
        }

        for (Method method : component.getDeclaredMethods()) {
            if (Modifier.isStatic(method.getModifiers()) && method.isAnnotationPresent(Registration.class)) {
                method.setAccessible(true);

                Class<?> classToRegister = method.getReturnType();
                Class<?> qualifier = qualifierFromAnnotations(method.getDeclaredAnnotations());
                Named namedAnnotation = method.getAnnotation(Named.class);
                boolean isSingleton = method.isAnnotationPresent(Singleton.class);
                RegistrationBuilder<?> builder = type(classToRegister);
                
                processBuilder(builder, isSingleton, namedAnnotation, qualifier);

                try {
                    builder.withFactory(component, method.getName(), processParameterReferences(method.getParameters()));
                    logger.debug("Registering: " + builder);
                    context.register(builder);
                } catch (NoSuchMethodException e) {
                    throw new ComponentRegistrationException(
                            "Couldn't register " + method.getName() + " in " + component + " of type: " + classToRegister, e);
                }
            }
        }
    }

    protected void processBuilder(RegistrationBuilder<?> builder, boolean isSingleton, Named namedAnnotation, Class<?> qualifier) {
        if (isSingleton) {
            builder.asSingleton();
        }
        
        if (namedAnnotation != null) {
            builder.named(namedAnnotation.value());
        }
        
        if (namedAnnotation == null && qualifier != null && !qualifier.equals(Named.class)) {
            builder.qualifiedBy(qualifier.getName());
        }
    }

    private void getDependencies(Class<?> componentToRegister, boolean isLeaf) {

        if (componentToRegister == null || components.contains(componentToRegister)) {
            return;
        }

        getDependencies(componentToRegister.getSuperclass(), false);
        if (componentToRegister.isAnnotationPresent(Component.class)) {
            if (isLeaf) {
                leaves.add(componentToRegister);
            }
            components.add(componentToRegister);
        }

        ComponentDependency dependencyAnnotation = componentToRegister.getAnnotation(ComponentDependency.class);
        if (dependencyAnnotation != null) {
            Class<?>[] dependencyArray = dependencyAnnotation.value();
            for (Class<?> dependency : dependencyArray) {
                if (!dependency.isAnnotationPresent(Component.class)) {
                    throw new ComponentRegistrationException(
                            "The component class " + componentToRegister + " has non-component dependency: " + dependency
                                    + ". Is there an @Component annotations missing in the dependency?");
                }
                getDependencies(dependency, true);
            }
        }
    }

}
