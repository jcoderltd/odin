/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin.web;

import static io.jcoder.odin.builder.ReferenceBuilder.ofType;
import static io.jcoder.odin.builder.RegistrationBuilder.singleton;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import io.jcoder.odin.InjectionContext;
import io.jcoder.odin.builder.ReferenceBuilder;
import io.jcoder.odin.reference.InjectableReference;
import io.jcoder.odin.web.aop.HttpServletInterceptor;
import io.jcoder.odin.web.aop.HttpServletInterceptorRegistration;

/**
 *
 * @author Camilo Gonzalez
 */
public abstract class InjectionContextWebServlet extends HttpServlet {

    private static final long serialVersionUID = 5451373285705790146L;

    private final static Logger logger = LoggerFactory.getLogger(InjectionContextWebServlet.class);

    private InjectionContext context;

    private RequestScope requestScope;

    private final Map<String, InterceptableServletCaller> exactCallers = new ConcurrentHashMap<>();

    private final Map<String, InterceptableServletCaller> extensionBasedCallers = new ConcurrentHashMap<>();

    private final Map<String, InterceptableServletCaller> prefixBasedCallers = new ConcurrentHashMap<>();

    private final List<HttpServletInterceptorRegistration<?>> interceptors = new CopyOnWriteArrayList<>();

    private final Set<Servlet> initializedServlets = Sets.newIdentityHashSet();

    private ServletConfig config;

    @Override
    public final void init(ServletConfig config) throws ServletException {
        try {
            this.context = this.buildContext();
            this.config = config;

            if (!this.context.hasScope(RequestScope.class)) {
                this.requestScope = new RequestScope();
                this.context.registerScope(requestScope);
            } else {
                this.requestScope = (RequestScope) this.context.getScope(RequestScope.class);
            }

            this.context.register(singleton(DefaultResourceDispatchServlet.class));

            Preconditions.checkNotNull(this.context, "The InjectionContext is null");
            Preconditions.checkArgument(this.context.initialized(), "The InjectionContext needs to be initialized");

            this.registerServlets(this.context);
        } catch (final Exception ex) {
            logger.error("Exception initializing the InjectionContextWebFilter", ex);
        }
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        HttpServletRequest request;
        boolean setRequestScope;
        if (req instanceof ServletCallerHttpServletRequest) {
            request = ((ServletCallerHttpServletRequest) req).getRequest();
            setRequestScope = false;
        } else {
            request = (HttpServletRequest) req;
            setRequestScope = true;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Request for: {}", request.getRequestURL().toString());
        }
        String pathInfo = request.getPathInfo() == null ? "" : request.getPathInfo();
        String requestedPath = request.getServletPath() + pathInfo;
        List<HttpServletInterceptorRegistration<?>> interceptors = matchingInterceptors(requestedPath);
        PathServletCaller caller = matchingCallerFor(requestedPath);
        if (caller != null) {
            ServletCallerHttpServletRequest wrappedRequest = new ServletCallerHttpServletRequest(request, caller.servletPath,
                    caller.pathInfo);
            try {
                if (setRequestScope) {
                    requestScope.setRequest(wrappedRequest);
                }
                caller.callServlet(interceptors, wrappedRequest, resp);
            } finally {
                if (setRequestScope) {
                    requestScope.unsetRequest(wrappedRequest);
                }
            }
        } else {
            logger.trace("No registered servlet found for servlet path {}", requestedPath);
        }
    }

    private List<HttpServletInterceptorRegistration<?>> matchingInterceptors(String requestedPath) {
        return interceptors.stream()
                .filter(interceptor -> interceptor.matches(requestedPath))
                .collect(Collectors.toList());
    }

    protected final <T extends Servlet> void registerServlet(String path, InjectableReference<T> servletReference) throws ServletException {
        initializeServlet(servletReference.get(context));

        final ServletCaller servletCaller = (req, resp) -> {
            final Servlet servlet = servletReference.get(context);
            if (servlet == null) {
                logger.error("No Servlet found with class {}", servletReference.getInjectableType());
            } else {
                logger.trace("Invoking servlet {} for path {}", servlet.getClass().getName(), path);
                InjectionWebFilterAttributes.setMatchingPath(req, path);
                servlet.service(req, resp);
            }

        };
        registerServletCaller(path, servletCaller);
    }

