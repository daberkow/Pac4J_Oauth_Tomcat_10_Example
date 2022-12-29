package com.github.daberkow.pac4j_oauth_tomcat_10_example.oauth;

import com.github.daberkow.pac4j_oauth_tomcat_10_example.App;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import org.pac4j.core.util.generator.RandomValueGenerator;
import org.pac4j.oauth.client.CasOAuthWrapperClient;
import org.pac4j.oauth.client.GenericOAuth20Client;
import org.pac4j.oauth.client.OAuth20Client;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.profile.generic.GenericOAuth20ProfileDefinition;
import org.pac4j.scribe.builder.api.GenericApi20;
import java.io.Serial;
public class AbstractAuth extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 3611287488843780687L;
    protected static GenericOAuth20Client client;
    static RandomValueGenerator stateGenerator = new RandomValueGenerator(30);

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        OAuth20Configuration oAuthConfig = new OAuth20Configuration();
        oAuthConfig.setApi(GenericApi20.class);
        oAuthConfig.setProfileDefinition(new GenericOAuth20ProfileDefinition());

        oAuthConfig.setScope("profile");
        oAuthConfig.setKey("example-client");
        oAuthConfig.setSecret("example_token");
        oAuthConfig.setWithState(true);
        oAuthConfig.setStateGenerator(stateGenerator);

        client = new GenericOAuth20Client();
        client.setAuthUrl(App.oauthServer + "auth/realms/example/protocol/openid-connect/auth");
        client.setTokenUrl(App.oauthServer + "auth/realms/example/protocol/openid-connect/token");
        client.setName("example-client");
        client.setProfileUrl(App.oauthServer + "auth/realms/example/protocol/openid-connect/userinfo");
        client.setConfiguration(oAuthConfig);
        client.setProfileId("preferred_username");
        // This is the local server address, this matches your approved redirect URLs in your oauth server.
        client.setCallbackUrl("http://127.0.0.1:8080/oauth/redirect");
    }
}