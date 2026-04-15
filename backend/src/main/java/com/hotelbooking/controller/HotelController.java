package com.hotelbooking.controller;

import com.hotelbooking.dto.hotel.HotelRequest;
import com.hotelbooking.dto.hotel.HotelResponse;
import com.hotelbooking.service.HotelService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
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

import java.util.List;

@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @Operation(summary = "Browse and search hotels")
    @GetMapping
    public ResponseEntity<List<HotelResponse>> getHotels(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String amenity
    ) {
        return ResponseEntity.ok(hotelService.getAllHotels(location, amenity));
    }

    @Operation(summary = "Get hotel by id")
    @GetMapping("/{hotelId}")
    public ResponseEntity<HotelResponse> getHotelById(@PathVariable Long hotelId) {
        return ResponseEntity.ok(hotelService.getHotelById(hotelId));
    }

    @Operation(summary = "Create a hotel")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HotelResponse createHotel(@Valid @RequestBody HotelRequest request) {
        return hotelService.createHotel(request);
    }

    @Operation(summary = "Update a hotel")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{hotelId}")
    public ResponseEntity<HotelResponse> updateHotel(@PathVariable Long hotelId, @Valid @RequestBody HotelRequest request) {
        return ResponseEntity.ok(hotelService.updateHotel(hotelId, request));
    }

    @Operation(summary = "Soft delete a hotel")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{hotelId}")
    public ResponseEntity<Void> deleteHotel(@PathVariable Long hotelId) {
        hotelService.softDeleteHotel(hotelId);
        return ResponseEntity.noContent().build();
    }
}
