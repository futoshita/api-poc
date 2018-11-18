package com.futoshita.api.server.resource.mapper.exception;

import com.futoshita.api.server.oauth.exception.OAuth1UnauthorizedException;
import com.futoshita.api.server.service.exception.NonUniqueException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class OAuth1UnauthorizedExceptionMapper extends AncestorExceptionMapper implements ExceptionMapper<OAuth1UnauthorizedException> {

    private final Logger LOGGER = LoggerFactory.getLogger(OAuth1UnauthorizedExceptionMapper.class);

    @Override
    public Response toResponse(OAuth1UnauthorizedException ex) {
        LOGGER.warn(ex.getMessage(), ex);

        return buildResponse(ex, Status.UNAUTHORIZED);
    }

}
