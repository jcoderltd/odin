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
package io.jcoder.odin;

/**
 * Exception thrown when an underlying constructor or factory method fails to create the requested object.
 *
 * @author Camilo Gonzalez
 */
public class ObjectCreationException extends RuntimeException {

    private static final long serialVersionUID = 6014896594660845737L;

    public ObjectCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ObjectCreationException(String message) {
        super(message);
    }

    public ObjectCreationException(Throwable cause) {
        this("Couldn't create object due to exception at construction time", cause);
    }

}
