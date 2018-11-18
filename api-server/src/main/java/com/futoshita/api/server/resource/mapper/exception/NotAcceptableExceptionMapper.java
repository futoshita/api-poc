package com.futoshita.api.server.resource.mapper.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NotAcceptableExceptionMapper extends AncestorExceptionMapper implements ExceptionMapper<NotAcceptableException> {

    private final Logger LOGGER = LoggerFactory.getLogger(NotAcceptableExceptionMapper.class);

    @Context
    private HttpHeaders headers;

    @Override
    public Response toResponse(NotAcceptableException ex) {
        LOGGER.warn(ex.getMessage(), ex);

        Exception e = new Exception("the requested resource is not available for content types: " + headers.getAcceptableMediaTypes().toString() + ".");

        return buildResponse(e, Status.NOT_ACCEPTABLE);
    }

}
