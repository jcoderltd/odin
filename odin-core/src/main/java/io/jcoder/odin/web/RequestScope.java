/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin.web;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletRequest;

import io.jcoder.odin.registration.InjectionRegistration;
import io.jcoder.odin.scope.InstanceScope;
import io.jcoder.odin.scope.SingletonScope;

/**
 *
 * @author Camilo Gonzalez
 */
public class RequestScope implements InstanceScope {

    private final Map<ServletRequest, SingletonScope> perRequestScope = new ConcurrentHashMap<>();

    private final ThreadLocal<ServletRequest> threadRequest = new ThreadLocal<>();

    RequestScope() {
    }

    @Override
    public <T> T getInstance(InjectionRegistration<T> registration) {
        final ServletRequest request = threadRequest.get();
        if (request == null) {
            return null;
        }
        final SingletonScope scope = perRequestScope.get(request);
        return scope == null ? null : scope.getInstance(registration);
    }

    @Override
    public <T> boolean setInstance(InjectionRegistration<T> registration, T object) {
        final ServletRequest request = threadRequest.get();
        if (request == null) {
            return false;
        }
        final SingletonScope scope = perRequestScope.computeIfAbsent(request, req -> new SingletonScope());
        return scope.setInstance(registration, object);
    }

    void setRequest(ServletRequest request) {
        if (!perRequestScope.containsKey(request)) {
            perRequestScope.put(request, new SingletonScope());
        }
        threadRequest.set(request);
    }

    void unsetRequest(ServletRequest request) {
        threadRequest.remove();
        perRequestScope.remove(request);
    }

    @Override
    public void destroy() {
        perRequestScope.values().forEach(scope -> scope.destroy());
    }
}
