/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.smartcampus.filter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * LoggingFilter — logs every incoming request and outgoing response.
 * Implements BOTH ContainerRequestFilter AND ContainerResponseFilter in one class.
 *
 * Using filters for logging is superior to manual Logger.info() in every method because:
 * 1. DRY — logging logic defined once, not duplicated in every resource method
 * 2. Guaranteed coverage — runs on EVERY request, even errors and 404s
 * 3. Separation of concerns — resource methods focus on business logic
 * 4. Easy modification — change log format in one place
 *
 * @author Maurya Patel (W2112200)
 */
@Provider 
public class LoggingFilter
        implements ContainerRequestFilter, ContainerResponseFilter { // Implements BOTH interfaces

    // java.util.logging.Logger instance — outputs to Tomcat/NetBeans console
    private static final Logger LOGGER =
            Logger.getLogger(LoggingFilter.class.getName());

    /**
     * Runs BEFORE the request reaches any resource method.
     * Logs the HTTP method (GET, POST, DELETE) and the full request URI.
     * @param requestContext contains all incoming request details
     * @throws IOException required by the interface
     */
    @Override // Implement ContainerRequestFilter.filter()
    public void filter(ContainerRequestContext requestContext) throws IOException {
        LOGGER.info("--- Incoming Request ---");
        LOGGER.info("Method : " + requestContext.getMethod());
        LOGGER.info("URI    : " + requestContext.getUriInfo().getAbsolutePath());
    }

    /**
     * Runs AFTER the response leaves the resource method (even after exceptions!).
     * Logs the HTTP status code (200, 201, 404, 409, etc.).
     * @param requestContext  the original request context (available for correlation)
     * @param responseContext contains all outgoing response details
     * @throws IOException required by the interface
     */
    @Override // Implement ContainerResponseFilter.filter()
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {
        LOGGER.info("--- Outgoing Response ---");
        LOGGER.info("Status : " + responseContext.getStatus());
    }

}
