package com.hotelbooking.dto.hotel;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class HotelResponse {
    private Long id;
    private String name;
    private String location;
    private String description;
    private String amenities;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
