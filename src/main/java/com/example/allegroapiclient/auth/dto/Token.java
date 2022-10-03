package com.example.allegroapiclient.auth.dto;

public record Token(String token, boolean isSandbox, String hostUrl) {
    public String toStringJSON() {
        return "{" +
                "token:'" + token + '\'' +
                ", isSandbox:" + isSandbox +
                ", hostUrl:'" + hostUrl + '\'' +
                '}';
    }
}
