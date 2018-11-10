package com.futoshita.jersey.client;

import com.futoshita.jersey.client.entity.ErrorRepresentation;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.oauth1.AccessToken;
import org.glassfish.jersey.client.oauth1.ConsumerCredentials;
import org.glassfish.jersey.client.oauth1.OAuth1AuthorizationFlow;
import org.glassfish.jersey.client.oauth1.OAuth1ClientSupport;
import org.glassfish.jersey.logging.LoggingFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

public class ClientApp {

    private static Logger LOGGER = LoggerFactory.getLogger(ClientApp.class);

    private static Feature oauth1Feature;

    public ClientApp() {

    }

    private static class ClientConfigBuidler {

        private ClientConfig clientConfig;

        private ClientConfigBuidler() {
            SLF4JBridgeHandler.removeHandlersForRootLogger();
            SLF4JBridgeHandler.install();
        }

        private static class ClientConfigBuidlerHolder {
            private final static ClientConfigBuidler instance = new ClientConfigBuidler();
        }

        public static ClientConfigBuidler getInstance() {
            return ClientConfigBuidlerHolder.instance;
        }

        public ClientConfig getClientConfig() {
            if (clientConfig == null) {
                clientConfig = new ClientConfig();
                clientConfig.register(new LoggingFeature(java.util.logging.Logger.getLogger(ClientApp.class.getName()), Level.INFO, null, null));
            }

            return clientConfig;
        }
    }

    private static class SecuredClientConfigBuidler {

        private ClientConfig clientConfig;

        private SecuredClientConfigBuidler() {
            SLF4JBridgeHandler.removeHandlersForRootLogger();
            SLF4JBridgeHandler.install();
        }

        private static class SecuredClientConfigBuidlerHolder {
            private final static SecuredClientConfigBuidler instance = new SecuredClientConfigBuidler();
        }

        public static SecuredClientConfigBuidler getInstance() {
            return SecuredClientConfigBuidlerHolder.instance;
        }

        public ClientConfig getClientConfig() {
            if (clientConfig == null) {
                clientConfig = new ClientConfig();
                clientConfig.register(new LoggingFeature(java.util.logging.Logger.getLogger(ClientApp.class.getName()), Level.INFO, null, null));
            }

            clientConfig.register(oauth1Feature);

            return clientConfig;
        }
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
        Response response = ClientBuilder.newClient(ClientConfigBuidler.getInstance().getClientConfig()).target(uri).request().get();

        checkStatus(response, 200);

        return response.readEntity(clazz);
    }

    public <T> T getSecured(String uri, Class<T> clazz) throws Exception {
        Response response = ClientBuilder.newClient(SecuredClientConfigBuidler.getInstance().getClientConfig()).target(uri).request().get();

        checkStatus(response, 200);

        return response.readEntity(clazz);
    }

    public <T> void post(String uri, T entity, MediaType mediaType) throws Exception {
        Response response = ClientBuilder.newClient(ClientConfigBuidler.getInstance().getClientConfig()).target(uri).request().accept(mediaType).post(Entity.entity(entity, mediaType));

        checkStatus(response, 200);
    }

    private void checkStatus(Response response, int expectedStatus) throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("response mediatype: {}", response.getMediaType().toString());
        }

        if (response.getStatus() != expectedStatus) {
            String errorMessage = null;

            if (response.hasEntity()) {
                try {
                    if (response.getMediaType().equals(MediaType.APPLICATION_JSON_TYPE) || response.getMediaType().equals(MediaType.APPLICATION_XML_TYPE)) {
                        List<ErrorRepresentation> errors = response.readEntity(new GenericType<List<ErrorRepresentation>>() {

                        });
                        errorMessage = errors.get(0).getMessage();
                    } else {
                        errorMessage = response.readEntity(String.class);
                    }
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
