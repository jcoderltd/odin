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
package io.jcoder.odin.examples.component.provider;

import javax.inject.Provider;
import javax.inject.Singleton;

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

    private final Provider<UserRepository> userRepositoryProvider;

    public LoginService(Provider<UserRepository> userRepository) {
        this.userRepositoryProvider = userRepository;
    }

    public User login(int userId) {
        UserRepository userRepo = userRepositoryProvider.get();
        System.out.println(userRepo);
        return userRepo.get(userId);
    }

}
