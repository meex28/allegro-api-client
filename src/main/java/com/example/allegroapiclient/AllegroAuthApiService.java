package com.example.allegroapiclient;

import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;

@Service
public class AllegroAuthApiService {
    private final WebClient webClient;

    // Endpoints for Allegro API
    private final String baseAllegroUrl = "allegro.pl";
    private final String baseAllegroSandboxUrl = "allegro.pl.allegrosandbox.pl";
    private final String generateAccessTokenUrl = "auth/oauth/token";
    private final String signInToAuthorizeURL = "auth/oauth/authorize";

    //TODO: change redirectUri
    private final String redirectUri = "http://localhost:8080/api/apps/code/%s";

    public AllegroAuthApiService() {
        this.webClient = WebClient.builder().build();
    }

    private UriComponentsBuilder getBasicUriComponentsBuilder(boolean isSandbox){
        String host = isSandbox ? baseAllegroSandboxUrl : baseAllegroUrl;
        return UriComponentsBuilder.newInstance()
                .scheme("https")
                .host(host);
    }

    public String generateTokenForApplication(String clientId, String clientSecret, boolean isSandbox){
        String url = getBasicUriComponentsBuilder(isSandbox)
                .pathSegment(generateAccessTokenUrl)
                .build().toUriString();

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.put("grant_type", Collections.singletonList("client_credentials"));

        String responseBody = webClient.post()
                .uri(url)
                .headers(headers -> headers.setBasicAuth(clientId, clientSecret))
                .headers(headers -> headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED))
                .body(BodyInserters.fromFormData(body))
                .retrieve()
                .bodyToMono(String.class)
                .block();


        JSONObject json = new JSONObject(responseBody);
        return json.getString("access_token");
    }

    // return URL for generating access code for "token for user"
    public String generateAccessCodeUrl(String clientId, String endpoint, boolean isSandbox){
        return getBasicUriComponentsBuilder(isSandbox)
                .pathSegment(signInToAuthorizeURL)
                .queryParam("response_type", "code")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", String.format(redirectUri, endpoint))
                .build().toUriString();
    }

    // generate Token For User
    // return JSONObject with among others access token, refresh token, expire time, scope
    public JSONObject generateTokenForUser(String clientId,
                                     String clientSecret,
                                     String code,
                                     String endpoint,
                                     boolean isSandbox){
        String uri = getBasicUriComponentsBuilder(isSandbox)
                .path(generateAccessTokenUrl)
                .toUriString();

        String responseBody = webClient.post()
                .uri(uri, uriBuilder -> uriBuilder
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("code", code)
                        .queryParam("redirect_uri", String.format(redirectUri, endpoint))
                        .build())
                .headers(headers -> headers.setBasicAuth(clientId, clientSecret))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return new JSONObject(responseBody);
    }
}
