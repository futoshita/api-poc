package com.futoshita.jersey.server.resource.mapper.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.futoshita.jersey.server.entity.ErrorRepresentation;

@Provider
public class UnexpectedExceptionMapper implements ExceptionMapper<Throwable> {
  
  private final Logger LOGGER = LoggerFactory.getLogger(UnexpectedExceptionMapper.class);
  
  @Override
  public Response toResponse(Throwable ex) {
    LOGGER.error(ex.getMessage(), ex);
    
    ErrorRepresentation error = new ErrorRepresentation();
    error.setMessage(ex.getMessage());
    
    return Response.status(Status.CONFLICT).entity(error).type(MediaType.APPLICATION_JSON).build();
  }
  
}
