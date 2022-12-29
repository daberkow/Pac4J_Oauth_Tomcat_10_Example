package com.github.daberkow.pac4j_oauth_tomcat_10_example;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serial;

@WebServlet(
        name = "RootApp",
        urlPatterns = {"/"}
)
public class WebApp extends HttpServlet {
    private static final Logger logger = LogManager.getLogger(WebApp.class);
    @Serial
    private static final long serialVersionUID = 2802147441289972890L;

    @Override
    public final void init(ServletConfig config) throws ServletException {
        super.init(config);
        logger.info("Passed In Info From Launcher: " + getInitParameter("passedParam"));
    }

    @Override
    protected final void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        logger.info(req.getRequestURI());
        // For the root of the server we will always return the index, this is not production quality code.
        final ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("index.html");

        String tempStringHolder = new String(inputStream.readAllBytes());

        // Here we know we have some contents. The index page has a variable I will replace to show the user's status
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(tempStringHolder.substring(0, tempStringHolder.indexOf("THIS_SERVER_STATUS")));
        if (SessionManager.isLoggedIn(req)) {
            stringBuilder.append("Logged In");
        } else {
            stringBuilder.append("NOT Logged In");
        }
        stringBuilder.append(tempStringHolder.substring(tempStringHolder.indexOf("THIS_SERVER_STATUS") + 18));

        // Send the data to the user and close the connection
        resp.setContentType("text/html; charset=utf-8");
        final ServletOutputStream outputStream = resp.getOutputStream();
        outputStream.println(stringBuilder.toString());
        outputStream.flush();
        outputStream.close();
    }
}
