package com.futoshita.jersey.server.oauth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.Method;

@Provider
public class AccessTokenFilterDynamicFeature implements DynamicFeature {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        final String resourcePackageName = resourceInfo.getResourceClass().getPackage().getName();
        final String resourceClassName = resourceInfo.getResourceClass().getName();
        final Method resourceMethodName = resourceInfo.getResourceMethod();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("resourcePackage: {}, resourceClass: {}, resourceMehtod: {}", resourcePackageName, resourceClassName,
                    resourceMethodName);
            LOGGER.debug("AccessTokenRequired: {}", resourceMethodName.isAnnotationPresent(AccessTokenRequired.class));
        }

        if ("com.futoshita.jersey.server.resource.api".equals(resourcePackageName)
                && resourceMethodName.isAnnotationPresent(AccessTokenRequired.class)) {
            context.register(AccessTokenFilter.class);
        }
    }
}
