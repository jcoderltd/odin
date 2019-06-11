/*
 * Copyright 2018 - JCoder Ltd
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