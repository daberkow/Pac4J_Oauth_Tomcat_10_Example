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
public class OauthInit extends AbstractAuth {
    private static final Logger logger = LogManager.getLogger(OauthInit.class);
    @Serial
    private static final long serialVersionUID = -2859249637388126407L;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        WebContext context = new JEEContext(req, resp);
        SessionStore sessionStore = JEESessionStoreFactory.INSTANCE.newSessionStore(req, resp);
//        client.getRedirectionAction(context, sessionStore);
        Optional<RedirectionAction> redirect = client.getRedirectionAction(context, sessionStore);
        // https://github.com/tumbashlah/apereo/blob/77a15413d384e4c59a200d8bfc2b30d6a1de75d4/support/cas-server-support-pac4j-webflow/src/main/java/org/apereo/cas/web/BaseDelegatedAuthenticationController.java#L88
        if (redirect.isPresent()) {
            var action = redirect.get();
            logger.debug("Determined final redirect action for client [{}] as [{}]", client, action);
            if (action instanceof WithLocationAction foundAction) {
                String temp = foundAction.getLocation();
//                URIBuilder builder = null;
//                try {
//                    builder = new URIBuilder(foundAction.getLocation());
//                } catch (URISyntaxException e) {
//                    e.printStackTrace();
//                    return;
//                }
//                var url = builder.toString();
                logger.info("Redirecting client [{}] to [{}] based on identifier [{}]", client.getName(), temp, 1);
                resp.sendRedirect(temp);
            }
            if (action instanceof WithContentAction) {
//                var seeOtherAction = (WithContentAction) action;
                resp.sendError(500);
            }
        }
    }
}

