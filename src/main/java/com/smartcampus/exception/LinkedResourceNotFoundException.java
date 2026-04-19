/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.smartcampus.exception;

/**
 * LinkedResourceNotFoundException — thrown when POST /sensors includes
 * a roomId that does not reference an existing room in the DataStore.
 * Mapped to HTTP 422 Unprocessable Entity by LinkedResourceNotFoundExceptionMapper.
 * @author Maurya Patel (W2112200)
 */
public class LinkedResourceNotFoundException extends RuntimeException { // Unchecked exception
    public LinkedResourceNotFoundException(String message) { // Constructor with error message
        super(message); // Pass to RuntimeException for getMessage()
    }
}
