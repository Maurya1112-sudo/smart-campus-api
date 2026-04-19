/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.smartcampus.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.util.HashMap;
import java.util.Map;

/**
 * DiscoveryResource — root API endpoint at GET /api/v1.
 * Returns JSON metadata: name, version, contact, description, and resource links.
 * Resource links follow the HATEOAS principle — clients discover all endpoints from here.
 *
 *
 * @author Maurya Patel (W2112200)
 */
@Path("/")
public class DiscoveryResource {

    /**
     * GET /api/v1 — returns API metadata and HATEOAS navigation links.
     * @return Map serialised to JSON by Jackson
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> discover() { // Returns a Map — Jackson converts to JSON object


        Map<String, Object> info = new HashMap<>();
        info.put("name", "Smart Campus Sensor & Room Management API");
        info.put("version", "1.0");
        info.put("contact", "Hamed Hamzeh - Module Leader, University of Westminster");
        info.put("student", "Maurya Patel (W2112200)");
        info.put("description", "RESTful API for managing campus rooms and IoT sensors");

        // HATEOAS links — clients navigate the API from this single entry point
        Map<String, String> resourceLinks = new HashMap<>();
        resourceLinks.put("rooms", "/api/v1/rooms");
        resourceLinks.put("sensors", "/api/v1/sensors");
        info.put("resources", resourceLinks);

        return info; // Jackson serialises this Map to JSON — returned as 200 OK

    }

}
