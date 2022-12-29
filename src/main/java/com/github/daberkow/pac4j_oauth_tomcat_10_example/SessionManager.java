package com.github.daberkow.pac4j_oauth_tomcat_10_example;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * A servlet to allow for checking of the users status by AJAX.
 */
@WebServlet(
        name = "SessionManager",
        urlPatterns = {"/session/*"}
)
public class SessionManager extends HttpServlet {
    private static final Logger logger = LogManager.getLogger(SessionManager.class);
    @Override
    protected final void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        logger.info(req.getRequestURI());

        String returningText = "";
        switch (req.getRequestURI().toLowerCase()) {
            case "/session/status" -> {
                if (isLoggedIn(req)) {
                    returningText = "Logged In";
                } else {
                    returningText = "Not Logged In";
                }
            }
            case "/session/logout" -> {
                req.getSession().invalidate();
                req.logout();
                resp.sendRedirect("/");
            }
            default -> returningText = "No Return";
        }
        final ServletOutputStream outputStream = resp.getOutputStream();
        resp.setContentType("text/html; charset=utf-8");
        outputStream.write(returningText.getBytes());
        outputStream.flush();
        outputStream.close();
    }

    protected static boolean isLoggedIn(HttpServletRequest req) {
        // By saying getSession(false), Tomcat won't make a new session if the user is not currently logged in.
        HttpSession session = req.getSession(false);
        return (session != null && session.getAttribute("user") != null
                && session.getAttribute("user") != "");
    }

}
