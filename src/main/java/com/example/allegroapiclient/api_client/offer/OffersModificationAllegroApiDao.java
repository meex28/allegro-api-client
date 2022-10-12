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

    private String getCommandId(String body){
        JSONObject json = new JSONObject(body);
        return json.getString("id");
    }

    private String batchModification(String uri, JSONObject modifications, List<String> offersIds,
                                     boolean isPublication, Token token){
        List<JSONObject> offers = offersIds.stream()
                .map(offer -> new JSONObject().put("id", offer))
                .collect(Collectors.toList());
        JSONArray offerCriteria = new JSONArray()
                .put(new JSONObject()
                        .put("offers", new JSONArray(offers))
                        .put("type", "CONTAINS_OFFERS"));

        String modificationKey = isPublication ? "publication" : "modification";
        JSONObject body = new JSONObject()
                .put(modificationKey, modifications)
                .put("offerCriteria", offerCriteria);

        String responseBody = webClient.put()
                .uri(uri)
                .headers(headers -> headers.setBearerAuth(token.token()))
                .body(BodyInserters.fromValue(body.toString()))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return getCommandId(responseBody);
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

        return getCommandId(responseBody);
    }

    public String modifyOffers(String commandId, JSONObject modifications, List<String> offersIds, Token token){
        String uri = APIUtils.getBasicUriComponentsBuilder(token.isSandbox())
                .pathSegment("/sale/offer-modification-commands")
                .pathSegment(commandId)
                .build().toUriString();

        return batchModification(uri, modifications, offersIds, false, token);
    }

    // Methods to change offers price
    private enum OfferPriceModificationTypes{
        FIXED_PRICE, INCREASE_PRICE, DECREASE_PRICE, INCREASE_PERCENTAGE, DECREASE_PERCENTAGE
    }

    private String modifyOffersPrice(String commandId, JSONObject modifications, List<String> offersIds, Token token){
        String uri = APIUtils.getBasicUriComponentsBuilder(token.isSandbox())
                .pathSegment("/sale/offer-price-change-commands")
                .pathSegment(commandId)
                .build().toUriString();

        return batchModification(uri, modifications, offersIds, false, token);
    }

    public String modifyOffersFixedPrice(String commandId, String amount, String currency, List<String> offersIds,
                                          Token token){
        JSONObject modification = new JSONObject()
                .put("type", OfferPriceModificationTypes.FIXED_PRICE)
                .put("price", new JSONObject()
                        .put("amount", amount)
                        .put("currency", currency));

        return modifyOffersPrice(commandId, modification, offersIds, token);
    }

    public String increaseOffersPrice(String commandId, String amount, String currency, List<String> offersIds,
                                      Token token){
        JSONObject modification = new JSONObject()
                .put("type", OfferPriceModificationTypes.INCREASE_PRICE)
                .put("value", new JSONObject()
                        .put("amount", amount)
                        .put("currency", currency));

        return modifyOffersPrice(commandId, modification, offersIds, token);
    }

    public String decreaseOffersPrice(String commandId, String amount, String currency, List<String> offersIds,
                                      Token token){
        JSONObject modification = new JSONObject()
                .put("type", OfferPriceModificationTypes.DECREASE_PRICE)
                .put("value", new JSONObject()
                        .put("amount", amount)
                        .put("currency", currency));

        return modifyOffersPrice(commandId, modification, offersIds, token);
    }

    public String increasePercentageOffersPrice(String commandId, String percentage, List<String> offersIds,
                                                Token token){
        JSONObject modification = new JSONObject()
                .put("type", OfferPriceModificationTypes.INCREASE_PERCENTAGE)
                .put("percentage", percentage);

        return modifyOffersPrice(commandId, modification, offersIds, token);
    }

    public String decreasePercentageOffersPrice(String commandId, String percentage, List<String> offersIds,
                                                Token token){
        JSONObject modification = new JSONObject()
                .put("type", OfferPriceModificationTypes.DECREASE_PRICE)
                .put("percentage", percentage);

        return modifyOffersPrice(commandId, modification, offersIds, token);
    }

    // Methods to change offers quantity
    private enum OfferQuantityModificationTypes{
        FIXED, GAIN
    }

    private String modifyOffersQuantity(String commandId, JSONObject modifications, List<String> offersIds,
                                        Token token){
        String uri = APIUtils.getBasicUriComponentsBuilder(token.isSandbox())
                .pathSegment("/sale/offer-quantity-change-commands")
                .pathSegment(commandId)
                .build().toUriString();

        return batchModification(uri, modifications, offersIds, false, token);
    }

    public String modifyOffersFixedQuantity(String commandId, String value, List<String> offersIds, Token token){
        JSONObject modification = new JSONObject()
                .put("changeType", OfferQuantityModificationTypes.FIXED)
                .put("value", value);

        return modifyOffersQuantity(commandId, modification, offersIds, token);
    }

    public String modifyOffersGainQuantity(String commandId, String value, List<String> offersIds, Token token){
        JSONObject modification = new JSONObject()
                .put("changeType", OfferQuantityModificationTypes.GAIN)
                .put("value", value);

        return modifyOffersQuantity(commandId, modification, offersIds, token);
    }

    // Methods working with publication status modifying
    private enum PublicationModifierStatus{
        ACTIVATE, END
    }

    public String modifyOffersPublicationStatus(String commandId, PublicationModifierStatus action, String scheduledFor,
                                                 List<String> offersIds, Token token){
        String uri = APIUtils.getBasicUriComponentsBuilder(token.isSandbox())
                .pathSegment("/sale/offer-publication-commands")
                .pathSegment(commandId)
                .build().toUriString();

        JSONObject publication = new JSONObject()
                .put("action", action);

        if(scheduledFor != null)
            publication.put("scheduledFor", scheduledFor);

        return batchModification(uri, publication, offersIds, true, token);
    }

    public String modifyOffersPublicationStatus(String commandId, PublicationModifierStatus action,
                                                List<String> offersIds, Token token){
        return modifyOffersPublicationStatus(commandId, action, null, offersIds, token);
    }

    // Methods working with command summaries and reports
    private JSONObject commandSummaryOrReport(String uri, String token){
        String responseBody = webClient.get()
                .uri(uri)
                .headers(headers -> headers.setBearerAuth(token))
                .retrieve()
                .bodyToMono(String.class)
                .block();
        return new JSONObject(responseBody);
    }

    public JSONObject modificationCommandSummary(String commandId, Token token){
        String uri = APIUtils.getBasicUriComponentsBuilder(token.isSandbox())
                .pathSegment("/sale/offer-modification-commands")
                .pathSegment(commandId)
                .build().toUriString();

        return commandSummaryOrReport(uri, token.token());
    }

    public JSONObject changePriceCommandSummary(String commandId, Token token){
        String uri = APIUtils.getBasicUriComponentsBuilder(token.isSandbox())
                .pathSegment("/sale/offer-price-change-commands")
                .pathSegment(commandId)
                .build().toUriString();

        return commandSummaryOrReport(uri, token.token());
    }

    public JSONObject changeQuantityCommandSummary(String commandId, Token token){
        String uri = APIUtils.getBasicUriComponentsBuilder(token.isSandbox())
                .pathSegment("/sale/offer-quantity-change-commands")
                .pathSegment(commandId)
                .build().toUriString();

        return commandSummaryOrReport(uri, token.token());
    }

    public JSONObject modificationCommandDetailedReport(String commandId, Token token){
        String uri = APIUtils.getBasicUriComponentsBuilder(token.isSandbox())
                .pathSegment("/sale/offer-modification-commands")
                .pathSegment(commandId)
                .pathSegment("/tasks")
                .build().toUriString();

        return commandSummaryOrReport(uri, token.token());
    }

    public JSONObject changePriceDetailedReport(String commandId, Token token){
        String uri = APIUtils.getBasicUriComponentsBuilder(token.isSandbox())
                .pathSegment("/sale/offer-price-change-commands")
                .pathSegment(commandId)
                .pathSegment("/tasks")
                .build().toUriString();

        return commandSummaryOrReport(uri, token.token());
    }

    public JSONObject changeQuantityDetailedReport(String commandId, Token token){
        String uri = APIUtils.getBasicUriComponentsBuilder(token.isSandbox())
                .pathSegment("/sale/offer-quantity-change-commands")
                .pathSegment(commandId)
                .pathSegment("/tasks")
                .build().toUriString();

        return commandSummaryOrReport(uri, token.token());
    }
}
