/**
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
 * Represents a setter function that has a single parameter of type O belonging to a class of type T.
 *
 * <p>
 * Assuming class T has a method <code>void setString(String x)</code>, a reference to the setString method can be used
 * as a {@link SetterFunction}, for example, T::setString
 *
 * @author Camilo Gonzalez
 */
public interface SetterFunction<T, O> extends Serializable {

    void set(T setterOwner, O parameter);

}
