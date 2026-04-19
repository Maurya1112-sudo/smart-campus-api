/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.smartcampus.mapper;

import com.smartcampus.exception.SensorUnavailableException;
import com.smartcampus.model.ErrorMessage;

import javax.ws.rs.core.*;
import javax.ws.rs.ext.*;

/**
 * SensorUnavailableExceptionMapper — converts SensorUnavailableException to HTTP 403 Forbidden.
 * 403 means the server understood the request but refuses to authorise it —
 * the sensor's MAINTENANCE state is the constraint, not the client's credentials.
 * @author Maurya Patel (W2112200)
 */
@Provider // CRITICAL: Register as global exception mapper
public class SensorUnavailableExceptionMapper
        implements ExceptionMapper<SensorUnavailableException> { // Catches SensorUnavailableException

    /**
     * Converts SensorUnavailableException to a 403 Forbidden HTTP response.
     * @param e the exception thrown by SensorReadingResource.addReading()
     * @return HTTP 403 Forbidden with JSON ErrorMessage body
     */
    @Override // Implement the ExceptionMapper interface method
    public Response toResponse(SensorUnavailableException e) { // Convert exception to response
        ErrorMessage error = new ErrorMessage(
                e.getMessage(),
                403,
                "https://api.smartcampus.ac.uk/docs/errors#403");

        return Response.status(Response.Status.FORBIDDEN)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
