package com.example.allegroapiclient.api_client.utils;

import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponentsBuilder;

import org.springframework.http.HttpHeaders;

import java.util.Collections;

public class APIUtils {
    public static final String requestsContentType = "application/vnd.allegro.public.v1+json";
    public static final String requestsAccept = requestsContentType;

    public static UriComponentsBuilder getBasicUriComponentsBuilder(boolean isSandbox){
        final String baseAllegroSandboxUrl = "api.allegro.pl.allegrosandbox.pl";
        final String baseAllegroUrl = "api.allegro.pl";
        String host = isSandbox ? baseAllegroSandboxUrl : baseAllegroUrl;
        return UriComponentsBuilder.newInstance()
                .scheme("https")
                .host(host);
    }

    public static UriComponentsBuilder getBasicImageUriComponentsBuilder(boolean isSandbox){
        final String baseAllegroSandboxUrl = "upload.allegro.pl.allegrosandbox.pl";
        final String baseAllegroUrl = "upload.allegro.pl";
        String host = isSandbox ? baseAllegroSandboxUrl : baseAllegroUrl;
        return UriComponentsBuilder.newInstance()
                .scheme("https")
                .host(host);
    }

    public static void setBasicContentType(HttpHeaders headers){
        headers.setContentType(MediaType.valueOf(APIUtils.requestsContentType));
        headers.setAccept(
                Collections.singletonList(MediaType.valueOf(APIUtils.requestsContentType)));
    }
}
