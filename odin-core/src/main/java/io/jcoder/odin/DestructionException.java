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
package io.jcoder.odin;

/**
 * Exception thrown when an {@link InjectionContext} fails to destroy an object.
 *
 * @author Camilo Gonzalez
 */
public class DestructionException extends RuntimeException {

    private static final long serialVersionUID = -7953593596238564165L;

    public DestructionException(String message, Throwable cause) {
        super(message, cause);
    }

    public DestructionException(String message) {
        super(message);
    }

    public DestructionException(Throwable cause) {
        super(cause);
    }

}
