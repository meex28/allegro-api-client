package com.example.allegroapiclient.api_client.offer;

import com.example.allegroapiclient.api_client.AllegroApiDao;
import com.example.allegroapiclient.api_client.utils.APIUtils;
import com.example.allegroapiclient.auth.dto.Token;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class OffersAllegroApiDao extends AllegroApiDao {
    public OffersAllegroApiDao() {
        super();
    }

    public JSONObject getAllFieldsOfTheParticularOffer(String offerId, Token token){
        String uri = APIUtils.getBasicUriComponentsBuilder(token.isSandbox())
                .pathSegment("/sale/offers")
                .pathSegment(offerId)
                .build().toUriString();

        return get(uri, token.token());
    }

    public JSONObject createOfferDraft(JSONObject offer, Token token){
        String uri = APIUtils.getBasicUriComponentsBuilder(token.isSandbox())
                .pathSegment("/sale/offers")
                .build().toUriString();

        return post(uri, offer.toString(), token.token());
    }

    public JSONObject createOfferBasedOnProduct(JSONObject offer, Token token){
        String uri = APIUtils.getBasicUriComponentsBuilder(token.isSandbox())
                .pathSegment("/sale/product-offers")
                .build().toUriString();

        return post(uri, offer.toString(), token.token());
    }

    public JSONObject completeDraft(String offerId, JSONObject offer, Token token){
        return editFullOffer(offerId, offer, token);
    }

    public JSONObject editFullOffer(String offerId, JSONObject offer, Token token){
        String uri = APIUtils.getBasicUriComponentsBuilder(token.isSandbox())
                .pathSegment("/sale/offers")
                .pathSegment(offerId)
                .build().toUriString();

        return put(uri, offerId.toString(), token.token());
    }

    public JSONObject editOffer(String offerId, JSONObject offer, Token token){
        String uri = APIUtils.getBasicUriComponentsBuilder(token.isSandbox())
                .pathSegment("/sale/product-offers")
                .pathSegment(offerId)
                .build().toUriString();

        return patch(uri, offer.toString(), token.token());
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
