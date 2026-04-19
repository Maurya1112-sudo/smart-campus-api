/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.model;

/**
 * Sensor — POJO representing an IoT sensor deployed on the Smart Campus.
 * Fields match spec: id ("TEMP-001"), type, status, currentValue, roomId.
 * status can be "ACTIVE", "MAINTENANCE", or "OFFLINE".

 *
 * @author Maurya Patel (W2112200)
 */
public class Sensor {

    private String id;            // Unique sensor identifier, e.g., "TEMP-001"
    private String type;          // Category: "Temperature", "CO2", "Occupancy"
    private String status;        // Operational state: "ACTIVE", "MAINTENANCE", "OFFLINE"
    private double currentValue;  // Most recent measurement — updated on POST reading (Part 4.2 side-effect)
    private String roomId;        // Foreign key to parent Room — validated on POST sensor (Part 3.1)

    /** No-arg constructor — required by Jackson for JSON deserialisation. */
    public Sensor() {}

    /**
     * Parameterised constructor.
     * @param id           unique sensor identifier
     * @param type         sensor category
     * @param status       operational state
     * @param currentValue initial measurement value
     * @param roomId       ID of the room where sensor is deployed
     */
    public Sensor(String id, String type, String status, double currentValue, String roomId) {
        this.id = id;
        this.type = type;
        this.status = status;
        this.currentValue = currentValue;
        this.roomId = roomId;
    }


    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getCurrentValue() { return currentValue; }
    public void setCurrentValue(double currentValue) { this.currentValue = currentValue; }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

}
