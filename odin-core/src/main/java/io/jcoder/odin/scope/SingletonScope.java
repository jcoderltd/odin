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
