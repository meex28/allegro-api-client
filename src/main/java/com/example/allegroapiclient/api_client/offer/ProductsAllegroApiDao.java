package com.example.allegroapiclient.api_client.offer;

import com.example.allegroapiclient.api_client.WebClientStatusCodeHandler;
import com.example.allegroapiclient.api_client.utils.APIUtils;
import com.example.allegroapiclient.auth.dto.Token;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ProductsAllegroApiDao {
    private final WebClient webClient;

    public ProductsAllegroApiDao() {
        this.webClient = WebClient.builder()
                .defaultHeaders(APIUtils::setBasicContentType)
                .filter(WebClientStatusCodeHandler.errorResponseFilter)
                .build();
    }

    public JSONObject searchProduct(String phrase, Token token){
        String uri = APIUtils.getBasicUriComponentsBuilder(token.isSandbox())
                .pathSegment("/sale/products")
                .queryParam("phrase", phrase)
                .build().toUriString();

        String responseBody = webClient.get()
                .uri(uri)
                .headers(headers -> headers.setBearerAuth(token.token()))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return new JSONObject(responseBody);
    }

    public JSONObject getAllDataOfParticularProduct(String productId, Token token){
        String uri = APIUtils.getBasicUriComponentsBuilder(token.isSandbox())
                .pathSegment("/sale/products")
                .pathSegment(productId)
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
