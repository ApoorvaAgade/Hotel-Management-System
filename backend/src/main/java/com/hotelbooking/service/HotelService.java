package com.hotelbooking.service;

import com.hotelbooking.dto.hotel.HotelRequest;
import com.hotelbooking.dto.hotel.HotelResponse;
import com.hotelbooking.entity.Hotel;
import com.hotelbooking.exception.ResourceNotFoundException;
import com.hotelbooking.repository.HotelRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelService {

    private final HotelRepository hotelRepository;

    public HotelService(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    public HotelResponse createHotel(HotelRequest request) {
        Hotel hotel = Hotel.builder()
                .name(request.getName())
                .location(request.getLocation())
                .description(request.getDescription())
                .amenities(request.getAmenities())
                .isActive(true)
                .build();
        return toResponse(hotelRepository.save(hotel));
    }

    public HotelResponse updateHotel(Long id, HotelRequest request) {
        Hotel hotel = getActiveHotelEntity(id);
        hotel.setName(request.getName());
        hotel.setLocation(request.getLocation());
        hotel.setDescription(request.getDescription());
        hotel.setAmenities(request.getAmenities());
        return toResponse(hotelRepository.save(hotel));
    }

    public void softDeleteHotel(Long id) {
        Hotel hotel = getActiveHotelEntity(id);
        hotel.setIsActive(false);
        hotelRepository.save(hotel);
    }

    public List<HotelResponse> getAllHotels(String location, String amenity) {
        if ((location == null || location.isBlank()) && (amenity == null || amenity.isBlank())) {
            return hotelRepository.findAllByIsActiveTrueOrderByCreatedAtDesc().stream()
                    .map(this::toResponse)
                    .toList();
        }
        return hotelRepository.searchActiveHotels(normalize(location), normalize(amenity)).stream()
                .map(this::toResponse)
                .toList();
    }

    public HotelResponse getHotelById(Long id) {
        return toResponse(getActiveHotelEntity(id));
    }

    public Hotel getActiveHotelEntity(Long id) {
        return hotelRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + id));
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private HotelResponse toResponse(Hotel hotel) {
        return HotelResponse.builder()
                .id(hotel.getId())
                .name(hotel.getName())
                .location(hotel.getLocation())
                .description(hotel.getDescription())
                .amenities(hotel.getAmenities())
                .isActive(hotel.getIsActive())
                .createdAt(hotel.getCreatedAt())
                .updatedAt(hotel.getUpdatedAt())
                .build();
    }
}
