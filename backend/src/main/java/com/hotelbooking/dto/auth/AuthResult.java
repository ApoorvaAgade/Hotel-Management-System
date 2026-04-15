package com.hotelbooking.dto.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResult {
    private String token;
    private String username;
    private String role;
    private String message;
}
