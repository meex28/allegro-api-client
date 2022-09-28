package com.example.allegroapiclient.allegro_auth.token_generation;

import com.example.allegroapiclient.allegro_auth.AllegroAuthApiService;
import com.example.allegroapiclient.entities.AllegroApp;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationCodeFlowTokenGeneration {
    private final AllegroAuthApiService authApiService;

    @Autowired
    public AuthorizationCodeFlowTokenGeneration(AllegroAuthApiService authApiService) {
        this.authApiService = authApiService;
    }

    // create URL that allows client to authorize app
    public String generateAuthURL(AllegroApp app){
        return authApiService.generateAccessCodeUrl(app.getClientId(), app.getEndpoint(), app.isSandbox());
    }

    public AllegroApp generateToken(AllegroApp app, String code){
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
