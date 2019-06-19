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
package io.jcoder.odin.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 *
 * @author Camilo Gonzalez
 */
public class MemberFinder {
    public static Field getField(Class<?> classToProcess, String fieldName) throws NoSuchFieldException {
        if (classToProcess == null) {
            throw new NoSuchFieldException("No field " + fieldName + " found");
        }

        try {
            return classToProcess.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            return getField(classToProcess.getSuperclass(), fieldName);
        }
    }

    public static Method getMethod(Class<?> classToProcess, String methodName, Class<?>... parameters) throws NoSuchMethodException {
        if (classToProcess == null) {
            throw new NoSuchMethodException("No method " + methodName + " found");
        }

        try {
            return classToProcess.getDeclaredMethod(methodName, parameters);
        } catch (NoSuchMethodException e) {
            return getMethod(classToProcess.getSuperclass(), methodName, parameters);
        }
    }
}