    protected final <T extends Servlet> void registerServlet(String path, ReferenceBuilder<T> servletReference) throws ServletException {
        registerServlet(path, servletReference.build());
    }

    protected final <T extends Servlet> void registerServlet(String path, Class<T> servletType) throws ServletException {
        registerServlet(path, ofType(servletType));
    }

    protected final <T extends HttpServletInterceptor> void registerInterceptor(String path, InjectableReference<T> interceptorReference)
            throws ServletException {
        interceptors.add(new HttpServletInterceptorRegistration<T>(path, interceptorReference));
    }

    protected final <T extends HttpServletInterceptor> void registerInterceptor(String path, ReferenceBuilder<T> interceptorReference)
            throws ServletException {
        registerInterceptor(path, interceptorReference.build());
    }

    protected final <T extends HttpServletInterceptor> void registerInterceptor(String path, Class<T> interceptorType)
            throws ServletException {
        registerInterceptor(path, ofType(interceptorType));
    }

    protected final <T extends Servlet> void registerResource(String path) throws ServletException {
        registerServlet(path, ofType(DefaultResourceDispatchServlet.class));
    }

    protected void initializeServlet(Servlet servlet) throws ServletException {
        if (!initializedServlets.contains(servlet)) {
            servlet.init(config);
            initializedServlets.add(servlet);
        }
    }

    private void registerServletCaller(String path, ServletCaller servletCaller) {
        InterceptableServletCaller interceptableCaller = new InterceptableServletCaller(servletCaller);
        if (path.startsWith("*")) {
            extensionBasedCallers.put(path.substring(1), interceptableCaller);
        } else {
            if (path.endsWith("/*")) {
                path = path.substring(0, path.length() - 2);
            }
            prefixBasedCallers.put(path, interceptableCaller);
            exactCallers.put(path, interceptableCaller);
        }
    }

    protected abstract InjectionContext buildContext();

    protected abstract void registerServlets(InjectionContext context) throws ServletException;

    private PathServletCaller matchingCallerFor(String requestedPath) {
        InterceptableServletCaller servletCaller = exactCallers.get(requestedPath);

        if (servletCaller != null) {
            return new PathServletCaller(servletCaller, requestedPath, "");
        }

        String pathPart = requestedPath;
        do {
            servletCaller = prefixBasedCallers.get(pathPart);
            if (servletCaller != null) {
                return new PathServletCaller(servletCaller, pathPart, requestedPath.substring(pathPart.length()));
            }
            if (pathPart.isEmpty()) {
                break;
            }
            final int lastSlashIdx = pathPart.lastIndexOf('/');
            pathPart = pathPart.substring(0, Math.max(0, lastSlashIdx));
        } while (servletCaller == null);

        return extensionBasedCallers.entrySet().stream()
                .filter(callerEntry -> {
                    return requestedPath.endsWith(callerEntry.getKey());
                })
                .findFirst()
                .map(entry -> new PathServletCaller(entry.getValue(), requestedPath, ""))
                .orElse(null);
    }

    @Override
    public void destroy() {
        logger.debug("Filter is being destroyed - removing reference to InjectionContext");
        if (this.context != null) {
            try {
                this.context.destroy();
            } catch (Exception ex) {
                logger.warn("Exception destroying InjectionContext", ex);
            }
            this.context = null;
        }
    }

    private final class PathServletCaller {
        private final InterceptableServletCaller servletCaller;

        private final String servletPath;

        private final String pathInfo;

        public PathServletCaller(InterceptableServletCaller servletCaller, String servletPath, String pathInfo) {
            this.servletCaller = servletCaller;
            this.servletPath = servletPath;
            this.pathInfo = pathInfo;
        }

        public void callServlet(List<HttpServletInterceptorRegistration<?>> interceptors, ServletCallerHttpServletRequest wrappedRequest,
                HttpServletResponse resp) throws IOException, ServletException {

            // get the interceptor objects from the InjectionContext
            List<HttpServletInterceptor> resolvedInterceptors = interceptors.stream()
                    .map(intReg -> intReg.interceptor(context))
                    .collect(Collectors.toList());

            servletCaller.callServlet(resolvedInterceptors, wrappedRequest, resp);
        }

    }

}
