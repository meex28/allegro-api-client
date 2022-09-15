package com.example.allegroapiclient;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class AllegroAuthApiService {
    private final RestTemplate restTemplate;
    // Endpoints for Allegro API
    private final String baseAllegroUrl = "allegro.pl";
    private final String baseAllegroSandboxUrl = "allegro.pl.allegrosandbox.pl";
    private final String signInToAuthorizeURL = "/auth/oauth/authorize";

    //TODO: make redirect uri in properties
    private final String redirectUri = "http://localhost:8080/api/apps/code";
    public AllegroAuthApiService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    private UriComponentsBuilder getBasicUriComponentsBuilder(boolean isSandbox){
        String host = isSandbox ? baseAllegroSandboxUrl : baseAllegroUrl;
        return UriComponentsBuilder.newInstance()
                .scheme("https")
                .host(host);
    }

    public String generateAccessCode(String clientId, String clientSecret, boolean isSandbox){
        String url = getBasicUriComponentsBuilder(isSandbox)
                .path(signInToAuthorizeURL)
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .queryParam("redirect_uri", redirectUri)
                .build().toUriString();


    }
}
