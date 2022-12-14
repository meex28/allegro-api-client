package com.example.allegroapiclient.auth.allegro_auth;

import com.example.allegroapiclient.auth.entities.AllegroApp;
import com.example.allegroapiclient.auth.entities.FlowTypes;
import com.example.allegroapiclient.auth.exceptions.DeviceFlowTokenPending;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    private final String deviceFlowAuthorizationURL = "auth/oauth/device";

    //TODO: change redirectUri
    private final String redirectUri = "http://localhost:8080/api/apps/code/%s";

    @Autowired
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

    // return URL to generate authorization code used in generation token for user using Authorization Code flow
    public String generateAccessCodeUrl(String clientId, String endpoint, boolean isSandbox){
        return getBasicUriComponentsBuilder(isSandbox)
                .pathSegment(signInToAuthorizeURL)
                .queryParam("response_type", "code")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", String.format(redirectUri, endpoint))
                .build().toUriString();
    }

    // generate Token For User using Authorization Code flow
    // return JSONObject with among others access token, refresh token, expire time, scope
    public JSONObject generateTokenForUserWithAuthorizationCode(String clientId,
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

    // initialize required codes to generate Token for User using Device flow
    public JSONObject prepareGenerationTokenForUserWithDeviceFlow(String clientId,
                                                                  String clientSecret,
                                                                  boolean isSandbox){
        String url = getBasicUriComponentsBuilder(isSandbox)
                .pathSegment(deviceFlowAuthorizationURL)
                .build().toUriString();

        String responseBody = webClient.post()
                .uri(url, uriBuilder -> uriBuilder
                        .queryParam("client_id", clientId)
                        .build())
                .headers(headers -> headers.setBasicAuth(clientId, clientSecret))
                .headers(headers -> headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return new JSONObject(responseBody);
    }

    public JSONObject requestForTokenForUserDeviceFlow(String clientId,
                                                       String clientSecret,
                                                       boolean isSandbox,
                                                       String deviceCode) throws DeviceFlowTokenPending{
        String url = getBasicUriComponentsBuilder(isSandbox)
                .pathSegment(generateAccessTokenUrl)
                .build().toUriString();

        String responseBody = webClient.post()
                .uri(url, uriBuilder -> uriBuilder
                        .queryParam("grant_type", "urn:ietf:params:oauth:grant-type:device_code")
                        .queryParam("device_code", deviceCode)
                        .build())
                .headers(headers -> headers.setBasicAuth(clientId, clientSecret))
                .retrieve()
                .onStatus(HttpStatus.BAD_REQUEST::equals,
                        response -> response.bodyToMono(String.class).map(DeviceFlowTokenPending::new))
                .bodyToMono(String.class)
                .block();

        return new JSONObject(responseBody);
    }

    public JSONObject refreshToken(AllegroApp app){
        String url = getBasicUriComponentsBuilder(app.isSandbox())
                .pathSegment(generateAccessTokenUrl)
                .build().toUriString();

        String responseBody = webClient.post()
                .uri(url, uriBuilder -> {
                    uriBuilder = uriBuilder.queryParam("grant_type", "refresh_token")
                            .queryParam("refresh_token", app.getRefreshToken());
                    if(app.getAuthFlowType().equals(FlowTypes.AUTHORIZATION_CODE))
                        uriBuilder = uriBuilder.queryParam("redirect_uri",
                                                            String.format(redirectUri, app.getEndpoint()));
                    return uriBuilder.build();
                })
                .headers(headers -> headers.setBasicAuth(app.getClientId(), app.getClientSecret()))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return new JSONObject(responseBody);
    }


}
