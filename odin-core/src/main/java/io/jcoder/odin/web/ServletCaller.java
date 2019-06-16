/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 *
 * @author Camilo Gonzalez
 */
public interface ServletCaller {

    void callServlet(ServletRequest request, ServletResponse response) throws ServletException, IOException;

}
