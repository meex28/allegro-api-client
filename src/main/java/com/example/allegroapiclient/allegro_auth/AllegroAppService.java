package com.example.allegroapiclient.allegro_auth;

import com.example.allegroapiclient.entities.AllegroApp;
import com.example.allegroapiclient.exceptions.InvalidClientIdException;
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

    public void addNewApp(String clientId, String clientSecret, boolean isSandbox, String username, String endpoint) throws InvalidClientIdException {
        // check if clientId is unique
        if(repository.findById(clientId).isPresent())
            throw new InvalidClientIdException("There is already app with given ClientID");

        AllegroApp app = new AllegroApp(clientId, clientSecret, isSandbox, username, endpoint);
        app.setNew(true);
        repository.save(app);
    }

    public void generateTokenForApplication(String clientId) throws InvalidClientIdException{
        Optional<AllegroApp> allegroAppOptional = repository.findById(clientId);
        if(allegroAppOptional.isEmpty())
            throw new InvalidClientIdException("Invalid client id");

        AllegroApp app = allegroAppOptional.get();

        String token = authApiService.generateTokenForApplication(
                app.getClientId(),
                app.getClientSecret(),
                app.isSandbox());

        app.setTokenForApplication(token);
        repository.save(app);
    }

    public String getAccessCodeUrl(String clientId) throws InvalidClientIdException{
        Optional<AllegroApp> allegroAppOptional = repository.findById(clientId);
        if(allegroAppOptional.isEmpty())
            throw new InvalidClientIdException("Invalid client id");

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

    public String generateEndpoint(String username){
        int number = repository.countByUsername(username)+1;
        return String.format("%s-%d", username, number);
    }
}
