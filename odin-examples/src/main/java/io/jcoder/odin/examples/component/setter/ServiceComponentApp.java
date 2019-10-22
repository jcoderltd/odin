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
package io.jcoder.odin.examples.component.setter;

import io.jcoder.odin.annotation.component.ComponentRegistrar;
import io.jcoder.odin.annotation.component.DefaultComponentRegistrar;

/**
 * Entry point for the {@link ServiceComponent} example.
 * 
 * @author Camilo Gonzalez
 */
public class ServiceComponentApp {

    public static void main(String[] args) throws Exception {
        ComponentRegistrar registrar = new DefaultComponentRegistrar();
        registrar.addComponent(ServiceComponent.class);
        registrar.initialize();

        ServiceComponent serviceComponent = registrar.injectionContext().get(ServiceComponent.class);
        System.out.println(serviceComponent.getServiceB());
    }

}
