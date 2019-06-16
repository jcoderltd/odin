/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin.web.aop;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author Camilo Gonzalez
 */
public interface HttpServletInterceptor {

    boolean before(HttpServletRequest request, HttpServletResponse response);

    boolean onSuccess(HttpServletRequest request, HttpServletResponse response);

    boolean onError(HttpServletRequest request, HttpServletResponse response, Exception ex);

}
