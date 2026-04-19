/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.smartcampus.exception;

/**
 * RoomNotEmptyException — thrown when DELETE /rooms/{id} is attempted
 * on a room that still has sensors assigned (sensorIds not empty).
 * Mapped to HTTP 409 Conflict by RoomNotEmptyExceptionMapper.
 * @author Maurya Patel (W2112200)
 */
public class RoomNotEmptyException extends RuntimeException { // Unchecked exception
    public RoomNotEmptyException(String message) { // Constructor with error message
        super(message); // Pass to RuntimeException for getMessage()
    }
}
