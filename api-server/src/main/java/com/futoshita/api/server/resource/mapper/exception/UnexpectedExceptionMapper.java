package com.futoshita.api.server.resource.mapper.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UnexpectedExceptionMapper extends AncestorExceptionMapper implements ExceptionMapper<Throwable> {

    private final Logger LOGGER = LoggerFactory.getLogger(UnexpectedExceptionMapper.class);

    @Override
    public Response toResponse(Throwable ex) {
        LOGGER.error(ex.getMessage());

        return buildResponse(ex, Status.INTERNAL_SERVER_ERROR);
    }

}
