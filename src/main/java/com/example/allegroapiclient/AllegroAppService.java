package com.example.allegroapiclient;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AllegroAppService {
    private final AllegroAppRepository repository;
    private AllegroAuthApiService authApiService;
    private final Logger logger = LoggerFactory.getLogger(AllegroAppService.class);

    @Autowired
    public AllegroAppService(AllegroAppRepository repository, AllegroAuthApiService authApiService) {
        this.repository = repository;
        this.authApiService = authApiService;
    }

    public void generateTokenForApplication(String clientId) throws IllegalArgumentException{
        Optional<AllegroApp> allegroAppOptional = repository.findById(clientId);
        if(allegroAppOptional.isEmpty())
            throw new IllegalArgumentException("Invalid client id");

        AllegroApp app = allegroAppOptional.get();

        String token = authApiService.generateTokenForApplication(
                app.getClientId(),
                app.getClientSecret(),
                app.isSandbox());

        app.setTokenForApplication(token);
        repository.save(app);
    }

    public String getAccessCodeUrl(String clientId) throws IllegalArgumentException{
        Optional<AllegroApp> allegroAppOptional = repository.findById(clientId);
        if(allegroAppOptional.isEmpty())
            throw new IllegalArgumentException("Invalid client id");

        AllegroApp app = allegroAppOptional.get();
        return authApiService.generateAccessCodeUrl(app.getClientId(), app.getEndpoint(), app.isSandbox());
    }

    public void addTokenForUserToApp(String code, String endpoint){
        Optional<AllegroApp> allegroAppOptional = repository.findByEndpoint(endpoint);

        if(allegroAppOptional.isEmpty()){
            logger.error(String.format("Invalid endpoint=%s to generate token.", endpoint));
            return;
        }

        AllegroApp allegroApp = allegroAppOptional.get();

        JSONObject tokenData = authApiService.generateTokenForUser(allegroApp.getClientId(),
                allegroApp.getClientSecret(),
                code,
                allegroApp.getEndpoint(),
                allegroApp.isSandbox());

        allegroApp.setTokenForUser(tokenData.getString("access_token"));
        allegroApp.setRefreshToken(tokenData.getString("refresh_token"));
        repository.save(allegroApp);
    }
}
