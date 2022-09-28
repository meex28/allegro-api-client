package com.example.allegroapiclient.exceptions;

import lombok.Getter;

public class DeviceFlowTokenPending extends RuntimeException{
    @Getter
    private final String error;

    public DeviceFlowTokenPending(String error) {
        this.error = error;
    }
}
