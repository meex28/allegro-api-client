package com.example.allegroapiclient;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.*;

@SpringBootTest
class AllegroApiClientApplicationTests {
    @Autowired
    AllegroAuthApiService allegroAuthApiService;

    @Test
    void contextLoads() {
    }

    @Test
    void generateAccessToken(){
        String clientId = "a5f357c6035d440f84d41d5a762101f9";
        String clientSecret = "hMtRrbiQ4YchZ6MJNoFaRQwrm7PGO7R1LBFQj9K9h4zKAgeGJcmJguhpvC2d5Nqh";
        String token = allegroAuthApiService.generateAccessCode(clientId, clientSecret, true);
        System.out.println(token);
    }
}
