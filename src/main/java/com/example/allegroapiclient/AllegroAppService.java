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
