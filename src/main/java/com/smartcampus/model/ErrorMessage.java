/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.model;

/**
 * ErrorMessage — standardized error response payload for ALL API errors.
 * Used by all 4 ExceptionMappers to return consistent JSON error bodies.
 * Clients receive predictable error structure across the entire API.
 *
 * @author Maurya Patel (W2112200)
 */
public class ErrorMessage {

    private String errorMessage;   
    private int errorCode;
    private String documentation; 

    /** No-arg constructor — required by Jackson for JSON deserialisation. */
    public ErrorMessage() {}

    /**
     * Parameterised constructor — used by all ExceptionMappers.
     * @param errorMessage  description of the error
     * @param errorCode     HTTP status code
     * @param documentation link to error docs
     */
    public ErrorMessage(String errorMessage, int errorCode, String documentation) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
        this.documentation = documentation;
    }


    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public int getErrorCode() { return errorCode; }
    public void setErrorCode(int errorCode) { this.errorCode = errorCode; }

    public String getDocumentation() { return documentation; }
    public void setDocumentation(String documentation) { this.documentation = documentation; }

}
