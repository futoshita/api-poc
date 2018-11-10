package com.futoshita.jersey.client;

import com.futoshita.jersey.client.entity.ErrorRepresentation;
import org.glassfish.jersey.client.oauth1.AccessToken;
import org.glassfish.jersey.client.oauth1.ConsumerCredentials;
import org.glassfish.jersey.client.oauth1.OAuth1AuthorizationFlow;
import org.glassfish.jersey.client.oauth1.OAuth1ClientSupport;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class ClientApp {

    private Feature oauth1Feature;

    public ClientApp() {

    }

    public void initOAuth1Feature(String confFilePath) throws Exception {
        Properties properties = new Properties();
        properties.load(new FileInputStream(new File(confFilePath)));

        ConsumerCredentials consumerCredentials = new ConsumerCredentials(properties.getProperty("consumer.key"),
                properties.getProperty("consumer.secret"));

        String tokenKey = properties.getProperty("token.key");
        String tokenSecret = properties.getProperty("token.secret");

        if (tokenKey == null || tokenKey.isEmpty()) {
            OAuth1AuthorizationFlow oauth1Dance = OAuth1ClientSupport.builder(consumerCredentials)
                    .authorizationFlow(properties.getProperty("oauth1.uri.request-token"),
                            properties.getProperty("oauth1.uri.access-token"), properties.getProperty("oauth1.uri.authorization"))
                    .build();
            String authorizationUri = oauth1Dance.start();

            String verifier = get(authorizationUri, String.class);

            AccessToken accessToken = oauth1Dance.finish(verifier);

            oauth1Feature = oauth1Dance.getOAuth1Feature();
        } else {
            AccessToken accessToken = new AccessToken(tokenKey, tokenSecret);
            oauth1Feature = OAuth1ClientSupport.builder(consumerCredentials).feature().accessToken(accessToken).build();
        }
    }

    public <T> T get(String uri, Class<T> clazz) throws Exception {
        Client client = ClientBuilder.newBuilder().build();
        Response response = client.target(uri).request().get();

        checkStatus(response, 200);

        return response.readEntity(clazz);
    }

    public <T> T getSecured(String uri, Class<T> clazz) throws Exception {
        Client client = ClientBuilder.newBuilder().register(oauth1Feature).build();
        Response response = client.target(uri).request().get();

        checkStatus(response, 200);

        return response.readEntity(clazz);
    }

    public <T> void post(String uri, T entity, MediaType mediaType) throws Exception {
        Client client = ClientBuilder.newBuilder().build();
        Response response = client.target(uri).request().accept(mediaType).post(Entity.entity(entity, mediaType));

        checkStatus(response, 200);
    }

    private void checkStatus(Response response, int expectedStatus) throws Exception {
        if (response.getStatus() != expectedStatus) {
            String errorMessage = null;

            if (response.hasEntity()) {
                try {
                    ErrorRepresentation error = response.readEntity(ErrorRepresentation.class);
                    errorMessage = error.getMessage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // TODO
            }

            throw new Exception("status code: " + response.getStatus() + ", reason: "
                    + response.getStatusInfo().getReasonPhrase() + ", cause: " + errorMessage);
        }
    }

}
