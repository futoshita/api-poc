package com.futoshita.api.sdk;

import com.futoshita.api.sdk.entity.ClientAppProperties;
import com.futoshita.api.sdk.service.AncestorService;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.oauth1.AccessToken;
import org.glassfish.jersey.client.oauth1.ConsumerCredentials;
import org.glassfish.jersey.client.oauth1.OAuth1AuthorizationFlow;
import org.glassfish.jersey.client.oauth1.OAuth1ClientSupport;
import org.glassfish.jersey.logging.LoggingFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.ws.rs.core.Feature;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;

public class ClientApp extends AncestorService {

    private static Logger LOGGER = LoggerFactory.getLogger(ClientApp.class);

    private ClientAppProperties clientAppProperties;
    private static Feature oauth1Feature;

    private ClientApp() {
    }

    private static class ClientAppHolder {
        private final static ClientApp instance = new ClientApp();
    }

    public static ClientApp getInstance() {
        return ClientAppHolder.instance;
    }

    public void loadProperties(String filePath) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(new File(filePath)));

        clientAppProperties = new ClientAppProperties();
        clientAppProperties.setApiBaseUrl(properties.getProperty("api.base.url"));
        clientAppProperties.setAccessTokenKey(properties.getProperty("access-token.key"));
        clientAppProperties.setAccessTokenSecret(properties.getProperty("access-token.secret"));
        clientAppProperties.setConsumerKey(properties.getProperty("consumer.key"));
        clientAppProperties.setConsumerSecret(properties.getProperty("consumer.secret"));
        clientAppProperties.setLocale(properties.getProperty("locale"));
        clientAppProperties.setOauth1AuthorizationUri(properties.getProperty("oauth1.uri.authorization"));
        clientAppProperties.setOauth1AccessTokenUri(properties.getProperty("oauth1.uri.access-token"));
        clientAppProperties.setOauth1RequestTokenUri(properties.getProperty("oauth1.uri.request-token"));
    }

    public ClientAppProperties getClientAppProperties() {
        return clientAppProperties;
    }

    public void setClientAppProperties(ClientAppProperties properties) {
        this.clientAppProperties = properties;
    }

    public void initOAuth1Feature() throws Exception {
        ConsumerCredentials consumerCredentials = new ConsumerCredentials(clientAppProperties.getConsumerKey(),
                clientAppProperties.getConsumerSecret());

        String tokenKey = clientAppProperties.getAccessTokenKey();
        String tokenSecret = clientAppProperties.getAccessTokenSecret();

        if (tokenKey == null || tokenKey.isEmpty()) {
            String requestTokenUrl = clientAppProperties.getOauth1RequestTokenUri();
            String accessTokenUrl = clientAppProperties.getOauth1AccessTokenUri();
            String authorizationUrl = clientAppProperties.getOauth1AuthorizationUri();

            OAuth1AuthorizationFlow oauth1Dance = OAuth1ClientSupport.builder(consumerCredentials)
                    .authorizationFlow(requestTokenUrl, accessTokenUrl, authorizationUrl)
                    .build();
            String authorizationUri = oauth1Dance.start();

            String verifier = get(authorizationUri, null, String.class);

            AccessToken accessToken = oauth1Dance.finish(verifier);
            clientAppProperties.setAccessTokenKey(accessToken.getToken());
            clientAppProperties.setAccessTokenSecret(accessToken.getAccessTokenSecret());

            oauth1Feature = oauth1Dance.getOAuth1Feature();
        } else {
            AccessToken accessToken = new AccessToken(tokenKey, tokenSecret);
            oauth1Feature = OAuth1ClientSupport.builder(consumerCredentials).feature().accessToken(accessToken).build();
        }
    }

    public static class ClientConfigBuidler {

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

    public static class SecuredClientConfigBuidler {

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

                clientConfig.register(oauth1Feature);
            }

            return clientConfig;
        }

        public void resetClientConfig() {
            clientConfig = null;
        }
    }
}
