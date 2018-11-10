package com.futoshita.api.server.resource.filter;
//package com.futoshita.jersey.server.resources.filter;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.lang.reflect.InvocationTargetException;
//import java.util.List;
//
//import javax.inject.Inject;
//import javax.ws.rs.container.ContainerRequestContext;
//import javax.ws.rs.container.ContainerRequestFilter;
//import javax.ws.rs.container.ResourceInfo;
//import javax.ws.rs.core.Context;
//import javax.ws.rs.core.MediaType;
//import javax.xml.bind.JAXBContext;
//import javax.xml.bind.JAXBException;
//import javax.xml.bind.Unmarshaller;
//import javax.xml.transform.stream.StreamSource;
//
//import org.eclipse.persistence.jaxb.MarshallerProperties;
//import org.glassfish.jersey.message.internal.ReaderWriter;
//import org.glassfish.jersey.server.ExtendedUriInfo;
//import org.glassfish.jersey.uri.UriTemplate;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public class BadParameterFilter implements ContainerRequestFilter {
//  
//  private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
//  
//  @Context
//  ExtendedUriInfo extendedUriInfo;
//  @Context
//  ResourceInfo resourceInfo;
//  
//  @Inject
//  public BadParameterFilter() {
//    
//  }
//  
//  @Override
//  public void filter(ContainerRequestContext requestContext) throws IOException {
//    if (("POST".equals(requestContext.getMethod()) || "PUT".equals(requestContext.getMethod()))
//        && resourceInfo.getResourceMethod().isAnnotationPresent(EntityToCheck.class)) {
//      String path = "";
//      for (UriTemplate template : extendedUriInfo.getMatchedTemplates()) {
//        path = template.getTemplate() + path;
//      }
//      
//      if (LOGGER.isDebugEnabled()) {
//        LOGGER.debug("HTTP request: {} {}", requestContext.getMethod(), path);
//      }
//      
//      String entityClassName = resourceInfo.getResourceMethod().getAnnotation(EntityToCheck.class).name();
//      Class<?> entityClass = null;
//      try {
//        entityClass = Class.forName(entityClassName);
//      } catch (ClassNotFoundException e) {
//        LOGGER.error(e.getMessage(), e);
//        throw new RuntimeException(e);
//      }
//      
//      Object entity = null;
//      try {
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        InputStream in = requestContext.getEntityStream();
//        ReaderWriter.writeTo(in, out);
//        
//        byte[] requestBytes = out.toByteArray();
//        
//        StreamSource streamSource = new StreamSource(new ByteArrayInputStream(requestBytes));
//        
//        JAXBContext jc = JAXBContext.newInstance(entityClass);
//        
//        if (LOGGER.isDebugEnabled()) {
//          LOGGER.debug("JAXBContext: {}", jc);
//        }
//        
//        Unmarshaller unmarshaller = jc.createUnmarshaller();
//        if (MediaType.APPLICATION_JSON_TYPE.equals(requestContext.getMediaType())) {
//          unmarshaller.setProperty(MarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
//        }
//        
//        if (streamSource != null) {
//          entity = unmarshaller.unmarshal(streamSource);
//        }
//        
//        requestContext.setEntityStream(new ByteArrayInputStream(requestBytes));
//      } catch (IOException | JAXBException e) {
//        LOGGER.error(e.getMessage(), e);
//        throw new RuntimeException(e);
//      }
//      
//      List<String> missingData = null;
//      
//      try {
//        missingData = EntityUtil.checkMissingData(entity);
//      } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
//          | InvocationTargetException e) {
//        LOGGER.error(e.getMessage(), e);
//      }
//      
//      String message = "error while executing method " + requestContext.getMethod() + " " + path;
//      
//      if (null == missingData) {
//        String reason = "missing parameter(s): business entity is null";
//        LOGGER.error(message, reason);
//      } else if (!missingData.isEmpty()) {
//        String reason = "missing parameter(s): " + missingData.toString();
//        LOGGER.error(message, reason);
//      }
//    }
//  }
//  
//}
