package com.example.allegroapiclient.api_client;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

public class WebClientStatusCodeHandler {
    public static ExchangeFilterFunction errorResponseFilter = ExchangeFilterFunction
            .ofResponseProcessor(WebClientStatusCodeHandler::exchangeFilterResponseProcessor);

    public static Mono<ClientResponse> exchangeFilterResponseProcessor(ClientResponse response) {
        HttpStatus status = response.statusCode();

        if(status.is4xxClientError()){
            return response.bodyToMono(String.class)
                    .flatMap(body -> Mono.error(new HttpClientErrorException(status, body)));
        }

        return Mono.just(response);
    }
}
