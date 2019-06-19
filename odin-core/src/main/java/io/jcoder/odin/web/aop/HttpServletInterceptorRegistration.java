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
package io.jcoder.odin.web.aop;

import io.jcoder.odin.InjectionContext;
import io.jcoder.odin.reference.InjectableReference;

/**
 *
 * @author Camilo Gonzalez
 */
public class HttpServletInterceptorRegistration<T extends HttpServletInterceptor> {

    private final boolean prefixBased;

    private final String path;

    private final InjectableReference<T> interceptor;

    public HttpServletInterceptorRegistration(String path, InjectableReference<T> interceptor) {
        this.prefixBased = path.endsWith("*");
        this.path = this.prefixBased ? path.substring(0, path.length() - 1) : path;
        this.interceptor = interceptor;
    }

    public T interceptor(InjectionContext context) {
        return interceptor.get(context);
    }

    public boolean matches(String requestPath) {
        if (prefixBased) {
            return requestPath.startsWith(path);
        }
        return path.equals(requestPath);
    }

}
