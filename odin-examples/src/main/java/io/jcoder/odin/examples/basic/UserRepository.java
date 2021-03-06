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
 * Represents a repository for {@link User} data.
 * 
 * <p>
 * This is an empty class as it's meant only for the DI example
 * 
 * @author Camilo Gonzalez
 */
@Singleton
public class UserRepository {

    private static int NUM_INSTANCE = 0;

    private int instance;

    public UserRepository() {
        this.instance = NUM_INSTANCE++;
    }

    public User get(long id) {
        System.out.println("Retrieving user with id: " + id);
        return new User(id, "Name", "email@somedomain.com");
    }

    public User save(User user) {
        System.out.println("Saving user: " + user);
        return user;
    }

    @Override
    public String toString() {
        return "UserRepository [instance=" + instance + "]";
    }

}
