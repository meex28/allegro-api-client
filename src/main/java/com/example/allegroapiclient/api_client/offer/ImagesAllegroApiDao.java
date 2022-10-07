package com.example.allegroapiclient.api_client.offer;

import com.example.allegroapiclient.api_client.WebClientStatusCodeHandler;
import com.example.allegroapiclient.api_client.utils.APIUtils;
import com.example.allegroapiclient.auth.dto.Token;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ImagesAllegroApiDao {
    private final WebClient webClient;

    public ImagesAllegroApiDao() {
        this.webClient = WebClient.builder()
                .defaultHeaders(APIUtils::setBasicContentType)
                .filter(WebClientStatusCodeHandler.errorResponseFilter)
                .build();
    }

    public JSONObject uploadByUrl(String url, Token token){
        String uri = APIUtils.getBasicImageUriComponentsBuilder(token.isSandbox())
                .pathSegment("sale/images")
                .build().toUriString();

        JSONObject body = new JSONObject()
                .put("url", url);

        String responseBody = webClient.post()
                .uri(uri)
                .headers(headers -> headers.setBearerAuth(token.token()))
                .body(BodyInserters.fromValue(body.toString()))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return new JSONObject(responseBody);
    }
}
