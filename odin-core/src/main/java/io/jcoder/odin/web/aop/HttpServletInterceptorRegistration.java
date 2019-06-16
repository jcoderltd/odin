/*
 * Copyright 2018 - JCoder Ltd
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
