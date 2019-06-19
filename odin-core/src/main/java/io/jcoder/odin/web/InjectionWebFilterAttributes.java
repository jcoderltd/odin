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
package io.jcoder.odin.web;

import javax.servlet.ServletRequest;

/**
 *
 * @author Camilo Gonzalez
 */
public final class InjectionWebFilterAttributes {
    public static final String MATCHING_PATH = "odin.web.filter.matchingPath";

    private InjectionWebFilterAttributes() {
    }

    public static void setMatchingPath(final ServletRequest request, final String path) {
        request.setAttribute(MATCHING_PATH, path);
    }

    public static String getMatchingPath(final ServletRequest request) {
        return (String) request.getAttribute(MATCHING_PATH);
    }
}
