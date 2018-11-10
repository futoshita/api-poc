package com.futoshita.api.server;

import com.futoshita.api.server.entity.provider.MoxyJsonContextResolver;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import org.glassfish.jersey.oauth1.signature.OAuth1SignatureFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.mvc.jsp.JspMvcFeature;
import org.glassfish.jersey.server.oauth1.DefaultOAuth1Provider;
import org.glassfish.jersey.server.oauth1.OAuth1ServerFeature;
import org.glassfish.jersey.server.validation.ValidationConfig;
import org.glassfish.jersey.server.validation.internal.InjectingConstraintValidatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ParameterNameProvider;
import javax.validation.Validation;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.ext.ContextResolver;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class ServerApp extends ResourceConfig {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public ServerApp() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("initializing resources logging...");
        }

        register(LoggingFeature.class);

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

    /**
     * Custom configuration of validation. This configuration defines custom:
     * <ul>
     * <li>ConstraintValidationFactory - so that validators are able to inject
     * Jersey providers/resources.</li>
     * <li>ParameterNameProvider - if method input parameters are invalid, this
     * class returns actual parameter names instead of the default ones
     * ({@code arg0, arg1, ..})</li>
     * </ul>
     */
    public static class ValidationConfigurationContextResolver implements ContextResolver<ValidationConfig> {

        @Context
        private ResourceContext resourceContext;

        @Override
        public ValidationConfig getContext(final Class<?> type) {
            return new ValidationConfig()
                    .constraintValidatorFactory(resourceContext.getResource(InjectingConstraintValidatorFactory.class))
                    .parameterNameProvider(new CustomParameterNameProvider());
        }

        private class CustomParameterNameProvider implements ParameterNameProvider {

            private final ParameterNameProvider nameProvider;

            public CustomParameterNameProvider() {
                nameProvider = Validation.byDefaultProvider().configure().getDefaultParameterNameProvider();
            }

            @Override
            public List<String> getParameterNames(final Constructor<?> constructor) {
                return nameProvider.getParameterNames(constructor);
            }

            @Override
            public List<String> getParameterNames(final Method method) {
                if ("addUser".equals(method.getName())) {
                    return Arrays.asList("user");
                }
                return nameProvider.getParameterNames(method);
            }
        }
    }
}