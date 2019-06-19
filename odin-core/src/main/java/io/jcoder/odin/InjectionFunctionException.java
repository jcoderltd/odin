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

import io.jcoder.odin.function.InjectionFunction;

/**
 *
 * @author Camilo Gonzalez
 */
public class InjectionFunctionException extends RuntimeException {

    private static final long serialVersionUID = 2835737866796725340L;

    private final InjectionFunction<?> failedFunction;

    public InjectionFunctionException(InjectionFunction<?> failedFunction, String message, Throwable cause) {
        super("Exception of InjectionFunction: " + failedFunction + " - " + message, cause);
        this.failedFunction = failedFunction;
    }

    public InjectionFunctionException(InjectionFunction<?> failedFunction, String message) {
        this(failedFunction, message, null);
    }

    public InjectionFunction<?> getFailedFunction() {
        return failedFunction;
    }

}
