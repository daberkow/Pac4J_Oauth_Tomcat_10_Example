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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;
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
            logger.error("User came with bad credentials to the redirect endpoint.");
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
    }
//    private String getProfileGroups(String accessToken) {
//        logger.debug("Pulling user groups");
//        StringBuilder stringBuilder = new StringBuilder();
//        //https://rtfm.palantir.build/docs/multipass/develop/api.html
//        String infoOnMe = getFromMultipassApi("api/me/groups", accessToken);
//        JsonObject profileData;
//        try {
//            profileData = JsonParser.parseString(infoOnMe).getAsJsonObject();
//            for (JsonElement singleGroup : profileData.get("groups").getAsJsonArray()) {
//                JsonObject jsonObject = singleGroup.getAsJsonObject();
//                stringBuilder.append(jsonObject.get("name").getAsString()).append(",");
//            }
//        } catch (Throwable e) {
//            return "";
//        }
//        return stringBuilder.toString();
//    }
//    private String getFromMultipassApi(String endpoint, String accessToken) {
//        StringBuilder buffer = new StringBuilder();
//        try {
//            URL executingUrl = new URL(Manager.getLocalSettings().getMultipassUrl() + endpoint);
//            HttpURLConnection connection = (HttpURLConnection) executingUrl.openConnection();
//            connection.setRequestMethod("GET");
//            connection.setDoOutput(true);
//            connection.setRequestProperty("user-agent", "opennetboot-" + Core.ONB_VERSION);
//            connection.setRequestProperty("Authorization", "Bearer " + accessToken);
//            connection.setRequestProperty("Content-Type", "application/json");
//            connection.setRequestProperty("charset", "utf-8");
//            connection.setUseCaches(false);
//            InputStream content = connection.getInputStream();
//            BufferedReader in   = new BufferedReader(new InputStreamReader(content));
//            String line;
//            while ((line = in.readLine()) != null) {
//                buffer.append(line);
//            }
//        } catch (Exception e) {
//            return "";
//        }
//        return buffer.toString();
//    }
}
