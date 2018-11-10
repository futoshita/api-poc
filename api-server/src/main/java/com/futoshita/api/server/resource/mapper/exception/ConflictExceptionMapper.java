package com.futoshita.api.server.resource.mapper.exception;

import com.futoshita.api.server.service.exception.NonUniqueException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ConflictExceptionMapper extends AncestorExceptionMapper implements ExceptionMapper<NonUniqueException> {

    private final Logger LOGGER = LoggerFactory.getLogger(ConflictExceptionMapper.class);

    @Override
    public Response toResponse(NonUniqueException ex) {
        LOGGER.error(ex.getMessage());

        return buildResponse(ex, Status.CONFLICT);
    }

}
