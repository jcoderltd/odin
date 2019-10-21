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
package io.jcoder.odin.examples.basic;

import javax.inject.Singleton;

/**
 * Represents a service class for {@link User} management.
 * 
 * <p>
 * This is an empty class as it's meant only for the DI example
 * 
 * @author Camilo Gonzalez
 */
@Singleton
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User processUserUpdate(User user) {
        return userRepository.save(user);
    }

    @Override
    public String toString() {
        return "UserService [userRepository=" + userRepository + "]";
    }

}
