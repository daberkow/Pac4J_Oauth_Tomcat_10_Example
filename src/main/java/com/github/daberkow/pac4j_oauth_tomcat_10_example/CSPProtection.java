package com.github.daberkow.pac4j_oauth_tomcat_10_example;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.filters.FilterBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import java.io.IOException;

public class CSPProtection extends FilterBase {
    private final Log log = LogFactory.getLog(CSPProtection.class); // must not be static
    private static String policy = "default-src 'self'";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        if (response instanceof HttpServletResponse httpResponse) {
            if (response.isCommitted()) {
                throw new ServletException("httpHeaderSecurityFilter.committed");
            }

            // CSP
            httpResponse.setHeader("Content-Security-Policy", policy);
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    protected Log getLogger() {
        return log;
    }

    public static String getPolicy() {
        return policy;
    }

    public static void setPolicy(String policy) {
        CSPProtection.policy = policy;
    }
}
