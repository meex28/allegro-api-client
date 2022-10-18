package com.example.allegroapiclient.auth.allegro_auth.token_generation;

import com.example.allegroapiclient.auth.allegro_auth.AllegroAuthApiService;
import com.example.allegroapiclient.auth.entities.AllegroApp;
import com.example.allegroapiclient.auth.exceptions.InvalidClientIdException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ApplicationTokenGeneration {
    private final AllegroAuthApiService authApiService;
    private final Logger logger = LoggerFactory.getLogger(ApplicationTokenGeneration.class);

    @Autowired
    public ApplicationTokenGeneration(AllegroAuthApiService authApiService) {
        this.authApiService = authApiService;
    }

    // get AllegroApp, generate Token for Application and update given app.
    public AllegroApp generate(AllegroApp app) {
        logger.info(String.format("Generating application token for app: %s", app));
        String token = authApiService.generateTokenForApplication(
                app.getClientId(),
                app.getClientSecret(),
                app.isSandbox());

        app.setTokenForApplication(token);
        return app;
    }
}