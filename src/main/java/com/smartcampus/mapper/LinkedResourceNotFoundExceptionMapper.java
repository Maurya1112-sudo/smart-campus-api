/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.smartcampus.mapper;

import com.smartcampus.exception.LinkedResourceNotFoundException;
import com.smartcampus.model.ErrorMessage;

import javax.ws.rs.core.*;
import javax.ws.rs.ext.*;

/**
 * LinkedResourceNotFoundExceptionMapper — converts LinkedResourceNotFoundException to HTTP 422.
 * 422 Unprocessable Entity is more semantically accurate than 404 because:
 * - 404 = the URL endpoint itself was not found (wrong path)
 * - 422 = the JSON body was valid but contained a semantic error (invalid roomId reference)
 * @author Maurya Patel (W2112200)
 */
@Provider // CRITICAL: Register as global exception mapper
public class LinkedResourceNotFoundExceptionMapper
        implements ExceptionMapper<LinkedResourceNotFoundException> { // Catches LinkedResourceNotFoundException

    /**
     * Converts LinkedResourceNotFoundException to a 422 HTTP response.
     * @param e the exception thrown by SensorResource.createSensor()
     * @return HTTP 422 Unprocessable Entity with JSON ErrorMessage body
     */
    @Override // Implement the ExceptionMapper interface method
    public Response toResponse(LinkedResourceNotFoundException e) { // Convert exception to response
        ErrorMessage error = new ErrorMessage(
                e.getMessage(),
                422,
                "https://api.smartcampus.ac.uk/docs/errors#422");

        return Response.status(422)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}

