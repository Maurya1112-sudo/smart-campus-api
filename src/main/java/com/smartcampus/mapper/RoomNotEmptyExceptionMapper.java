/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.smartcampus.mapper;

import com.smartcampus.exception.RoomNotEmptyException;
import com.smartcampus.model.ErrorMessage;

import javax.ws.rs.core.*;
import javax.ws.rs.ext.*;

/**
 * RoomNotEmptyExceptionMapper — converts RoomNotEmptyException to HTTP 409 Conflict.
 * Returns a JSON ErrorMessage body with errorMessage, errorCode, and documentation link.
 * @Provider annotation is CRITICAL — without it Jersey will NOT detect this mapper.
 * @author Maurya Patel (W2112200)
 */
@Provider // CRITICAL: Tells Jersey to register this as a global exception mapper
public class RoomNotEmptyExceptionMapper
        implements ExceptionMapper<RoomNotEmptyException> { // Catches RoomNotEmptyException

    /**
     * Converts RoomNotEmptyException to a 409 Conflict HTTP response.
     * @param e the exception thrown by RoomResource.deleteRoom()
     * @return HTTP 409 Conflict with JSON ErrorMessage body
     */
    @Override // Implement the ExceptionMapper interface method
    public Response toResponse(RoomNotEmptyException e) {
       
        ErrorMessage error = new ErrorMessage(
                e.getMessage(),
                409,
                "https://api.smartcampus.ac.uk/docs/errors#409"); // Documentation link

        return Response.status(Response.Status.CONFLICT)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
