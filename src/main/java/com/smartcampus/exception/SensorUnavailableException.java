/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.smartcampus.exception;

/**
 * SensorUnavailableException — thrown when POST /sensors/{id}/readings is attempted
 * on a sensor with status "MAINTENANCE" (physically disconnected).
 * Mapped to HTTP 403 Forbidden by SensorUnavailableExceptionMapper.
 * @author Maurya Patel (W2112200)
 */
public class SensorUnavailableException extends RuntimeException { // Unchecked exception
    public SensorUnavailableException(String message) { // Constructor with error message
        super(message); // Pass to RuntimeException for getMessage()
    }
}
