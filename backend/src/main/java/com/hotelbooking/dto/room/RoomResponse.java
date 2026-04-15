package com.hotelbooking.dto.room;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class RoomResponse {
    private Long id;
    private Long hotelId;
    private String hotelName;
    private String hotelLocation;
    private String roomNumber;
    private String category;
    private BigDecimal pricePerNight;
    private Integer capacity;
    private String amenities;
    private Boolean isActive;
    private Boolean available;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
