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

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Comparator;

/**
 * Comparator used to sort {@link InjectionFunction} based on the order of injection that should be applied.
 *
 * <p>
 * This comparator guarantees that:
 *
 * <ol>
 * <li>Functions from superclasses are applied before than functions from subclasses
 * <li>Fields from the same class are applied before methods from the same class
 * <li>{@link InjectionFunction} objects that don't specify a <code>member</code> are last to be applied in no
 * particular order amongst them
 * </ol>
 *
 * @author Camilo Gonzalez
 */
public class InjectionFunctionComparator implements Comparator<InjectionFunction<?>> {

    /**
     * Result to return when only the left side comparable {@link InjectionFunction} object specifies a member
     */
    private static int ONLY_LEFT_FUNCTION_HAS_MEMBER = -1;

    /**
     * Result to return when only the right side comparable {@link InjectionFunction} object specifies a member
     */
    private static int ONLY_RIGHT_FUNCTION_HAS_MEMBER = 1;

    /**
     * Result to return when both {@link InjectionFunction} objects don't specify a member they inject
     */
    private static int BOTH_FUNCTIONS_NOT_MEMBERS = 0;

    @Override
    public int compare(InjectionFunction<?> a, InjectionFunction<?> b) {
        return a.member()
                .map(fa -> b.member()
                        .map(fb -> compareClassMembers(a.member().get(), fb))
                        .orElse(ONLY_LEFT_FUNCTION_HAS_MEMBER))
                .orElse(b.member()
                        .map(fb -> ONLY_RIGHT_FUNCTION_HAS_MEMBER)
                        .orElse(BOTH_FUNCTIONS_NOT_MEMBERS));
    }

    private int compareClassMembers(Member a, Member b) {
        Class<?> aClass = a.getDeclaringClass();
        Class<?> bClass = b.getDeclaringClass();
        if (aClass.equals(bClass)) {
            if (a instanceof Field && b instanceof Method) {
                return -1;
            }
            if (a instanceof Method && b instanceof Field) {
                return 1;
            }
        } else {
            if (aClass.isAssignableFrom(bClass)) {
                return -1;
            } else if (bClass.isAssignableFrom(aClass)) {
                return 1;
            }
        }
        return 0;
    }
}
