package com.example.allegroapiclient.auth.dto;

import com.example.allegroapiclient.auth.entities.FlowTypes;
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

    private FlowTypes authFlowType;
}