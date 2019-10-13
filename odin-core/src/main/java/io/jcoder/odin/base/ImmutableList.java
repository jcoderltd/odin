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
package io.jcoder.odin.base;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Camilo Gonzalez
 */
public final class ImmutableList {

    private ImmutableList() {
    }

    public static <T> List<T> of() {
        return Collections.emptyList();
    }

    public static <T> List<T> sortedCopyOf(Comparator<? super T> comparator, Collection<? extends T> sourceList) {
        return Collections.unmodifiableList(sourceList.stream().sorted(comparator).collect(Collectors.toList()));
    }

}
