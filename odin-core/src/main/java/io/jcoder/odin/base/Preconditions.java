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
package io.jcoder.odin.base;

/**
 *
 * @author Camilo Gonzalez
 */
public final class Preconditions {
    private Preconditions() {
    }

    public static <T> void verifyNotNull(T obj, String errorMsg) {
        if (obj == null) {
            throw new NullPointerException(errorMsg);
        }
    }

    public static <T> void verifyArgumentCondition(boolean condition, String errorMsg) {
        if (!condition) {
            throw new IllegalArgumentException(errorMsg);
        }
    }

    public static <T> void verifyState(boolean stateCondition, String errorMsg) {
        if (!stateCondition) {
            throw new IllegalStateException(errorMsg);
        }
    }
}
