package com.futoshita.jersey.server.oauth;

import java.lang.reflect.Method;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class AccessTokenFilterDynamicFeature implements DynamicFeature {
  
  private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
  
  @Override
  public void configure(ResourceInfo resourceInfo, FeatureContext context) {
    final String resourcePackage = resourceInfo.getResourceClass().getPackage().getName();
    final Method resourceMethod = resourceInfo.getResourceMethod();
    
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("resourcePackage: {}", resourcePackage);
      LOGGER.debug("AccessTokenRequired: {}", resourceMethod.isAnnotationPresent(AccessTokenRequired.class));
    }
    
    if ("com.futoshita.jersey.server.resources.api".equals(resourcePackage)
        && resourceMethod.isAnnotationPresent(AccessTokenRequired.class)) {
      context.register(AccessTokenFilter.class);
    }
  }
}
