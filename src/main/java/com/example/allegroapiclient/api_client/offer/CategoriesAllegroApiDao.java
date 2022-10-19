package com.example.allegroapiclient.api_client.offer;

import com.example.allegroapiclient.api_client.AllegroApiDao;
import com.example.allegroapiclient.api_client.utils.APIUtils;
import com.example.allegroapiclient.auth.dto.Token;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class CategoriesAllegroApiDao extends AllegroApiDao {
    public CategoriesAllegroApiDao() {
        super();
    }

    public JSONObject getIdsOfAllegroCategories(String id, Token token){
        String uri = APIUtils.getBasicUriComponentsBuilder(token.isSandbox())
                .pathSegment("/sale/categories")
                .queryParam("parent.id", id)
                .build().toUriString();

        return get(uri, token.token());
    }

    public JSONObject getCategoryById(String id, Token token){
        String uri = APIUtils.getBasicUriComponentsBuilder(token.isSandbox())
                .pathSegment("/sale/categories")
                .pathSegment(id)
                .build().toUriString();

        return get(uri, token.token());
    }

    public JSONObject getParametersSupportedByCategory(String id, Token token){
        String uri = APIUtils.getBasicUriComponentsBuilder(token.isSandbox())
                .pathSegment("/sale/categories")
                .pathSegment(id)
                .pathSegment("parameters")
                .build().toUriString();

        return get(uri, token.token());
    }
}
