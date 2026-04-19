/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.model;

/**
 * SensorReading — POJO representing a single measurement data point from a sensor.
 * Fields: id (UUID), timestamp (epoch ms), value (the measurement).
 * id and timestamp are auto-generated server-side on POST; client only sends "value".
 *
 * @author Maurya Patel (W2112200)
 */
public class SensorReading {

    private String id;      // Unique reading event ID — UUID generated server-side
    private long timestamp; // Epoch time in milliseconds when reading was captured
    private double value;   // The actual metric value recorded by sensor hardware

    /** No-arg constructor — required by Jackson for JSON deserialisation. */
    public SensorReading() {}

    /**
     * Parameterised constructor.
     * @param id        UUID string
     * @param timestamp epoch milliseconds
     * @param value     measurement value
     */
    public SensorReading(String id, long timestamp, double value) {
        this.id = id;
        this.timestamp = timestamp;
        this.value = value;
    }


    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }

}
