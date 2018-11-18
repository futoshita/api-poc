package com.futoshita.api.server.resource.mapper.exception;

import com.futoshita.api.server.entity.ErrorRepresentation;

import javax.ws.rs.core.*;
import java.util.ArrayList;
import java.util.List;

public abstract class AncestorExceptionMapper {

    @Context
    private javax.inject.Provider<Request> request;

    protected Response buildResponse(Throwable ex, Response.Status status) {
        ErrorRepresentation error = new ErrorRepresentation();
        error.setMessage(ex.getMessage());

        List<ErrorRepresentation> errors = new ArrayList<>();
        errors.add(error);

        Response.ResponseBuilder response = Response.status(status).type(getMediaType());

        response.entity(new GenericEntity(errors, (new GenericType<List<ErrorRepresentation>>() {
        }).getType()));

        return response.build();
    }

    protected Response buildResponse(List<ErrorRepresentation> errors, Response.Status status) {
        Response.ResponseBuilder response = Response.status(status).type(getMediaType());

        response.entity(new GenericEntity(errors, (new GenericType<List<ErrorRepresentation>>() {
        }).getType()));

        return response.build();
    }

    private MediaType getMediaType() {
        List<Variant> variants = Variant.mediaTypes(new MediaType[]{MediaType.APPLICATION_XML_TYPE, MediaType.APPLICATION_JSON_TYPE}).build();
        Variant variant = request.get().selectVariant(variants);
        if (variant != null) {
            return variant.getMediaType();
        } else {
            return MediaType.APPLICATION_JSON_TYPE;
        }
    }
}
