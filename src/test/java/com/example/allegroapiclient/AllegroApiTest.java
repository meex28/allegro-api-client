package com.example.allegroapiclient;

import com.example.allegroapiclient.api_client.offer.CategoriesAllegroApiDao;
import com.example.allegroapiclient.api_client.offer.ImagesAllegroApiDao;
import com.example.allegroapiclient.api_client.offer.ProductsAllegroApiDao;
import com.example.allegroapiclient.auth.allegro_auth.AllegroAppService;
import com.example.allegroapiclient.auth.dto.Token;
import com.example.allegroapiclient.auth.exceptions.InvalidClientIdException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AllegroApiTest {
    @Autowired
    CategoriesAllegroApiDao categoriesAllegroApiDao;

    @Autowired
    ProductsAllegroApiDao productsAllegroApiDao;

    @Autowired
    ImagesAllegroApiDao imagesAllegroApiDao;

    @Autowired
    AllegroAppService authService;

    @Test
    public void getIdsCategories() throws InvalidClientIdException {
        Token token = authService.getToken(TestTokens.clientId);
        JSONObject categories = categoriesAllegroApiDao.getIdsOfAllegroCategories(
                "954b95b6-43cf-4104-8354-dea4d9b10ddf", token);
        System.out.println(categories);
    }

    @Test
    public void getProduct() throws InvalidClientIdException{
        Token token = authService.getToken(TestTokens.clientId);
        JSONObject product = productsAllegroApiDao.searchProduct("5906312705198", token);
        System.out.println(product);
    }

    @Test
    public void uploadImage() throws InvalidClientIdException {
        String url = "https://media.istockphoto.com/photos/" +
                "cat-surfing-on-internet-picture-id1172290687" +
                "?b=1&k=20&m=1172290687&s=170667a&w=0&h=9Lf8ihc3segSYkjtUY1gBbxhCK7Gs02ZavBlxwCsUs4=";
        Token token = authService.getToken(TestTokens.clientId);

        JSONObject response = imagesAllegroApiDao.uploadByUrl(url, token);
        System.out.println(response);
    }
}
