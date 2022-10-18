package com.example.allegroapiclient.auth.allegro_auth.token_generation;

import com.example.allegroapiclient.auth.allegro_auth.AllegroAuthApiService;
import com.example.allegroapiclient.auth.entities.AllegroApp;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationCodeFlowTokenGeneration {
    private final AllegroAuthApiService authApiService;
    private final Logger logger = LoggerFactory.getLogger(AuthorizationCodeFlowTokenGeneration.class);

    @Autowired
    public AuthorizationCodeFlowTokenGeneration(AllegroAuthApiService authApiService) {
        this.authApiService = authApiService;
    }

    // create URL that allows client to authorize app
    public String generateAuthURL(AllegroApp app){
        logger.info(String.format("Generating authorization URL (with code flow authorization) for app: %s", app));
        return authApiService.generateAccessCodeUrl(app.getClientId(), app.getEndpoint(), app.isSandbox());
    }

    public AllegroApp generateToken(AllegroApp app, String code){
        logger.info(String.format("Generating token for user (code flow) for app: %s", app));
        JSONObject tokenData = authApiService.generateTokenForUserWithAuthorizationCode(app.getClientId(),
                app.getClientSecret(),
                code,
                app.getEndpoint(),
                app.isSandbox());

        app.setTokenForUser(tokenData.getString("access_token"));
        app.setRefreshToken(tokenData.getString("refresh_token"));
        return app;
    }
}
