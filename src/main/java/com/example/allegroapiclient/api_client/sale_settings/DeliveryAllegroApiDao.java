package com.example.allegroapiclient.api_client.sale_settings;

import com.example.allegroapiclient.api_client.AllegroApiDao;
import com.example.allegroapiclient.api_client.utils.APIUtils;
import com.example.allegroapiclient.auth.dto.Token;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class DeliveryAllegroApiDao extends AllegroApiDao {
    public DeliveryAllegroApiDao() {
        super();
    }

    // Shipping rates
    public JSONObject getUsersShippingRates(Token token){
        String uri = APIUtils.getBasicUriComponentsBuilder(token.isSandbox())
                .pathSegment("/sale/shipping-rates")
                .build().toUriString();

        return get(uri, token.token());
    }

    public JSONObject getListOfDeliveryMethods(Token token){
        String uri = APIUtils.getBasicUriComponentsBuilder(token.isSandbox())
                .pathSegment("/sale/delivery-methods")
                .build().toUriString();

        return get(uri, token.token());
    }
}
