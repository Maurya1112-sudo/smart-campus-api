package com.smartcampus;

import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * SmartCampusApp — the JAX-RS Application configuration class.
 *
 * Extends ResourceConfig (which itself extends javax.ws.rs.core.Application)
 * to bootstrap the REST API. The packages() method scans all sub-packages of
 * "com.smartcampus" for classes annotated with @Path, @Provider, etc.
 * This automatically registers all resources, exception mappers, and filters.
 *
 * @ApplicationPath("/api/v1") sets the versioned base URI for the entire API.
 * Combined with Tomcat context path: http://localhost:8080/smart-campus-api/api/v1
 *
 * @author Maurya Patel (W2112200)
 */
@ApplicationPath("/api/v1") // Base URI prefix — all endpoints start with /api/v1
public class SmartCampusApp extends ResourceConfig {

    /**
     * Constructor — configures Jersey to scan all sub-packages for annotated classes.
     * packages() finds all @Path resources, @Provider mappers, and @Provider filters
     * under com.smartcampus.* automatically — no need to register each class manually.
     */
    public SmartCampusApp() {

        // This discovers: DiscoveryResource, RoomResource, SensorResource,
        //   all 4 ExceptionMappers, and LoggingFilter automatically
        packages("com.smartcampus");
    }

}
