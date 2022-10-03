package com.example.allegroapiclient.auth.allegro_auth.token_generation;

import com.example.allegroapiclient.auth.allegro_auth.AllegroAuthApiService;
import com.example.allegroapiclient.auth.entities.AllegroApp;
import com.example.allegroapiclient.auth.exceptions.InvalidClientIdException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ApplicationTokenGeneration {
    private final AllegroAuthApiService authApiService;

    @Autowired
    public ApplicationTokenGeneration(AllegroAuthApiService authApiService) {
        this.authApiService = authApiService;
    }

    // get AllegroApp, generate Token for Application and update given app.
    public AllegroApp generate(AllegroApp app) throws InvalidClientIdException {
        String token = authApiService.generateTokenForApplication(
                app.getClientId(),
                app.getClientSecret(),
                app.isSandbox());

        app.setTokenForApplication(token);
        return app;
    }
}