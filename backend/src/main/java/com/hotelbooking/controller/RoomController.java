package com.hotelbooking.controller;

import com.hotelbooking.dto.room.RoomRequest;
import com.hotelbooking.dto.room.RoomResponse;
import com.hotelbooking.entity.RoomCategory;
import com.hotelbooking.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @Operation(summary = "Get rooms by hotel with availability")
    @GetMapping("/hotels/{hotelId}/rooms")
    public ResponseEntity<List<RoomResponse>> getRoomsByHotel(
            @PathVariable Long hotelId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String amenity,
            @RequestParam(required = false) RoomCategory category
    ) {
        return ResponseEntity.ok(roomService.getRoomsByHotel(hotelId, checkIn, checkOut, minPrice, maxPrice, amenity, category));
    }

    @Operation(summary = "Get room details by id")
    @GetMapping("/hotels/{hotelId}/rooms/{roomId}")
    public ResponseEntity<RoomResponse> getRoomById(
            @PathVariable Long hotelId,
            @PathVariable Long roomId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut
    ) {
        return ResponseEntity.ok(roomService.getRoomById(hotelId, roomId, checkIn, checkOut));
    }

    @Operation(summary = "Search rooms globally")
    @GetMapping("/rooms/search")
    public ResponseEntity<List<RoomResponse>> searchRooms(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String amenity,
            @RequestParam(required = false) RoomCategory category
    ) {
        return ResponseEntity.ok(roomService.searchRooms(location, checkIn, checkOut, minPrice, maxPrice, amenity, category));
    }

    @Operation(summary = "Create a room in a hotel")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/hotels/{hotelId}/rooms")
    @ResponseStatus(HttpStatus.CREATED)
    public RoomResponse createRoom(@PathVariable Long hotelId, @Valid @RequestBody RoomRequest request) {
        return roomService.createRoom(hotelId, request);
    }

    @Operation(summary = "Update a room in a hotel")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/hotels/{hotelId}/rooms/{roomId}")
    public ResponseEntity<RoomResponse> updateRoom(
            @PathVariable Long hotelId,
            @PathVariable Long roomId,
            @Valid @RequestBody RoomRequest request
    ) {
        return ResponseEntity.ok(roomService.updateRoom(hotelId, roomId, request));
    }

    @Operation(summary = "Soft delete a room")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/hotels/{hotelId}/rooms/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long hotelId, @PathVariable Long roomId) {
        roomService.softDeleteRoom(hotelId, roomId);
        return ResponseEntity.noContent().build();
    }
}
