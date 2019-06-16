/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin.scope;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.jcoder.odin.registration.InjectionRegistration;

/**
 *
 * @author Camilo Gonzalez
 */
public class SingletonScope implements InstanceScope {

    private final static Logger logger = LoggerFactory.getLogger(SingletonScope.class);

    private final ConcurrentMap<InjectionRegistration<?>, Object> objectPerRegistration = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getInstance(InjectionRegistration<T> registration) {
        return (T) objectPerRegistration.get(registration);
    }

    @Override
    public <T> boolean setInstance(InjectionRegistration<T> registration, T object) {
        if (objectPerRegistration.containsKey(registration)) {
            return false;
        }
        objectPerRegistration.put(registration, object);
        return true;
    }

    @Override
    public void destroy() {
        objectPerRegistration.entrySet().forEach(this::destroyObject);
    }

    @SuppressWarnings("unchecked")
    private <T> void destroyObject(Entry<InjectionRegistration<?>, Object> entry) {
        try {
            InjectionRegistration<T> registration = (InjectionRegistration<T>) entry.getKey();
            registration.destroy((T) entry.getValue());
        } catch (Exception ex) {
            logger.warn("Exception destroying object of type: {}", entry.getKey().getRegisteredClass(), ex);
        }
    }

}
