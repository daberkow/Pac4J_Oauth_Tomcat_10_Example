package com.github.daberkow.pac4j_oauth_tomcat_10_example;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.startup.Tomcat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import java.io.File;

public class TomcatLauncher implements Runnable {
    private static final Logger logger = LogManager.getLogger(TomcatLauncher.class);

    @Override
    public void run() {
        // https://devcenter.heroku.com/articles/create-a-java-web-application-using-embedded-tomcat
        final Tomcat tomcat = new Tomcat();
        File tomcatHome = new File( "tmp" + File.separator + "webapps");
        logger.info(tomcatHome.getAbsolutePath());
        if (!tomcatHome.exists() && !tomcatHome.mkdirs()) {
            logger.error("Error making working directory for Tomcat");
        }
        tomcat.setBaseDir("tmp");
        tomcat.setPort(8080);

        // This is optional, and in place to pass Nessus Security Scans
        // https://stackoverflow.com/questions/52814582/tomcat-is-not-redirecting-to-400-bad-request-custom-error-page/55702749#55702749
        var host = (StandardHost) tomcat.getHost();
        var errorReportValve = new org.apache.catalina.valves.ErrorReportValve();
        errorReportValve.setShowReport(false);
        errorReportValve.setShowServerInfo(false);
        host.addValve(errorReportValve);

        // Set some custom context information
        final Context ctx = tomcat.addContext("/", "");

        // More changes pass nessus scans
        // https://github.com/jiaguangzhao/base/blob/905aaf4111f4779e236043ff423951672ade848a/src/main/java/com/example/base/aop/configure/TomcatConfigure.java
        FilterDef httpHeaderSecurityFilter = new FilterDef();
        httpHeaderSecurityFilter.setFilterName("httpHeaderSecurity");
        httpHeaderSecurityFilter.setFilterClass("org.apache.catalina.filters.HttpHeaderSecurityFilter");
        httpHeaderSecurityFilter.addInitParameter("antiClickJackingEnabled", String.valueOf(Boolean.TRUE));
        httpHeaderSecurityFilter.addInitParameter("antiClickJackingOption", "DENY");
        httpHeaderSecurityFilter.addInitParameter("xssProtectionEnabled", String.valueOf(Boolean.TRUE));
        httpHeaderSecurityFilter.addInitParameter("blockContentTypeSniffingEnabled", String.valueOf(Boolean.TRUE));
        httpHeaderSecurityFilter.setAsyncSupported(String.valueOf(Boolean.TRUE));
        FilterMap httpHeaderSecurityFilterMap = new FilterMap();
        httpHeaderSecurityFilterMap.setFilterName("httpHeaderSecurity");
        httpHeaderSecurityFilterMap.addURLPattern("/*");
        httpHeaderSecurityFilterMap.setDispatcher("REQUEST");
        ctx.addFilterDef(httpHeaderSecurityFilter);
        ctx.addFilterMap(httpHeaderSecurityFilterMap);

        // This is not actually used, but wanted an example of injecting a CSP policy for servlet application
        // Tomcat 10
        FilterDef cspFilter = new FilterDef();
        cspFilter.setFilterName("cspHeaderSecurity");
        cspFilter.setFilterClass("com.github.daberkow.pac4j_oauth_tomcat_10_example.CSPProtection");
        cspFilter.addInitParameter("policy", "" +
                        "frame-src 'self' http://127.0.0.1:8080 http://127.0.0.1:8081; " +
                        "frame-ancestors 'self' http://127.0.0.1:8080 http://127.0.0.1:8081; object-src 'none';");
        cspFilter.setAsyncSupported(String.valueOf(Boolean.TRUE));
        FilterMap cspHeaderSecurityFilterMap = new FilterMap();
        cspHeaderSecurityFilterMap.setFilterName("cspHeaderSecurity");
        cspHeaderSecurityFilterMap.addURLPattern("/*");
        cspHeaderSecurityFilterMap.setDispatcher("REQUEST");
        ctx.addFilterDef(cspFilter);
        ctx.addFilterMap(cspHeaderSecurityFilterMap);

        // Setup our web servlets
        try {
            final Wrapper webApp = Tomcat.addServlet(ctx,
                    "WebApp", "com.github.daberkow.pac4j_oauth_tomcat_10_example.WebApp");
            // This is not required but an example of how to pass in data from your launch context to the servlet
            webApp.addInitParameter("passedParam", "Example Data!");

            // Init the Oauth Data
            Tomcat.addServlet(ctx,
                    "Oauth", "com.github.daberkow.pac4j_oauth_tomcat_10_example.oauth.OauthInit");
            Tomcat.addServlet(ctx,
                    "OauthCallback", "com.github.daberkow.pac4j_oauth_tomcat_10_example.oauth.OauthReturn");

            // This is a nice way later for AJAX to get the status of logged-in user
            Tomcat.addServlet(ctx,
                    "SessionManager", "com.github.daberkow.pac4j_oauth_tomcat_10_example.SessionManager");

            ctx.addServletMappingDecoded("/oauth", "Oauth");
            ctx.addServletMappingDecoded("/oauth/redirect", "OauthCallback");
            ctx.addServletMappingDecoded("/session/*", "SessionManager");
            ctx.addServletMappingDecoded("/", "WebApp");

            final Connector setupConnector = tomcat.getConnector();
            setupConnector.setScheme("http");

            // I included some less used options to be helpful. I usually put a nginx or Apache HTTPD proxy in front of
            // these services, so I do not init SSL here.
            // setupConnector.setProperty("compression", "0"); // This if you are doing odd servlet stuff
            // setupConnector.setSecure(true);
            // setupConnector.setAttribute("keyAlias", "tomcat");
            // setupConnector.setAttribute("keystorePass", "palantir");
            // setupConnector.setAttribute("keystoreFile", "../ssl/localkeystore.jks");
            // setupConnector.setAttribute("clientAuth", "false");
            // setupConnector.setAttribute("sslProtocol", "TLS");
            // setupConnector.setAttribute("SSLEnabled", true);

            // Start it up
            tomcat.setConnector(setupConnector);
            tomcat.start();
            logger.trace("Tomcat Started");
            tomcat.getServer().await();
        } catch (final LifecycleException e1) {
            logger.error("Problem loading Apache Modules", e1);
        }
    }
}



