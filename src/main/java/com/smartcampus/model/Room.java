/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Room — POJO representing a physical room on the Smart Campus.
 * Fields match spec exactly: id ("LIB-301"), name, capacity, sensorIds.
 * No-arg constructor + getters/setters required by Jackson for JSON serialisation.
 *
 * @author Maurya Patel (W2112200)
 */
public class Room {

    private String id;                                    // Unique room identifier, e.g., "LIB-301"
    private String name;                                  // readable name, e.g., "Library Quiet Study"
    private int capacity;                                 // Maximum occupancy for safety regulations
    private List<String> sensorIds = new ArrayList<>();   // IDs of sensors in this room — enables orphan check on DELETE

    /** No-arg constructor — required by Jackson for JSON deserialisation. */
    public Room() {}

    /**
     * Parameterised constructor for creating Room objects with initial data.
     * @param id       unique room identifier
     * @param name     readable room name
     * @param capacity maximum occupancy
     */
    public Room(String id, String name, int capacity) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
    }


    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public List<String> getSensorIds() { return sensorIds; }
    public void setSensorIds(List<String> sensorIds) { this.sensorIds = sensorIds; }

}
