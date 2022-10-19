package com.example.allegroapiclient;

import com.example.allegroapiclient.api_client.offer.CategoriesAllegroApiDao;
import com.example.allegroapiclient.api_client.offer.ImagesAllegroApiDao;
import com.example.allegroapiclient.api_client.offer.ProductsAllegroApiDao;
import com.example.allegroapiclient.api_client.sale_settings.DeliveryAllegroApiDao;
import com.example.allegroapiclient.auth.allegro_auth.AllegroAppService;
import com.example.allegroapiclient.auth.dto.Token;
import com.example.allegroapiclient.auth.exceptions.InvalidClientIdException;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.HttpClientErrorException;

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
    public void getCategory() throws InvalidClientIdException, JSONException {
        Token token = authService.getToken(TestTokens.clientId);
        String id = "15";
        JSONObject category = categoriesAllegroApiDao.getCategoryById(id, token);
        System.out.println(category.toString(2));
    }

    @Test
    public void getCategoryParams() throws InvalidClientIdException, JSONException{
        Token token = authService.getToken(TestTokens.clientId);
        String id = "15";
        JSONObject category = categoriesAllegroApiDao.getParametersSupportedByCategory(id, token);
        System.out.println(category.toString(2));
    }

    @Test
    public void getProduct() throws InvalidClientIdException, JSONException {
        Token token = authService.getToken(TestTokens.clientId);
        JSONObject product = productsAllegroApiDao.searchProduct("7341920754004", token);
        System.out.println(product.toString(2));
    }

    @Test
    public void getProductDetails() throws InvalidClientIdException, JSONException{
        Token token = authService.getToken(TestTokens.clientId);
        String id = "decf8589-56ea-498b-852c-8f9071c567ff";
        JSONObject product = productsAllegroApiDao.getAllDataOfParticularProduct(id, token);
        System.out.println(product.toString(2));
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
