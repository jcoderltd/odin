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
package io.jcoder.odin.examples.basic.component;

import io.jcoder.odin.annotation.component.Component;
import io.jcoder.odin.annotation.component.Registration;
import io.jcoder.odin.examples.basic.UserRepository;
import io.jcoder.odin.examples.basic.UserService;

/**
 * A component that contains the user related classes.
 * 
 * @author Camilo Gonzalez
 */
@Component
public class UserComponent {

    @Registration
    private final UserService userService;

    @Registration
    private final UserRepository userRepository;

    public UserComponent(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    public UserService getUserService() {
        return userService;
    }

}
