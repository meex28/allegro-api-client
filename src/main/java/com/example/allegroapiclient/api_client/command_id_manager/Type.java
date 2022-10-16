package com.example.allegroapiclient.api_client.command_id_manager;

public enum Type{
    PUBLICATION("offer-publication"),
    SINGLE_PRICE("change-price"),
    PRICE("offer-price-change"),
    QUANTITY("offer-quantity-change"),
    MODIFICATION("offer-modification");

    public final String url;

    private Type(String url){
        this.url = url;
    }
}
