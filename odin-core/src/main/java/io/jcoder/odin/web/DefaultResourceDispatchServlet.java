/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin.web;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author Camilo Gonzalez
 */
public class DefaultResourceDispatchServlet extends HttpServlet {
    
    private static final long serialVersionUID = -1176798423448087430L;
    
    private static final String DEFAULT_SERVLET_NAME = "default";

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher dispatcher = req.getServletContext().getNamedDispatcher(DEFAULT_SERVLET_NAME);
        dispatcher.forward(req, resp);
    }
}
