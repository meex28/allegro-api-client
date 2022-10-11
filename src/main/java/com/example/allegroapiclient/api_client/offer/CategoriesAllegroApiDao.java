package com.example.allegroapiclient.api_client.offer;

import com.example.allegroapiclient.api_client.WebClientStatusCodeHandler;
import com.example.allegroapiclient.api_client.utils.APIUtils;
import com.example.allegroapiclient.auth.dto.Token;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class CategoriesAllegroApiDao {
    private final WebClient webClient;

    public CategoriesAllegroApiDao() {
        this.webClient = WebClient.builder()
                .defaultHeaders(APIUtils::setBasicContentType)
                .filter(WebClientStatusCodeHandler.errorResponseFilter)
                .build();
    }

    public JSONObject getIdsOfAllegroCategories(String id, Token token){
        String uri = APIUtils.getBasicUriComponentsBuilder(token.isSandbox())
                .pathSegment("/sale/categories")
                .queryParam("parent.id", id)
                .build().toUriString();

        String responseBody = webClient.get()
                .uri(uri)
                .headers(headers -> headers.setBearerAuth(token.token()))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return new JSONObject(responseBody);
    }

    public JSONObject getCategoryById(String id, Token token){
        String uri = APIUtils.getBasicUriComponentsBuilder(token.isSandbox())
                .pathSegment("/sale/categories")
                .pathSegment(id)
                .build().toUriString();

        String responseBody = webClient.get()
                .uri(uri)
                .headers(headers -> headers.setBearerAuth(token.token()))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return new JSONObject(responseBody);
    }

    public JSONObject getParametersSupportedByCategory(String id, Token token){
        String uri = APIUtils.getBasicUriComponentsBuilder(token.isSandbox())
                .pathSegment("/sale/categories")
                .pathSegment(id)
                .pathSegment("parameters")
                .build().toUriString();

        String responseBody = webClient.get()
                .uri(uri)
                .headers(headers -> headers.setBearerAuth(token.token()))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return new JSONObject(responseBody);
    }
}
