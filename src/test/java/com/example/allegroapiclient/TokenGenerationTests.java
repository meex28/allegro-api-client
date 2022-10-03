package com.example.allegroapiclient;

import com.example.allegroapiclient.allegro_auth.AllegroAppAccessCodeController;
import com.example.allegroapiclient.allegro_auth.AllegroAppRepository;
import com.example.allegroapiclient.allegro_auth.AllegroAppService;
import com.example.allegroapiclient.allegro_auth.AllegroAuthApiService;
import com.example.allegroapiclient.dto.Token;
import com.example.allegroapiclient.entities.AllegroApp;
import com.example.allegroapiclient.exceptions.DeviceFlowTokenPending;
import com.example.allegroapiclient.exceptions.InvalidClientIdException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TokenGenerationTests {
    @Autowired
    AllegroAppService allegroAppService;

    @Autowired
    AllegroAuthApiService authApiService;

    @Autowired
    AllegroAppRepository repository;

    @Autowired
    AllegroAppAccessCodeController accessCodeController;

//    @BeforeAll
//    public void clearDatabase(){
//        repository.deleteAll();
//    }

    private void addNewApp(){
        AllegroApp app = new AllegroApp(TestTokens.clientId, TestTokens.clientSecret, true, "username");
        app.setEndpoint(TestTokens.endpoint);
        app.setNew(true);
        repository.save(app);
    }

    @Test
    void generateTokenForApplication() throws InvalidClientIdException {
        addNewApp();
        allegroAppService.generateTokenForApplication(TestTokens.clientId);
    }

    @Test
    void generateAccessCodeUrl(){
        String clientId = "a5f357c6035d440f84d41d5a762101f9";
        String url = authApiService.generateAccessCodeUrl(clientId, "aaa", true);
        System.out.println(url);
    }

    @Test
    void generateAccessToken(){
        String clientId = "a5f357c6035d440f84d41d5a762101f9";
        String clientSecret = "hMtRrbiQ4YchZ6MJNoFaRQwrm7PGO7R1LBFQj9K9h4zKAgeGJcmJguhpvC2d5Nqh";
        String token = authApiService.generateTokenForApplication(clientId, clientSecret, true);
        System.out.println(token);
    }

    @Test
    void tokenForUserDeviceFlowPreparing(){
        String clientId = "636ca94c10f44327b36b7f2d55635b72";
        String clientSecret = "YBJd3XO2nnUwfvFdixdLddz4UCpRhOya4TiOcDXHYjEFNYUwZJLvzM39vWOZPIt4";
        JSONObject result = authApiService.prepareGenerationTokenForUserWithDeviceFlow(clientId,
                clientSecret,
                true);
        System.out.println(result);
    }

    @Test
    void tokenForUserRequesting() throws DeviceFlowTokenPending {
        String clientId = "636ca94c10f44327b36b7f2d55635b72";
        String clientSecret = "YBJd3XO2nnUwfvFdixdLddz4UCpRhOya4TiOcDXHYjEFNYUwZJLvzM39vWOZPIt4";
        JSONObject response = authApiService.requestForTokenForUserDeviceFlow(clientId, clientSecret, true, "aaa");
        System.out.println(response);
    }

    @Test
    void isTokenValid(){
        String clientId = "636ca94c10f44327b36b7f2d55635b72";
        AllegroApp app = repository.findById(clientId).get();
        System.out.println(allegroAppService.isTokenValid(app.getTokenForUser()));
    }

    @Test
    void getUserToken() throws InvalidClientIdException {
        String clientId = "636ca94c10f44327b36b7f2d55635b72";
        Token token = allegroAppService.getToken(clientId);
        System.out.println(token);
    }
}
