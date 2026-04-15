package com.hotelbooking.service;

import com.hotelbooking.dto.room.RoomRequest;
import com.hotelbooking.dto.room.RoomResponse;
import com.hotelbooking.entity.BookingStatus;
import com.hotelbooking.entity.Hotel;
import com.hotelbooking.entity.Room;
import com.hotelbooking.entity.RoomCategory;
import com.hotelbooking.exception.ResourceNotFoundException;
import com.hotelbooking.repository.BookingRepository;
import com.hotelbooking.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final HotelService hotelService;

    public RoomService(RoomRepository roomRepository, BookingRepository bookingRepository, HotelService hotelService) {
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
        this.hotelService = hotelService;
    }

    public RoomResponse createRoom(Long hotelId, RoomRequest request) {
        Hotel hotel = hotelService.getActiveHotelEntity(hotelId);

        if (roomRepository.existsByHotelIdAndRoomNumberIgnoreCaseAndIsActiveTrue(hotelId, request.getRoomNumber())) {
            throw new IllegalArgumentException("Room number already exists for this hotel");
        }

        Room room = Room.builder()
                .hotel(hotel)
                .roomNumber(request.getRoomNumber())
                .category(request.getCategory())
                .pricePerNight(request.getPricePerNight())
                .capacity(request.getCapacity())
                .amenities(request.getAmenities())
                .isActive(true)
                .build();

        return toResponse(roomRepository.save(room), true);
    }

    public RoomResponse updateRoom(Long hotelId, Long roomId, RoomRequest request) {
        hotelService.getActiveHotelEntity(hotelId);
        Room room = getActiveRoomEntity(roomId);

        if (!room.getHotel().getId().equals(hotelId)) {
            throw new ResourceNotFoundException("Room not found in hotel with id: " + hotelId);
        }

        room.setRoomNumber(request.getRoomNumber());
        room.setCategory(request.getCategory());
        room.setPricePerNight(request.getPricePerNight());
        room.setCapacity(request.getCapacity());
        room.setAmenities(request.getAmenities());

        return toResponse(roomRepository.save(room), true);
    }

    public void softDeleteRoom(Long hotelId, Long roomId) {
        hotelService.getActiveHotelEntity(hotelId);
        Room room = getActiveRoomEntity(roomId);

        if (!room.getHotel().getId().equals(hotelId)) {
            throw new ResourceNotFoundException("Room not found in hotel with id: " + hotelId);
        }

        room.setIsActive(false);
        roomRepository.save(room);
    }

    public List<RoomResponse> getRoomsByHotel(
            Long hotelId,
            LocalDate checkIn,
            LocalDate checkOut,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String amenity,
            RoomCategory category
    ) {
        hotelService.getActiveHotelEntity(hotelId);
        boolean dateRangeProvided = checkIn != null && checkOut != null;

        return roomRepository.findByHotelIdAndIsActiveTrueOrderByCreatedAtDesc(hotelId).stream()
                .filter(room -> category == null || room.getCategory() == category)
                .filter(room -> minPrice == null || room.getPricePerNight().compareTo(minPrice) >= 0)
                .filter(room -> maxPrice == null || room.getPricePerNight().compareTo(maxPrice) <= 0)
                .filter(room -> amenity == null || amenity.isBlank() || room.getAmenities().toLowerCase().contains(amenity.toLowerCase()))
            .map(room -> Map.entry(room, isAvailable(room.getId(), checkIn, checkOut)))
            .filter(entry -> !dateRangeProvided || entry.getValue())
            .map(entry -> toResponse(entry.getKey(), entry.getValue()))
                .toList();
    }

    public RoomResponse getRoomById(Long hotelId, Long roomId, LocalDate checkIn, LocalDate checkOut) {
        hotelService.getActiveHotelEntity(hotelId);
        Room room = getActiveRoomEntity(roomId);

        if (!room.getHotel().getId().equals(hotelId)) {
            throw new ResourceNotFoundException("Room not found in hotel with id: " + hotelId);
        }

        return toResponse(room, isAvailable(roomId, checkIn, checkOut));
    }

    public List<RoomResponse> searchRooms(
            String location,
            LocalDate checkIn,
            LocalDate checkOut,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String amenity,
            RoomCategory category
    ) {
        boolean dateRangeProvided = checkIn != null && checkOut != null;

        return roomRepository.searchActiveRooms(normalize(location), minPrice, maxPrice, category).stream()
                .filter(room -> amenity == null || amenity.isBlank() || room.getAmenities().toLowerCase().contains(amenity.toLowerCase()))
            .map(room -> Map.entry(room, isAvailable(room.getId(), checkIn, checkOut)))
            .filter(entry -> !dateRangeProvided || entry.getValue())
            .map(entry -> toResponse(entry.getKey(), entry.getValue()))
                .toList();
    }

    public Room getActiveRoomEntity(Long roomId) {
        return roomRepository.findByIdAndIsActiveTrue(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + roomId));
    }

    public boolean isAvailable(Long roomId, LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) {
            return true;
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }
        return !bookingRepository.existsConflictingBooking(roomId, checkIn, checkOut, BookingStatus.CONFIRMED);
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private RoomResponse toResponse(Room room, boolean available) {
        return RoomResponse.builder()
                .id(room.getId())
                .hotelId(room.getHotel().getId())
                .hotelName(room.getHotel().getName())
                .hotelLocation(room.getHotel().getLocation())
                .roomNumber(room.getRoomNumber())
                .category(room.getCategory().name())
                .pricePerNight(room.getPricePerNight())
                .capacity(room.getCapacity())
                .amenities(room.getAmenities())
                .isActive(room.getIsActive())
                .available(available)
                .createdAt(room.getCreatedAt())
                .updatedAt(room.getUpdatedAt())
                .build();
    }
}
