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
package io.jcoder.odin.function;

import java.io.Serializable;

/**
 * Represents a PreDestroy function.
 *
 * <p>
 * This method is called on the instance after its being detached from the InjectionContext.
 *
 * @author Camilo Gonzalez
 */
@FunctionalInterface
public interface PreDestroyFunction<T> extends Serializable {

    void preDestroy(T instance);

}
