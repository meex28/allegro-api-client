package com.example.allegroapiclient.allegro_auth;

import com.example.allegroapiclient.entities.AllegroApp;
import com.example.allegroapiclient.entities.FlowTypes;
import com.example.allegroapiclient.exceptions.InvalidClientIdException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

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

    public void addNewApp(String clientId,
                          String clientSecret,
                          boolean isSandbox,
                          String username,
                          String endpoint,
                          FlowTypes authFlowType) throws InvalidClientIdException {
        // check if clientId is unique
        if(repository.findById(clientId).isPresent())
            throw new InvalidClientIdException("There is already app with given ClientID");

        AllegroApp app = new AllegroApp(clientId, clientSecret, isSandbox, username, endpoint,
                authFlowType);
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

    public String getAuthURL(String clientId) throws InvalidClientIdException{
        Optional<AllegroApp> appOptional = repository.findById(clientId);
        if(appOptional.isEmpty())
            throw new InvalidClientIdException("Invalid client id");
        AllegroApp app = appOptional.get();

        if(app.getAuthFlowType().equals(FlowTypes.AUTHORIZATION_CODE))
            return getAccessCodeUrl(app);
        else
            return generateTokenForUserDeviceFlow(app);
    }

    // Methods to authorize with "Authorization Code flow"
    // generate unique endpoint for application, to catch access code
    public String generateEndpoint(String username){
        int number = repository.countByUsername(username)+1;
        return String.format("%s-%d", username, number);
    }

    // create URL to allow client to authorize app
    private String getAccessCodeUrl(AllegroApp app) throws InvalidClientIdException{
        return authApiService.generateAccessCodeUrl(app.getClientId(), app.getEndpoint(), app.isSandbox());
    }

    // generate Token for User using catch code
    public void generateTokenForUserAuthCodeFlow(String code, String endpoint){
        Optional<AllegroApp> allegroAppOptional = repository.findByEndpoint(endpoint);

        if(allegroAppOptional.isEmpty()){
            logger.error(String.format("Invalid endpoint=%s to generate token.", endpoint));
            return;
        }

        AllegroApp allegroApp = allegroAppOptional.get();

        JSONObject tokenData = authApiService.generateTokenForUserWithAuthorizationCode(allegroApp.getClientId(),
                allegroApp.getClientSecret(),
                code,
                allegroApp.getEndpoint(),
                allegroApp.isSandbox());

        allegroApp.setTokenForUser(tokenData.getString("access_token"));
        allegroApp.setRefreshToken(tokenData.getString("refresh_token"));
        repository.save(allegroApp);
    }

    // Methods to authorize with "Device flow"
    public String generateTokenForUserDeviceFlow(AllegroApp app){
        JSONObject generationParams = authApiService.prepareGenerationTokenForUserWithDeviceFlow(
                app.getClientId(), app.getClientSecret(), app.isSandbox());

        CompletableFuture.runAsync(() -> makeRequestsForTokenDeviceFlow(app,
                                                                        generationParams.getString("device_code"),
                                                                        generationParams.getInt("interval")));

        return generationParams.getString("verification_uri_complete");
    }

    @Async
    public void makeRequestsForTokenDeviceFlow(AllegroApp app, String deviceCode, int interval){
        boolean isAuthPending = true;

        while (isAuthPending){
            logger.info("Generating token");
            JSONObject response = authApiService.requestForTokenForUserDeviceFlow(app.getClientId(),
                    app.getClientSecret(),
                    app.isSandbox(),
                    deviceCode);

            if(!response.has("error")){
                String tokenForUser = response.getString("access_token");
                String refreshToken = response.getString("refresh_token");
                app.setTokenForUser(tokenForUser);
                app.setRefreshToken(refreshToken);
                repository.save(app);
                isAuthPending = false;
            }else{
                try{
                    TimeUnit.SECONDS.sleep(interval);
                }catch (InterruptedException ignored){}
            }
        }
    }
}
