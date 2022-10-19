package com.example.allegroapiclient.api_client;

import com.example.allegroapiclient.api_client.utils.APIUtils;
import org.json.JSONObject;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

public abstract class AllegroApiDao {
    protected final WebClient webClient;

    public AllegroApiDao() {
        this.webClient = WebClient.builder()
                .defaultHeaders(APIUtils::setBasicContentType)
                .filter(WebClientStatusCodeHandler.errorResponseFilter)
                .build();
    }

    protected JSONObject get(String uri, String token){
        String responseBody = webClient.get()
                .uri(uri)
                .headers(headers -> headers.setBearerAuth(token))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return new JSONObject(responseBody);
    }

    protected JSONObject post(String uri, String body, String token){
        String responseBody = webClient.post()
                .uri(uri)
                .headers(headers -> headers.setBearerAuth(token))
                .body(BodyInserters.fromValue(body))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return new JSONObject(responseBody);
    }

    protected JSONObject put(String uri, String body, String token){
        String responseBody = webClient.put()
                .uri(uri)
                .headers(headers -> headers.setBearerAuth(token))
                .body(BodyInserters.fromValue(body))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return new JSONObject(responseBody);
    }

    protected JSONObject patch(String uri, String body, String token){
        String responseBody = webClient.patch()
                .uri(uri)
                .headers(headers -> headers.setBearerAuth(token))
                .body(BodyInserters.fromValue(body))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return new JSONObject(responseBody);
    }

    protected JSONObject delete(String uri, String token){
        String responseBody = webClient.delete()
                .uri(uri)
                .headers(headers -> headers.setBearerAuth(token))
                .retrieve()
                .bodyToMono(String.class)
                .block();
        return new JSONObject(responseBody);
    }
}
