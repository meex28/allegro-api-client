package com.example.allegroapiclient.api_client.offer;

import com.example.allegroapiclient.api_client.WebClientStatusCodeHandler;
import com.example.allegroapiclient.api_client.utils.APIUtils;
import com.example.allegroapiclient.auth.dto.Token;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class OffersAllegroApiDao {
    private final WebClient webClient;

    public OffersAllegroApiDao() {
        this.webClient = WebClient.builder()
                .defaultHeaders(APIUtils::setBasicContentType)
                .filter(WebClientStatusCodeHandler.errorResponseFilter)
                .build();
    }

    public JSONObject getAllFieldsOfTheParticularOffer(String offerId, Token token){
        String uri = APIUtils.getBasicUriComponentsBuilder(token.isSandbox())
                .pathSegment("/sale/offers")
                .pathSegment(offerId)
                .build().toUriString();

        String responseBody = webClient.get()
                .uri(uri)
                .headers(headers -> headers.setBearerAuth(token.token()))
                .retrieve()
                .bodyToMono(String.class)
                .block();
        return new JSONObject(responseBody);
    }

    public JSONObject createOfferDraft(JSONObject offer, Token token){
        String uri = APIUtils.getBasicUriComponentsBuilder(token.isSandbox())
                .pathSegment("/sale/offers")
                .build().toUriString();

        String responseBody = webClient.post()
                .uri(uri)
                .headers(headers -> headers.setBearerAuth(token.token()))
                .body(BodyInserters.fromValue(offer.toString()))
                .retrieve()
                .bodyToMono(String.class)
                .block();
        return new JSONObject(responseBody);
    }

    public JSONObject createOfferBasedOnProduct(JSONObject offer, Token token){
        String uri = APIUtils.getBasicUriComponentsBuilder(token.isSandbox())
                .pathSegment("/sale/product-offers")
                .build().toUriString();

        String responseBody = webClient.post()
                .uri(uri)
                .headers(headers -> headers.setBearerAuth(token.token()))
                .body(BodyInserters.fromValue(offer.toString()))
                .retrieve()
                .bodyToMono(String.class)
                .block();
        return new JSONObject(responseBody);
    }

    public JSONObject completeDraft(String offerId, JSONObject offer, Token token){
        return editFullOffer(offerId, offer, token);
    }

    public JSONObject editFullOffer(String offerId, JSONObject offer, Token token){
        String uri = APIUtils.getBasicUriComponentsBuilder(token.isSandbox())
                .pathSegment("/sale/offers")
                .pathSegment(offerId)
                .build().toUriString();

        String responseBody = webClient.put()
                .uri(uri)
                .headers(headers -> headers.setBearerAuth(token.token()))
                .body(BodyInserters.fromValue(offer.toString()))
                .retrieve()
                .bodyToMono(String.class)
                .block();
        return new JSONObject(responseBody);
    }

    public JSONObject editOffer(String offerId, JSONObject offer, Token token){
        String uri = APIUtils.getBasicUriComponentsBuilder(token.isSandbox())
                .pathSegment("/sale/product-offers")
                .pathSegment(offerId)
                .build().toUriString();

        String responseBody = webClient.patch()
                .uri(uri)
                .headers(headers -> headers.setBearerAuth(token.token()))
                .body(BodyInserters.fromValue(offer.toString()))
                .retrieve()
                .bodyToMono(String.class)
                .block();
        return new JSONObject(responseBody);
    }

    public void deleteDraftOffer(String offerId, Token token){
        String uri = APIUtils.getBasicUriComponentsBuilder(token.isSandbox())
                .pathSegment("/sale/offers")
                .pathSegment(offerId)
                .build().toUriString();

        webClient.delete()
                .uri(uri)
                .headers(headers ->  headers.setBearerAuth(token.token()))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
