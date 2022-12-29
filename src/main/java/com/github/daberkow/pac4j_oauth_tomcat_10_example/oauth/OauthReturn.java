package com.github.daberkow.pac4j_oauth_tomcat_10_example.oauth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.jee.context.JEEContext;
import org.pac4j.jee.context.session.JEESessionStoreFactory;
import org.apache.logging.log4j.Logger;
import java.io.*;
import java.util.Optional;

/**
 * Now we have returned from the OAuth server, and need to check the token we got and pull any user info we want.
 */
public class OauthReturn extends AbstractAuth {
    private static final Logger logger = LogManager.getLogger(OauthReturn.class);
    @Serial
    private static final long serialVersionUID = -2859249637388126407L;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        WebContext context = new JEEContext(req, resp);
        SessionStore sessionStore = JEESessionStoreFactory.INSTANCE.newSessionStore(req, resp);
        Optional<Credentials> credentials;
        //Wrap this in a try
        try {
            credentials = client.getCredentials(context, sessionStore);
        } catch (TechnicalException e) {
            // 'unauthorized_client' means the clientid and secret between this server and the oauth server do not match
            // In restarting this Java server you may see "invalid grant", this is left over from the session before
            logger.error("User came with bad credentials to the redirect endpoint.", e);
            resp.sendRedirect("/oauth");
            return;
        }

        Optional<UserProfile> profile = client.getUserProfile(credentials.get(), context, sessionStore);
        if (profile.isEmpty()) {
            logger.error("User came went through auth but is missing profile data.");
            resp.sendRedirect("/oauth");
            return;
        }
        HttpSession session = req.getSession(true);
        String username = profile.get().getId();
        logger.info("Successful login for " + username + " from " + req.getLocalAddr());
        session.setAttribute("user", username);
        resp.sendRedirect("/");
    }
}
