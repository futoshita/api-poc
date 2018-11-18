package com.futoshita.api.sdk;

import org.glassfish.jersey.client.oauth1.AccessToken;
import org.glassfish.jersey.client.oauth1.ConsumerCredentials;
import org.glassfish.jersey.client.oauth1.OAuth1AuthorizationFlow;
import org.glassfish.jersey.client.oauth1.OAuth1ClientSupport;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ResourceBundle;

public class OAuth1Client {
    private static final BufferedReader IN = new BufferedReader(new InputStreamReader(System.in, Charset.forName("UTF-8")));
    private static final String SECURED_RESOURCE_URI = "http://localhost:8080/api-server/api/greeting/hello/secured";

    public static void main(final String[] args) throws IOException {
        ResourceBundle rb = ResourceBundle.getBundle("app");

        String requestTokenUri = rb.getString("oauth1.uri.request-token");
        String accessTokenUri = rb.getString("oauth1.uri.access-token");
        String authorizeUri = rb.getString("oauth1.uri.authorization");

        String tokenKey = rb.getString("token.key");
        String tokenSecret = rb.getString("token.secret");

        final ConsumerCredentials consumerCredentials = new ConsumerCredentials(rb.getString("consumer.key"),
                rb.getString("consumer.secret"));

        final Feature filterFeature;
        if (tokenKey == null || tokenKey.isEmpty()) {
            final OAuth1AuthorizationFlow authFlow = OAuth1ClientSupport.builder(consumerCredentials)
                    .authorizationFlow(requestTokenUri,
                            accessTokenUri,
                            authorizeUri)
                    .build();
            final String authorizationUri = authFlow.start();

            System.out.println("Enter the following URI into a web browser and authorize me:");
            System.out.println(authorizationUri);
            System.out.print("Enter the authorization code: ");
            final String verifier;
            try {
                verifier = IN.readLine();
            } catch (final IOException ex) {
                throw new RuntimeException(ex);
            }
            final AccessToken accessToken = authFlow.finish(verifier);

            tokenKey = accessToken.getToken();
            tokenSecret = accessToken.getAccessTokenSecret();

            filterFeature = authFlow.getOAuth1Feature();
        } else {
            final AccessToken accessToken = new AccessToken(tokenKey, tokenSecret);
            filterFeature = OAuth1ClientSupport.builder(consumerCredentials).feature().accessToken(accessToken).build();
        }

        final Client client = ClientBuilder.newBuilder().register(filterFeature).build();

        final Response response = client.target(SECURED_RESOURCE_URI).request().get();
        if (response.getStatus() != 200) {
            String errorEntity = null;
            if (response.hasEntity()) {
                errorEntity = response.readEntity(String.class);
            }
            throw new RuntimeException("Request to api-server was not successful. Response code: " + response.getStatus()
                    + ", reason: " + response.getStatusInfo().getReasonPhrase() + ", entity: " + errorEntity);
        }

        String greeting = response.readEntity(String.class);

        System.out.println(greeting);
    }

}