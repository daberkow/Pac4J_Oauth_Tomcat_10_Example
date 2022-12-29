package com.github.daberkow.pac4j_oauth_tomcat_10_example;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.Serial;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;


@WebServlet(
        name = "RootApp",
        urlPatterns = {"/"}
)
public class WebApp extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(WebApp.class);
    @Serial
    private static final long serialVersionUID = 2802147441289972890L;

    @Override
    public final void init(ServletConfig config) throws ServletException {
        super.init(config);
        logger.info("Passed In Info From Launcher: " + getInitParameter("passedParam"));
    }

    @Override
    protected final void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        URL url = this.getClass().getClassLoader().getResource("index.html");

        File file = null;
        String tempStringHolder;
        try {
            file = new File(url.toURI());
            tempStringHolder = Files.readString(file.toPath());
        } catch (URISyntaxException | IOException e) {
            resp.sendError(500);
            throw new RuntimeException(e);
        }

        if (tempStringHolder == null) {
            // We did not hit the catch, but we did not get the contents that were expected for our embedded app.
            resp.sendError(404);
        }

        // Here we know we have some contents.
        resp.setContentType("text/html; charset=utf-8");
        final ServletOutputStream outputStream = resp.getOutputStream();
        outputStream.println(tempStringHolder);
        outputStream.flush();
        outputStream.close();
    }
}
