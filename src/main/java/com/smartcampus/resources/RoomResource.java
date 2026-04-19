/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.smartcampus.resources;

import com.smartcampus.exception.RoomNotEmptyException;
import com.smartcampus.model.Room;
import com.smartcampus.store.DataStore;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * RoomResource — manages CRUD operations for /api/v1/rooms.
 *
 * Endpoints:
 *   GET    /rooms          → list all rooms (full objects, not just IDs)
 *   POST   /rooms          → create a room (201 Created + Location header)
 *   GET    /rooms/{roomId} → get specific room (200 or 404)
 *   DELETE /rooms/{roomId} → delete room (204, 404, or 409 if has sensors)
 *
 * @author Maurya Patel (W2112200)
 */
@Path("/rooms") // All methods in this class handle paths under /api/v1/rooms
public class RoomResource {

    /**
     * GET /api/v1/rooms — returns all rooms as full objects (not just IDs).
     * Returning full objects avoids the N+1 problem — clients get all data in one request.
     * @return list of all Room objects as JSON array
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Room> getAllRooms() {
        // Create a new ArrayList from the ConcurrentHashMap values — snapshot of current data
        return new ArrayList<>(DataStore.rooms.values());
    }

    /**
     * POST /api/v1/rooms — creates a new room.
     * Client sends JSON with id, name, capacity. Server returns 201 Created + Location header.
     * @param room    Room object from JSON request body
     * @param uriInfo injected by JAX-RS for building the Location URI
     * @return 201 Created with Location header and room entity
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON) // Accept JSON input only — 415 if client sends wrong Content-Type
    @Produces(MediaType.APPLICATION_JSON)
    public Response createRoom(Room room, @Context UriInfo uriInfo) {
        // Store the room in the DataStore keyed by its ID
        DataStore.rooms.put(room.getId(), room);

        // Build Location URI pointing to the newly created room (REST best practice)
        URI location = uriInfo.getAbsolutePathBuilder()
                .path(room.getId())
                .build();

        // Return 201 Created with Location header and room as response body
        return Response.created(location).entity(room).build(); // 201 + Location + JSON body
    }

    /**
     * GET /api/v1/rooms/{roomId} — retrieves a specific room by ID.
     * @param roomId path parameter extracted from the URI
     * @return 200 OK with room, or 404 Not Found
     */
    @GET
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoom(@PathParam("roomId") String roomId) {
        Room room = DataStore.rooms.get(roomId);
        if (room == null) {
            throw new NotFoundException("Room not found: " + roomId); // JAX-RS built-in 404 exception
        }
        return Response.ok(room).build(); // 200 OK with room JSON
    }

    /**
     * DELETE /api/v1/rooms/{roomId} — deletes a room.
     * Business rule (Part 2.2): blocks deletion if room still has sensors → 409 Conflict.
     * If room not found → 404. If room has no sensors → 204 No Content (deleted).
     * @param roomId path parameter
     * @return 204 (deleted), 404 (not found), or 409 (has sensors, via exception mapper)
     */
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = DataStore.rooms.get(roomId);
        if (room == null) {
            throw new NotFoundException("Room not found: " + roomId); // 404 — idempotent behaviour
        }

        
        // This prevents "data orphans" — sensors would lose their parent room reference
        if (!room.getSensorIds().isEmpty()) { // Room has sensors — block deletion
            throw new RoomNotEmptyException( // Custom exception → mapped to 409 by RoomNotEmptyExceptionMapper
                    "Room '" + roomId + "' cannot be deleted — it still has "
                    + room.getSensorIds().size() + " sensor(s) assigned to it."); // Include sensor count
        }

        // Room exists and has no sensors — safe to delete
        DataStore.rooms.remove(roomId); // Remove from ConcurrentHashMap
        return Response.noContent().build(); // 204 No Content — standard REST response for successful DELETE
    }

}
