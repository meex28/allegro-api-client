package com.example.allegroapiclient;

import lombok.Data;

@Data
public class AllegroAppDTO {
    private String clientId;
    private String clientSecret;
    private boolean isSandbox;
    private String username;
    private String endpoint;
}