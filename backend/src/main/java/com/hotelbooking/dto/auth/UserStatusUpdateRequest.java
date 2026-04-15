package com.hotelbooking.dto.auth;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserStatusUpdateRequest {

    @NotNull(message = "isActive is required")
    private Boolean isActive;
}
