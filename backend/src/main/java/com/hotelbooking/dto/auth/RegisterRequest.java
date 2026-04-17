package com.hotelbooking.dto.auth;

import com.hotelbooking.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    private static final String EMAIL_REGEX = "^(?=.{1,254}$)(?=.{1,64}@)[A-Za-z0-9][A-Za-z0-9._-]*@[A-Za-z0-9-]+\\.(?:com|in)$";
    private static final String STRONG_PASSWORD_REGEX = "^(?=\\S{8,100}$)(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%&*!]).*$";

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 254, message = "Email must not exceed 254 characters")
    @Pattern(regexp = EMAIL_REGEX, message = "Email must be in valid format (example@domain.com) with a valid domain")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password length must be between 8 and 100 characters")
    @Pattern(
            regexp = STRONG_PASSWORD_REGEX,
            message = "Password must contain uppercase, lowercase, number, special character (@#$%&*!) and no spaces"
        )
    private String password;

    @NotNull(message = "Role is required")
    private Role role;
}
