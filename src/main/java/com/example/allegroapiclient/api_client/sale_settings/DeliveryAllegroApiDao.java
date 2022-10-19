package com.example.allegroapiclient.api_client.sale_settings;

import com.example.allegroapiclient.api_client.WebClientStatusCodeHandler;
import com.example.allegroapiclient.api_client.utils.APIUtils;
import com.example.allegroapiclient.auth.dto.Token;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class DeliveryAllegroApiDao {
    private final WebClient webClient;

    public DeliveryAllegroApiDao() {
        this.webClient = WebClient.builder()
                .defaultHeaders(APIUtils::setBasicContentType)
                .filter(WebClientStatusCodeHandler.errorResponseFilter)
                .build();
    }

    // Shipping rates
    public JSONObject getUsersShippingRates(Token token){
        String uri = APIUtils.getBasicUriComponentsBuilder(token.isSandbox())
                .pathSegment("/sale/shipping-rates")
                .build().toUriString();

        String responseBody = webClient.get()
                .uri(uri)
                .headers(headers -> headers.setBearerAuth(token.token()))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return new JSONObject(responseBody);
    }

    public JSONObject getListOfDeliveryMethods(Token token){
        String uri = APIUtils.getBasicUriComponentsBuilder(token.isSandbox())
                .pathSegment("/sale/delivery-methods")
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
