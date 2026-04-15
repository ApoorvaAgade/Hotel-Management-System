package com.hotelbooking.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {

    private static final String EMAIL_REGEX = "^(?=.{1,254}$)(?=.{1,64}@)[A-Za-z0-9][A-Za-z0-9._-]*@[A-Za-z0-9-]+\\.(?:com|in)$";

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 254, message = "Email must not exceed 254 characters")
    @Pattern(regexp = EMAIL_REGEX, message = "Email must be in valid format (example@domain.com) with a valid domain")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}
