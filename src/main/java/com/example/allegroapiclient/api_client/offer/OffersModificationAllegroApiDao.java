package com.example.allegroapiclient.api_client.offer;

import com.example.allegroapiclient.api_client.WebClientStatusCodeHandler;
import com.example.allegroapiclient.api_client.exceptions.BatchOfferModificationError;
import com.example.allegroapiclient.api_client.utils.APIUtils;
import com.example.allegroapiclient.auth.dto.Token;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

// service used to make offer modifications that required commandId
@Service
public class OffersModificationAllegroApiDao {
    private final WebClient webClient;

    public OffersModificationAllegroApiDao() {
        this.webClient = WebClient.builder()
                .defaultHeaders(APIUtils::setBasicContentType)
                .filter(WebClientStatusCodeHandler.errorResponseFilter)
                .build();
    }

    private String commandIdOrException(String body){
        JSONObject json = new JSONObject(body);

        if(json.keySet().contains("errors"))
            throw new BatchOfferModificationError(json.toString());
        else
            return json.getString("id");
    }

    public String modifyBuyNowPrice(String commandId, String offerId, String amount, String currency, Token token){
        JSONObject body = new JSONObject()
                .put("id", commandId)
                .put("input", new JSONObject()
                        .put("buyNowPrice", new JSONObject()
                                .put("amount", amount)
                                .put("currency", currency)));
        String uri = APIUtils.getBasicUriComponentsBuilder(token.isSandbox())
                .pathSegment("/offers")
                .pathSegment(offerId)
                .pathSegment("change-price-commands")
                .pathSegment(commandId)
                .build().toUriString();

        String responseBody = webClient.put()
                .uri(uri)
                .headers(headers -> headers.setBearerAuth(token.token()))
                .body(BodyInserters.fromValue(body.toString()))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return commandIdOrException(responseBody);
    }

    public String modifyOffers(String commandId, JSONObject modifications, List<String> offersIds, Token token){
        List<JSONObject> offers = offersIds.stream()
                .map(offer -> new JSONObject().put("id", offer))
                .collect(Collectors.toList());
        JSONArray offerCriteria = new JSONArray()
                .put(new JSONObject()
                        .put("offers", new JSONArray(offers))
                        .put("type", "CONTAINS_OFFERS"));
        JSONObject body = new JSONObject()
                .put("modification", modifications)
                .put("offerCriteria", offerCriteria);

        String uri = APIUtils.getBasicUriComponentsBuilder(token.isSandbox())
                .pathSegment("/sale/offer-modification-commands")
                .pathSegment(commandId)
                .build().toUriString();

        String responseBody = webClient.put()
                .uri(uri)
                .headers(headers -> headers.setBearerAuth(token.token()))
                .body(BodyInserters.fromValue(body.toString()))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return commandIdOrException(responseBody);
    }
}
