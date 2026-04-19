/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.smartcampus.store;

import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DataStore — central in-memory data store for the Smart Campus API.
 *
 * Uses ConcurrentHashMap instead of HashMap because JAX-RS has a per-request lifecycle:
 * Jersey creates a NEW resource instance for every HTTP request, meaning multiple threads
 * access this shared data simultaneously. ConcurrentHashMap provides thread-safe read/write
 * operations through internal segment-level locking — preventing data corruption and race
 *
 * No database is used — the spec explicitly forbids SQL/H2/SQLite.
 * Pre-loaded with sample data so the API has content on first run.
 *
 * @author Maurya Patel (W2112200)
 */
public class DataStore {


    public static final ConcurrentHashMap<String, Room> rooms = new ConcurrentHashMap<>();


    public static final ConcurrentHashMap<String, Sensor> sensors = new ConcurrentHashMap<>();


    // Each sensor ID maps to a List of its historical SensorReadings
    public static final ConcurrentHashMap<String, List<SensorReading>> readings = new ConcurrentHashMap<>(); // Thread-safe readings

    // Static initialiser block — runs once when the class is first loaded by the JVM
    // Pre-populates sample data so the API is not empty on first deployment
    static {


        Room r1 = new Room("LIB-301", "Library Quiet Study", 50);
        rooms.put(r1.getId(), r1);

        Room r2 = new Room("LAB-101", "Computer Lab", 30);
        rooms.put(r2.getId(), r2);


        Sensor s1 = new Sensor("TEMP-001", "Temperature", "ACTIVE", 22.5, "LIB-301");
        sensors.put(s1.getId(), s1);
        r1.getSensorIds().add(s1.getId());            // Link sensor to room — CRITICAL for 409 orphan check
        readings.put(s1.getId(), new ArrayList<>());


        Sensor s2 = new Sensor("CO2-001", "CO2", "MAINTENANCE", 450.0, "LAB-101");
        sensors.put(s2.getId(), s2);
        r2.getSensorIds().add(s2.getId());
        readings.put(s2.getId(), new ArrayList<>());

    }

}
