package com.course.exceptions;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
public class CustomExceptionMapper implements ExceptionMapper<ServiceException> {

    private static final Logger LOGGER = Logger.getLogger(CustomExceptionMapper.class);


    @Override
    public Response toResponse(ServiceException e) {
        LOGGER.error("Error occurred: " + e.getMessage(), e);
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(e.getMessage())
                .build();
    }
}
