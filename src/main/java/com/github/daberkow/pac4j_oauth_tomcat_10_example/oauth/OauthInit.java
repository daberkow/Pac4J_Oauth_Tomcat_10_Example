package com.github.daberkow.pac4j_oauth_tomcat_10_example.oauth;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.exception.http.RedirectionAction;
import org.pac4j.core.exception.http.WithContentAction;
import org.pac4j.core.exception.http.WithLocationAction;
import org.pac4j.jee.context.JEEContext;
import org.pac4j.jee.context.session.JEESessionStoreFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.io.Serial;
import java.util.Optional;

/**
 * Our entry into our OAuth flow, this gets your session between the server and your browser, makes sure you have a
 * valid cookie, then redirects you to the auth server to login.
 */
public class OauthInit extends AbstractAuth {
    private static final Logger logger = LogManager.getLogger(OauthInit.class);
    @Serial
    private static final long serialVersionUID = -2859249637388126407L;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        WebContext context = new JEEContext(req, resp);
        SessionStore sessionStore = JEESessionStoreFactory.INSTANCE.newSessionStore(req, resp);
        Optional<RedirectionAction> redirect = client.getRedirectionAction(context, sessionStore);
        // https://github.com/tumbashlah/apereo/blob/77a15413d384e4c59a200d8bfc2b30d6a1de75d4/support/cas-server-support-pac4j-webflow/src/main/java/org/apereo/cas/web/BaseDelegatedAuthenticationController.java#L88
        if (redirect.isPresent()) {
            var action = redirect.get();
            logger.debug("Determined final redirect action for client [{}] as [{}]", client, action);
            if (action instanceof WithLocationAction foundAction) {
                String tempLoc = foundAction.getLocation();
                logger.info("Redirecting client [{}] to [{}] based on identifier [{}]", client.getName(), tempLoc, 1);
                resp.sendRedirect(tempLoc);
            }
            if (action instanceof WithContentAction) {
                resp.sendError(500);
            }
        }
    }
}

