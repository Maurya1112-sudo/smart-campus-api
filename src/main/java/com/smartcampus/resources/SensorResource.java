/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.smartcampus.resources;

import com.smartcampus.exception.LinkedResourceNotFoundException;
import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.store.DataStore;
import com.smartcampus.subresource.SensorReadingResource;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SensorResource — manages /api/v1/sensors endpoints.
 *
 * Endpoints:
 *   GET  /sensors              -> list all sensors (optional ?type= filter)
 *   POST /sensors              -> register sensor (validates roomId -> 422 if not found)
 *   GET  /sensors/{sensorId}   -> get a specific sensor by ID (200 or 404)
 *   Sub-resource locator       -> delegates /sensors/{sensorId}/readings to SensorReadingResource
 *
 * @author Maurya Patel (W2112200)
 */
@Path("/sensors") // All methods handle paths under /api/v1/sensors
public class SensorResource {

    /**
     * GET /api/v1/sensors — returns all sensors, optionally filtered by type.
     * If ?type=CO2 is provided, filters case-insensitively (CO2, co2, Co2 all match).
     * If no type parameter, returns all sensors.
     *
     * @param type optional sensor type filter from query string
     * @return list of matching sensors as JSON array
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Sensor> getSensors(
            @QueryParam("type") @DefaultValue("") String type) { // Optional type filter


        List<Sensor> all = new ArrayList<>(DataStore.sensors.values());

        // If type filter is provided, filter using Java streams
        if (!type.isEmpty()) {
            return all.stream()
                    .filter(s -> s.getType().equalsIgnoreCase(type))
                    .collect(Collectors.toList());
        }

        return all;
    }

    /**
     * POST /api/v1/sensors — registers a new sensor.
     * Validates that the roomId in the request body references an existing room.
     * If roomId is invalid -> throws LinkedResourceNotFoundException -> 422.
     * On success, links the sensor to the room by adding its ID to room.getSensorIds().
     *
     * @param sensor  Sensor object from JSON request body
     * @param uriInfo injected for Location header
     * @return 201 Created with Location header, or 422 via exception mapper
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON) // Accept JSON only — 415 if wrong Content-Type
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSensor(Sensor sensor, @Context UriInfo uriInfo) {

        // CRITICAL validation: roomId must reference an existing room 
        Room room = DataStore.rooms.get(sensor.getRoomId()); // Look up the referenced room
        if (room == null) {
            throw new LinkedResourceNotFoundException( // Custom exception -> 422 via mapper
                    "Room with ID '" + sensor.getRoomId() + "' does not exist."); // Descriptive error
        }

        // Store the sensor in the DataStore
        DataStore.sensors.put(sensor.getId(), sensor);

        // Link sensor to room — add sensor ID to the room's sensorIds list
        // CRITICAL for the DELETE /rooms orphan check 
        room.getSensorIds().add(sensor.getId()); // Bidirectional link: Room <-> Sensor

        // Initialise an empty readings list for the new sensor
        DataStore.readings.put(sensor.getId(), new ArrayList<>());

        // Build Location URI for the newly created sensor
        URI location = uriInfo.getAbsolutePathBuilder()
                .path(sensor.getId())
                .build();

        // Return 201 Created
        return Response.created(location).entity(sensor).build(); // 201 + Location + sensor JSON
    }

    /**
     * GET /api/v1/sensors/{sensorId} — retrieves a specific sensor by ID.
     * Returns the full Sensor object as JSON if found, or 404 if the ID does not exist.
     * The 404 NotFoundException is caught by GenericExceptionMapper and returned as
     * a standard ErrorMessage JSON body — consistent with all other 404 responses in the API.
     *
     * Also used to verify the currentValue side-effect after POST /readings:
     * after posting a reading, the sensor's currentValue should reflect the latest value.
     *
     * @param sensorId path parameter extracted from the URI
     * @return 200 OK with sensor JSON, or 404 Not Found via exception mapper
     */
    @GET
    @Path("/{sensorId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensor(
            @PathParam("sensorId") String sensorId) {

        Sensor sensor = DataStore.sensors.get(sensorId);

        if (sensor == null) {
            throw new NotFoundException("Sensor not found: " + sensorId); // JAX-RS built-in 404 exception
        }

        return Response.ok(sensor).build(); // 200 OK with full sensor JSON body
    }

    /**
     * Sub-resource locator for /api/v1/sensors/{sensorId}/readings.
     *
     * A sub-resource locator only declares @Path and returns an instance of the sub-resource class.
     * Jersey dispatches the actual HTTP method (GET/POST) to methods in SensorReadingResource.
     *

     *
     * @param sensorId sensor ID from the URI path
     * @return new SensorReadingResource instance scoped to this sensor
     */
    @Path("/{sensorId}/readings") // Sub-path — NO HTTP verb annotation here!
    // NO @GET or @POST — this is a LOCATOR method, not a handler
    public SensorReadingResource getReadingResource(
            @PathParam("sensorId") String sensorId) {
        // Return a new instance of the sub-resource class with the sensor context
        return new SensorReadingResource(sensorId); // Jersey dispatches GET/POST to this instance
    }

}
