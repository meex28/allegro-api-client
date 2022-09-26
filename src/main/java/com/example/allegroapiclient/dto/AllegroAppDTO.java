package com.example.allegroapiclient.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AllegroAppDTO {
    private String clientId;

    private String clientSecret;

    @JsonProperty(value="isSandbox")
    private boolean isSandbox;

    private String username;

    private String endpoint;
}