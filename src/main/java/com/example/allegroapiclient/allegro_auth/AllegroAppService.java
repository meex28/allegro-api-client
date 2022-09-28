package com.example.allegroapiclient.allegro_auth;

import com.example.allegroapiclient.allegro_auth.token_generation.ApplicationTokenGeneration;
import com.example.allegroapiclient.allegro_auth.token_generation.AuthorizationCodeFlowTokenGeneration;
import com.example.allegroapiclient.allegro_auth.token_generation.DeviceFlowTokenGeneration;
import com.example.allegroapiclient.entities.AllegroApp;
import com.example.allegroapiclient.entities.FlowTypes;
import com.example.allegroapiclient.exceptions.InvalidClientIdException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AllegroAppService {
    // Beans
    private final AllegroAppRepository repository;
    private final AllegroAuthApiService authApiService;

    private final Logger logger = LoggerFactory.getLogger(AllegroAppService.class);
    private final ApplicationTokenGeneration applicationTokenGeneration;
    private final AuthorizationCodeFlowTokenGeneration authorizationCodeFlowTokenGeneration;
    private final DeviceFlowTokenGeneration deviceFlowTokenGeneration;


    @Autowired
    public AllegroAppService(AllegroAppRepository repository,
                             AllegroAuthApiService authApiService,
                             ApplicationTokenGeneration applicationTokenGeneration,
                             AuthorizationCodeFlowTokenGeneration authorizationCodeFlowTokenGeneration,
                             DeviceFlowTokenGeneration deviceFlowTokenGeneration) {
        this.repository = repository;
        this.authApiService = authApiService;
        this.applicationTokenGeneration = applicationTokenGeneration;
        this.authorizationCodeFlowTokenGeneration = authorizationCodeFlowTokenGeneration;
        this.deviceFlowTokenGeneration = deviceFlowTokenGeneration;
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

        app = applicationTokenGeneration.generate(app);
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
    // used to generate Authorization Code flow Token
    public String generateEndpoint(String username){
        int number = repository.countByUsername(username)+1;
        return String.format("%s-%d", username, number);
    }

    // create URL to allow client to authorize app
    private String getAccessCodeUrl(AllegroApp app) throws InvalidClientIdException{
        return authorizationCodeFlowTokenGeneration.generateAuthURL(app);
    }

    // generate Token for User using catch code and Authorization Code flow
    public void generateTokenForUserAuthCodeFlow(String code, String endpoint){
        Optional<AllegroApp> allegroAppOptional = repository.findByEndpoint(endpoint);
        if(allegroAppOptional.isEmpty()){
            logger.error(String.format("Invalid endpoint=%s to generate token.", endpoint));
            return;
        }
        AllegroApp allegroApp = allegroAppOptional.get();

        allegroApp = authorizationCodeFlowTokenGeneration.generateToken(allegroApp, code);

        repository.save(allegroApp);
    }

    // Methods to authorize with "Device flow"
    public String generateTokenForUserDeviceFlow(AllegroApp app){
        JSONObject generationParams = deviceFlowTokenGeneration.initializeTokenGeneration(app);

        deviceFlowTokenGeneration.generate(app,
                generationParams.getString("device_code"),
                generationParams.getInt("interval"));

        return generationParams.getString("verification_uri_complete");
    }
}
