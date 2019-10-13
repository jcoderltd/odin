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
package io.jcoder.odin.web;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.jcoder.odin.web.aop.HttpServletInterceptor;

public class InterceptableServletCaller {

    private final ServletCaller servletCaller;

    public InterceptableServletCaller(ServletCaller servletCaller) {
        this.servletCaller = servletCaller;
    }

    public void callServlet(List<HttpServletInterceptor> interceptors, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        invokeChain(0, interceptors, request, response);
    }

    private boolean invokeChain(int index, List<HttpServletInterceptor> interceptors, HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        if (index == interceptors.size()) {
            servletCaller.callServlet(request, response);
            return true;
        }

        HttpServletInterceptor interceptor = interceptors.get(index);
        boolean proceed = interceptor.before(request, response);
        if (!proceed) {
            return false;
        }

        try {
            boolean chainProceed = invokeChain(index + 1, interceptors, request, response);
            if (!chainProceed) {
                return false;
            }
            return interceptor.onSuccess(request, response);
        } catch (Exception ex) {
            if (interceptor.onError(request, response, ex)) {
                throw ex;
            } else {
                return false;
            }
        }
    }

}