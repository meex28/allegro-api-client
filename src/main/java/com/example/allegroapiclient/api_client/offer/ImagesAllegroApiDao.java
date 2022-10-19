package com.example.allegroapiclient.api_client.offer;

import com.example.allegroapiclient.api_client.AllegroApiDao;
import com.example.allegroapiclient.api_client.utils.APIUtils;
import com.example.allegroapiclient.auth.dto.Token;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class ImagesAllegroApiDao extends AllegroApiDao {
    public ImagesAllegroApiDao() {
        super();
    }

    public JSONObject uploadByUrl(String url, Token token){
        String uri = APIUtils.getBasicImageUriComponentsBuilder(token.isSandbox())
                .pathSegment("sale/images")
                .build().toUriString();

        JSONObject body = new JSONObject()
                .put("url", url);

        return post(uri, body.toString(), token.token());
    }
}
