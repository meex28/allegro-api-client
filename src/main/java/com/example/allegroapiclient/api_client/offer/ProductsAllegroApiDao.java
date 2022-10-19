package com.example.allegroapiclient.api_client.offer;

import com.example.allegroapiclient.api_client.AllegroApiDao;
import com.example.allegroapiclient.api_client.utils.APIUtils;
import com.example.allegroapiclient.auth.dto.Token;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class ProductsAllegroApiDao extends AllegroApiDao {
    public ProductsAllegroApiDao() {
        super();
    }

    public JSONObject searchProduct(String phrase, Token token){
        String uri = APIUtils.getBasicUriComponentsBuilder(token.isSandbox())
                .pathSegment("/sale/products")
                .queryParam("phrase", phrase)
                .build().toUriString();
        return get(uri, token.token());
    }

    public JSONObject getAllDataOfParticularProduct(String productId, Token token){
        String uri = APIUtils.getBasicUriComponentsBuilder(token.isSandbox())
                .pathSegment("/sale/products")
                .pathSegment(productId)
                .build().toUriString();
        return get(uri, token.token());
    }
}
