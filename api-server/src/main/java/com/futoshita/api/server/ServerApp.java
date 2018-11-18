package com.futoshita.api.server;

import com.futoshita.api.server.entity.provider.MoxyJsonContextResolver;
import com.futoshita.api.server.entity.validation.ValidationConfigurationContextResolver;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import org.glassfish.jersey.oauth1.signature.OAuth1SignatureFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.mvc.jsp.JspMvcFeature;
import org.glassfish.jersey.server.oauth1.DefaultOAuth1Provider;
import org.glassfish.jersey.server.oauth1.OAuth1ServerFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.ws.rs.core.MultivaluedHashMap;
import java.util.logging.Level;
import java.util.logging.LogManager;

public class ServerApp extends ResourceConfig {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public ServerApp() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("initializing resources logging...");
        }

        // redirection des logs vers logback
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        register(new LoggingFeature(java.util.logging.Logger.getLogger(LoggingFeature.DEFAULT_LOGGER_NAME), Level.INFO,
                LoggingFeature.Verbosity.PAYLOAD_ANY, 21474836));

        // Enable Tracing support.
        property(ServerProperties.TRACING, "ALL");

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("registering OAuth1 server feature...");
        }

        DefaultOAuth1Provider oap = new DefaultOAuth1Provider();
        oap.registerConsumer("some-owner-id", "abcdef", "123456", new MultivaluedHashMap<String, String>());

        register(new OAuth1ServerFeature(oap, "oauth/request-token", "oauth/access-token"));
        register(OAuth1SignatureFeature.class);

        packages("com.futoshita.api.server.oauth");

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("registering webservice resources...");
        }

        packages("com.futoshita.api.server.resource");

        register(ValidationConfigurationContextResolver.class);

        register(MoxyJsonFeature.class);
        register(new MoxyJsonContextResolver());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("registering jsp pages...");
        }

        register(JspMvcFeature.class);
        property("jersey.config.server.mvc.templateBasePath.jsp", "/WEB-INF/jsp");
    }


}