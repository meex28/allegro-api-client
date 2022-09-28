package com.example.allegroapiclient.utils;

import org.json.JSONObject;

import java.util.Base64;

// utils used to decode JWT tokens
public class JwtUtils {
    // decode JWT token, return String[] contains header, payload and signature
    public static String[] decode(String token){
        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();

        for(int i = 0; i<chunks.length; i++)
            chunks[i] = new String(decoder.decode(chunks[i]));

        return chunks;
    }

    public static String getHeaderAsString(String token){
        return decode(token)[0];
    }

    public static String getPayloadAsString(String token){
        return decode(token)[1];
    }

    public static JSONObject getHeaderAsJSONObject(String token){
        return new JSONObject(getHeaderAsString(token));
    }

    public static JSONObject getPayloadAsJSONObject(String token){
        return new JSONObject(getPayloadAsString(token));
    }
}
