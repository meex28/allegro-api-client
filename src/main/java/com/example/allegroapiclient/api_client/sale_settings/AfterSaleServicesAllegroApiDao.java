package com.example.allegroapiclient.api_client.sale_settings;

import com.example.allegroapiclient.api_client.AllegroApiDao;
import com.example.allegroapiclient.api_client.utils.APIUtils;
import com.example.allegroapiclient.auth.dto.Token;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class AfterSaleServicesAllegroApiDao extends AllegroApiDao {
    public AfterSaleServicesAllegroApiDao() {
        super();
    }

    // Return policies
    public JSONObject getUsersReturnPolicies(Token token){
        String uri = APIUtils.getBasicUriComponentsBuilder(token.isSandbox())
                .pathSegment("/after-sales-service-conditions/return-policies")
                .build().toUriString();

        return get(uri, token.token());
    }

    public JSONObject getUsersReturnPolicy(String id, Token token){
        String uri = APIUtils.getBasicUriComponentsBuilder(token.isSandbox())
                .pathSegment("/after-sales-service-conditions/return-policies")
                .pathSegment(id)
                .build().toUriString();

        return get(uri, token.token());
    }

    public JSONObject getUsersImpliedWarranties(Token token){
        String uri = APIUtils.getBasicUriComponentsBuilder(token.isSandbox())
                .pathSegment("/after-sales-service-conditions/implied-warranties")
                .build().toUriString();

        return get(uri, token.token());
    }

    public JSONObject getUsersImpliedWarranty(String id, Token token){
        String uri = APIUtils.getBasicUriComponentsBuilder(token.isSandbox())
                .pathSegment("/after-sales-service-conditions/implied-warranties")
                .pathSegment(id)
                .build().toUriString();

        return get(uri, token.token());
    }

    public JSONObject getUsersWarranties(Token token){
        String uri = APIUtils.getBasicUriComponentsBuilder(token.isSandbox())
                .pathSegment("/after-sales-service-conditions/warranties")
                .build().toUriString();

        return get(uri, token.token());
    }

    public JSONObject getUsersWarranty(String id, Token token){
        String uri = APIUtils.getBasicUriComponentsBuilder(token.isSandbox())
                .pathSegment("/after-sales-service-conditions/warranties")
                .pathSegment(id)
                .build().toUriString();

        return get(uri, token.token());
    }
}
