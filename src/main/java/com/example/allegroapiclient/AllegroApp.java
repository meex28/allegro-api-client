package com.example.allegroapiclient;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table
public class AllegroApp implements Persistable<String> {
    @Id
    private String clientId;

    private String clientSecret;

    private boolean isSandbox;

    private String username;

    private String tokenForApplication;

    private String tokenForUser;

    private String refreshToken;

    private String endpoint;

    @Transient
    private boolean isNew;

    public AllegroApp() {
    }

    public AllegroApp(String clientId, String clientSecret, boolean isSandbox, String username) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.isSandbox = isSandbox;
        this.username = username;
    }

    @Override
    public String getId() {
        return clientId;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }
}
