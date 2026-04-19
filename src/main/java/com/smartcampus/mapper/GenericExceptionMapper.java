/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.smartcampus.mapper;

import com.smartcampus.model.ErrorMessage;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Logger;

/**
 * GenericExceptionMapper — catch-all safety net for ANY unhandled exception.
 *
 * Implements ExceptionMapper<Throwable> — catches everything at the top of the hierarchy.
 * JAX-RS uses "closest match" — specific mappers (409, 422, 403) fire first;
 * this only catches exceptions no other mapper handles.
 *
 * CRITICAL FIX: If the exception is a WebApplicationException (e.g., NotFoundException -> 404),
 * we pass through its OWN status code wrapped in our standard ErrorMessage JSON format.
 * Without this fix, GET /rooms/FAKE-999 would return 500 instead of 404 because this mapper
 * would override the 404 with a generic 500.
 *
 * Only truly unexpected errors (NullPointerException, etc.) return 500.
 *

 * @author Maurya Patel (W2112200)
 */
@Provider // CRITICAL: Register as global catch-all exception mapper
public class GenericExceptionMapper implements ExceptionMapper<Throwable> { // Catches ALL throwables

    // java.util.logging.Logger — spec requires this, NOT SLF4J or Log4j
    private static final Logger LOGGER =
            Logger.getLogger(GenericExceptionMapper.class.getName());

    /**
     * Converts any unhandled Throwable to an appropriate HTTP response.
     * WebApplicationExceptions keep their own status code (404, 405, etc.).
     * All other exceptions become a safe 500 with no stack trace.
     * @param e the unhandled Throwable
     * @return HTTP response with JSON ErrorMessage body
     */
    @Override // Implement the ExceptionMapper interface method
    public Response toResponse(Throwable e) {

        // CRITICAL: If it's already a WebApplicationException (NotFoundException -> 404,
        // ForbiddenException -> 403, etc.), pass through its OWN status code.
        // Do NOT override with 500 — that was the original bug.
        if (e instanceof WebApplicationException) { // Check if JAX-RS already assigned a status
            WebApplicationException wae = (WebApplicationException) e; // Cast to access getResponse()
            Response original = wae.getResponse(); // Get the original response with correct status

            // Wrap in our standard ErrorMessage format for consistent JSON across ALL errors
            ErrorMessage error = new ErrorMessage(
                    e.getMessage() != null ? e.getMessage() : "Resource not found.", // Use exception message or default
                    original.getStatus(), // Preserve the original status code (404, 405, etc.)
                    "https://api.smartcampus.ac.uk/docs/errors#" + original.getStatus());

            return Response.status(original.getStatus())
                    .entity(error)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        // All other truly unexpected errors -> safe generic 500, never expose stack trace
        LOGGER.severe("INTERNAL ERROR: " + e.getClass().getName() + " - " + e.getMessage());

        // Build safe generic error — no stack trace, no internal details
        ErrorMessage error = new ErrorMessage(
                "An unexpected internal error occurred. Please contact support.", 
                500,
                "https://api.smartcampus.ac.uk/docs/errors#500");

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(error) // Safe ErrorMessage body
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
