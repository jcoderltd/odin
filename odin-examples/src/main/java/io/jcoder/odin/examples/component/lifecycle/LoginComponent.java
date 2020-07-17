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

import javax.inject.Inject;

import io.jcoder.odin.annotation.PostConstruct;
import io.jcoder.odin.annotation.PreDestroy;
import io.jcoder.odin.annotation.component.Component;
import io.jcoder.odin.annotation.component.Registration;
import io.jcoder.odin.examples.basic.UserRepository;

/**
 * A component that contains the login related classes.
 * 
 * <p>
 * In this example we are creating a {@link LoginService} that gets an {@link UserRepository} instance and has a
 * {@link PostConstruct} and a {@link PreDestroy} method.
 * 
 * @author Camilo Gonzalez
 */
@Component
public class LoginComponent {

    @Inject
    @Registration
    private LoginService loginService;

    @Inject
    @Registration
    private UserRepository userRepository;

    public LoginService getLoginService() {
        return loginService;
    }

}
