package com.example.allegroapiclient;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class AllegroAuthApiService {
    private final WebClient webClient;

    // Endpoints for Allegro API
    private final String baseAllegroUrl = "allegro.pl";
    private final String baseAllegroSandboxUrl = "allegro.pl.allegrosandbox.pl";
    private final String generateAccessTokenUrl = "/auth/oauth/token";
    private final String signInToAuthorizeURL = "/auth/oauth/authorize";

    //TODO: make redirect uri in properties
    private final String redirectUri = "http://localhost:8080/";

    public AllegroAuthApiService() {
        this.webClient = WebClient.builder().build();
    }

    private UriComponentsBuilder getBasicUriComponentsBuilder(boolean isSandbox){
        String host = isSandbox ? baseAllegroSandboxUrl : baseAllegroUrl;
        return UriComponentsBuilder.newInstance()
                .scheme("https")
                .host(host);
    }

    public String generateAccessCode(String clientId, String clientSecret, boolean isSandbox){
        String url = getBasicUriComponentsBuilder(isSandbox)
                .path(generateAccessTokenUrl)
                .build().toUriString();

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.put("grant_type", Collections.singletonList("client_credentials"));

        WebClient.RequestHeadersSpec<?> responseSpec = webClient.post()
                .uri(url)
                .headers(headers -> headers.setBasicAuth(clientId, clientSecret))
                .headers(headers -> headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED))
                .body(BodyInserters.fromFormData(body));

        return responseSpec.retrieve().bodyToMono(String.class)
                .block();
    }
}
