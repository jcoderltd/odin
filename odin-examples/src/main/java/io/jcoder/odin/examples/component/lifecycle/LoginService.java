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
package io.jcoder.odin.examples.component.lifecycle;

import javax.inject.Singleton;

import io.jcoder.odin.annotation.PostConstruct;
import io.jcoder.odin.annotation.PreDestroy;
import io.jcoder.odin.examples.basic.User;
import io.jcoder.odin.examples.basic.UserRepository;

/**
 * Represents a login class that depends on the {@link UserRepository}.
 * 
 * <p>
 * This is an empty class as it's meant only for the DI example
 * 
 * @author Camilo Gonzalez
 */
@Singleton
public class LoginService {

    private final UserRepository userRepository;

    public LoginService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User login(int userId) {
        return userRepository.get(userId);
    }

    @PostConstruct
    private void initialize() {
        System.out.println("Login Service initialized");
    }

    @PreDestroy
    private void terminate() {
        System.out.println("Login Service terminated");
    }
}
