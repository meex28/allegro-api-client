package com.example.allegroapiclient;

import com.example.allegroapiclient.api_client.offer.OffersModificationAllegroApiDao;
import com.example.allegroapiclient.api_client.utils.OfferModificationBuilder;
import com.example.allegroapiclient.auth.allegro_auth.AllegroAppService;
import com.example.allegroapiclient.auth.dto.Token;
import com.example.allegroapiclient.auth.exceptions.InvalidClientIdException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class BatchOfferModificationApiTest {
    @Autowired
    AllegroAppService authService;

    @Autowired
    OffersModificationAllegroApiDao offersModificationAllegroApiDao;

    @Test
    public void modifyBuyNowPrice() throws InvalidClientIdException {
        String id = "7693396259";
        Token token = authService.getToken(TestTokens.clientId);
        String commandId = "6365996a-6cae-11e9-a923-1681be663d3e";

        String idResponse = offersModificationAllegroApiDao.modifyBuyNowPrice(
                commandId, id, "100", "PLN", token);
        assert idResponse.equals(commandId);
    }

    @Test
    public void batchOfferModification() throws InvalidClientIdException{
        List<String> ids = List.of("7693396259", "7692033855");
        Token token = authService.getToken(TestTokens.clientId);
        JSONObject modifications = OfferModificationBuilder.get()
                .promotionBold(true)
                .build();
        String commandId = "6365d96a-6cae-11e9-a923-1681be663d3e";
        String commandIdReturn = offersModificationAllegroApiDao.modifyOffers(commandId, modifications, ids, token);
        assert commandId.equals(commandIdReturn);
    }
}
