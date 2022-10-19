package com.example.allegroapiclient;

import com.example.allegroapiclient.api_client.offer.OffersAllegroApiDao;
import com.example.allegroapiclient.api_client.utils.OfferBuilder;
import com.example.allegroapiclient.auth.allegro_auth.AllegroAppService;
import com.example.allegroapiclient.auth.dto.Token;
import com.example.allegroapiclient.auth.exceptions.InvalidClientIdException;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AllegroApiOffersTest {
    @Autowired
    AllegroAppService authService;

    @Autowired
    OffersAllegroApiDao offersAllegroApiDao;

    @Test
    public void getOfferById() throws InvalidClientIdException {
        Token token = authService.getToken(TestTokens.clientId);
        String offerId = "7690499818";
        JSONObject offer = offersAllegroApiDao.getAllFieldsOfTheParticularOffer(offerId, token);
        System.out.println(offer);
    }

    @Test
    public void createOfferDraft() throws InvalidClientIdException{
        Token token = authService.getToken(TestTokens.clientId);
        JSONObject offer = OfferBuilder.get("test-offer").build();
        System.out.println(offersAllegroApiDao.createOfferDraft(offer, token));
    }

    @Test
    public void deleteOfferDraft() throws InvalidClientIdException {
        Token token = authService.getToken(TestTokens.clientId);
        String id = "7695163573";
        offersAllegroApiDao.deleteDraftOffer(id, token);
    }

    @Test
    public void editOffer() throws InvalidClientIdException{
        Token token = authService.getToken(TestTokens.clientId);
        String id = "7695163573";
        JSONObject offer = offersAllegroApiDao.getAllFieldsOfTheParticularOffer(id, token);
        offer = OfferBuilder.get(offer)
                .category("319171")
                .build();
        offersAllegroApiDao.editFullOffer(id, offer, token);
    }

    @Test
    public void editOfferWithPatch() throws InvalidClientIdException {
        Token token = authService.getToken(TestTokens.clientId);
        String id = "7695163573";
        JSONObject offer = OfferBuilder.get()
                .external("External id test")
                .build();
        offersAllegroApiDao.editOffer(id, offer, token);
    }
}
