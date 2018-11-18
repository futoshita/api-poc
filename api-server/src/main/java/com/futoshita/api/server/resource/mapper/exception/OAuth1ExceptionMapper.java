package com.futoshita.api.server.resource.mapper.exception;

import org.glassfish.jersey.server.oauth1.OAuth1Exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class OAuth1ExceptionMapper extends AncestorExceptionMapper implements ExceptionMapper<OAuth1Exception> {

    private final Logger LOGGER = LoggerFactory.getLogger(OAuth1ExceptionMapper.class);

    @Override
    public Response toResponse(OAuth1Exception ex) {
        LOGGER.warn(ex.getMessage(), ex);

        return buildResponse(ex, Status.UNAUTHORIZED);
    }

}
