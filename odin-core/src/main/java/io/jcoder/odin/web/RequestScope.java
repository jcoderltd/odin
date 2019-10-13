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
