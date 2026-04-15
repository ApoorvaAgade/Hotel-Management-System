package com.hotelbooking.dto.hotel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class HotelRequest {

    @NotBlank(message = "Hotel name is required")
    @Size(max = 150, message = "Hotel name must not exceed 150 characters")
    private String name;

    @NotBlank(message = "Location is required")
    @Size(max = 200, message = "Location must not exceed 200 characters")
    private String location;

    @NotBlank(message = "Description is required")
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @NotBlank(message = "Amenities are required")
    @Size(max = 1000, message = "Amenities must not exceed 1000 characters")
    private String amenities;
}
