package com.futoshita.api.server.resource.mapper.exception;

import com.futoshita.api.server.entity.ErrorRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ElementKind;
import javax.validation.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Provider
public class ConstraintViolationExceptionMapper extends AncestorExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    private final Logger LOGGER = LoggerFactory.getLogger(ConstraintViolationExceptionMapper.class);

    @Override
    public Response toResponse(ConstraintViolationException ex) {
        List<ErrorRepresentation> errors = constraintViolationToValidationErrors(ex);

        if (LOGGER.isWarnEnabled()) {
            for (ErrorRepresentation error : errors) {
                LOGGER.warn(error.getMessage());
            }
        }

        return buildResponse(errors, getResponseStatus(ex));
    }

    public static List<ErrorRepresentation> constraintViolationToValidationErrors(ConstraintViolationException violation) {
        return (List) violation.getConstraintViolations().stream().map((violation1) -> {
            return new ErrorRepresentation(violation1.getMessage(), violation1.getMessageTemplate());
        }).collect(Collectors.toList());
    }

    private Response.Status getResponseStatus(ConstraintViolationException violation) {
        Iterator<ConstraintViolation<?>> iterator = violation.getConstraintViolations().iterator();
        if (iterator.hasNext()) {
            ConstraintViolation cv = ((ConstraintViolation) iterator.next());

            String constraintAnnotation = cv.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName();
            Path path = cv.getPropertyPath();

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("constraint annotation: {}", constraintAnnotation);
                LOGGER.debug("constraint violation path: {}", path.toString());
            }

            if (constraintAnnotation.matches("^Unique.*$")) {
                return Response.Status.CONFLICT;
            }

            Iterator var2 = path.iterator();

            while (var2.hasNext()) {
                Path.Node node = (Path.Node) var2.next();

                ElementKind kind = node.getKind();
                if (ElementKind.RETURN_VALUE.equals(kind)) {
                    return Response.Status.NOT_FOUND;
                }
            }
        }

        return Response.Status.BAD_REQUEST;
    }
}
