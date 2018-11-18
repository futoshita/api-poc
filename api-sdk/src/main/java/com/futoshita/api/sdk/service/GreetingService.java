package com.futoshita.api.sdk.service;

import com.futoshita.api.sdk.ClientApp;

import javax.ws.rs.core.MediaType;

public class GreetingService extends AncestorService {

    private final static String RESOURCE_URI = "/greeting/hello";

    public static String getHello(MediaType... mediaTypes) throws Exception {
        return get(ClientApp.getInstance().getClientAppProperties().getApiBaseUrl(), RESOURCE_URI, String.class, mediaTypes);
    }

    public static String getHelloSecured(MediaType... mediaTypes) throws Exception {
        return getSecured(ClientApp.getInstance().getClientAppProperties().getApiBaseUrl(), RESOURCE_URI + "/secured", String.class, mediaTypes);
    }
}
