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

import static io.jcoder.odin.annotation.builder.AnnotationAwareRegistrationBuilder.annotated;

import io.jcoder.odin.DefaultInjectionContext;
import io.jcoder.odin.InjectionContext;

/**
 * Entry point for our example application.
 * 
 * @author Camilo Gonzalez
 */
public class UserApp {

    public static void main(String[] args) throws Exception {
        InjectionContext context = new DefaultInjectionContext();
        context.register(annotated(UserService.class));
        context.register(annotated(UserRepository.class));
        context.initialize();
        
        UserService userService = context.get(UserService.class);
        User user = new User(1, "abc@somedomain.com", "User Name");
        userService.processUserUpdate(user);
    }

}
