package com.example.allegroapiclient;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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

    @BeforeAll
    public void clearDatabase(){
        repository.deleteAll();
    }

    private void addNewApp(){
        AllegroApp app = new AllegroApp(TestTokens.clientId, TestTokens.clientSecret, true, "username");
        app.setEndpoint(TestTokens.endpoint);
        app.setNew(true);
        repository.save(app);
    }

    @Test
    void generateTokenForApplication(){
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
}
