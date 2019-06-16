/*
 * Copyright 2018 - JCoder Ltd
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
