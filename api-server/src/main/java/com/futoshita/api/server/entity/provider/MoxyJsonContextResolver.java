package com.futoshita.api.server.entity.provider;

import org.eclipse.persistence.jaxb.BeanValidationMode;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.glassfish.jersey.moxy.json.MoxyJsonConfig;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

@Provider
public class MoxyJsonContextResolver implements ContextResolver<MoxyJsonConfig> {

    private final MoxyJsonConfig config;

    public MoxyJsonContextResolver() {
        config = new MoxyJsonConfig()//.setIncludeRoot(true)
                .setFormattedOutput(true)
                // Turn off BV otherwise the entities on server would be validated by MOXy as well.
                .property(MarshallerProperties.BEAN_VALIDATION_MODE, BeanValidationMode.NONE);
    }

    @Override
    public MoxyJsonConfig getContext(Class<?> objectType) {
        return config;
    }

}