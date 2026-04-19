/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.smartcampus.subresource;

import com.smartcampus.exception.SensorUnavailableException;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import com.smartcampus.store.DataStore;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * SensorReadingResource — sub-resource class for /api/v1/sensors/{sensorId}/readings.
 * Accessed ONLY via the sub-resource locator in SensorResource — NO @Path on this class.
 *
 * Endpoints (relative to /sensors/{sensorId}/readings):
 *   GET  / → fetch all historical readings for the sensor
 *   POST / → add a new reading (blocks MAINTENANCE sensors → 403)
 *
 *
 * @author Maurya Patel (W2112200)
 */
// NO @Path annotation here — path is declared in SensorResource's locator method
public class SensorReadingResource {

    private final String sensorId; // The sensor context — set by the locator method

    /**
     * Constructor — receives sensorId from the sub-resource locator in SensorResource.
     * This is NOT DI — it's a simple constructor call: new SensorReadingResource(sensorId).
     * @param sensorId the sensor ID from the URI path
     */
    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    /**
     * GET /api/v1/sensors/{sensorId}/readings — fetches all readings for this sensor.
     * Returns empty array [] if no readings exist yet (not an error, just empty history).
     * @return list of SensorReading objects as JSON array
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<SensorReading> getReadings() {

        // Validate that the sensor exists
        if (!DataStore.sensors.containsKey(sensorId)) {
            throw new NotFoundException("Sensor not found: " + sensorId); // Built-in 404
        }


        // getOrDefault prevents NullPointerException for sensors with no readings entry
        return DataStore.readings.getOrDefault(sensorId, new ArrayList<>());
    }

    /**
     * POST /api/v1/sensors/{sensorId}/readings — adds a new reading.
     *
     * Validates:
     * 1. Sensor must exist (404 if not)
     * 2. Sensor must NOT be in MAINTENANCE status (403 Forbidden if it is)
     *
     * Auto-generates UUID id and System.currentTimeMillis() timestamp if not provided.
     *

     *
     * @param reading SensorReading from JSON body (client sends at minimum: {"value": 23.7})
     * @return 201 Created with the complete reading object
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON) // Accept JSON body
    @Produces(MediaType.APPLICATION_JSON)
    public Response addReading(SensorReading reading) {

        // Validate sensor exists
        Sensor sensor = DataStore.sensors.get(sensorId);
        if (sensor == null) {
            throw new NotFoundException("Sensor not found: " + sensorId); // 404
        }

       
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) { // Case-insensitive check
            throw new SensorUnavailableException( // Custom exception → 403 via mapper
                    "Sensor '" + sensorId + "' is in MAINTENANCE and cannot accept new readings."); // Error message
        }

        // Auto-generate reading ID if not provided by client
        if (reading.getId() == null || reading.getId().isEmpty()) {
            reading.setId(UUID.randomUUID().toString());
        }

        // Auto-generate timestamp if not provided (0 means not set)
        if (reading.getTimestamp() == 0) {
            reading.setTimestamp(System.currentTimeMillis());
        }

        // Store the reading in the sensor's readings list
        // computeIfAbsent creates the list if it doesn't exist yet — thread-safe
        DataStore.readings.computeIfAbsent(sensorId, k -> new ArrayList<>())
                .add(reading);

        // This ensures data consistency — GET /sensors/TEMP-001 shows the latest reading value
        sensor.setCurrentValue(reading.getValue()); // Update parent sensor's currentValue

        // Return 201 Created with the complete reading (including generated id and timestamp)
        return Response.status(Response.Status.CREATED)
                .entity(reading) // Reading JSON body
                .build();
    }

}
