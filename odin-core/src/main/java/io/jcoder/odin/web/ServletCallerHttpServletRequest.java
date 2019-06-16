/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * 
 * @author Camilo Gonzalez
 */
public class ServletCallerHttpServletRequest extends HttpServletRequestWrapper {

    private final String servletPath;
    private final String pathInfo;

    public ServletCallerHttpServletRequest(HttpServletRequest request, String servletPath, String pathInfo) {
        super(request);
        this.servletPath = servletPath;
        this.pathInfo = pathInfo;
    }
    
    public HttpServletRequest getRequest() {
        return (HttpServletRequest) super.getRequest();
    }

    @Override
    public String getServletPath() {
        return servletPath;
    }

    @Override
    public String getPathInfo() {
        return pathInfo;
    }

}
