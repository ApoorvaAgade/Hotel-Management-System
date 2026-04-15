package com.hotelbooking.service;

import com.hotelbooking.dto.auth.AuthResult;
import com.hotelbooking.dto.auth.RegisterRequest;
import com.hotelbooking.dto.auth.UserResponse;
import com.hotelbooking.entity.Role;
import com.hotelbooking.entity.User;
import com.hotelbooking.exception.ResourceNotFoundException;
import com.hotelbooking.exception.UnauthorizedActionException;
import com.hotelbooking.repository.UserRepository;
import com.hotelbooking.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public UserResponse register(RegisterRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean requesterIsAdmin = authentication != null
                && authentication.isAuthenticated()
                && authentication.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        boolean adminAlreadyExists = userRepository.existsByRoleAndIsActiveTrue(Role.ADMIN);

        if (request.getRole() == Role.ADMIN && adminAlreadyExists && !requesterIsAdmin) {
            throw new UnauthorizedActionException("Only ADMIN can create ADMIN users");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .isActive(true)
                .build();

        User saved = userRepository.save(user);
        return toUserResponse(saved);
    }

    public AuthResult login(String email, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        User user = userRepository.findByEmailAndIsActiveTrue(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());

        return AuthResult.builder()
                .token(token)
                .username(user.getName())
                .role(user.getRole().name())
                .message("Login successful")
                .build();
    }

    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmailAndIsActiveTrue(email)
                .orElseThrow(() -> new ResourceNotFoundException("Active user not found"));
        return toUserResponse(user);
    }

    public List<UserResponse> getAllActiveUsers() {
        return userRepository.findAllByIsActiveTrueOrderByCreatedAtDesc().stream()
                .map(this::toUserResponse)
                .toList();
    }

    public List<UserResponse> getAllUsersForAdmin() {
        return userRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toUserResponse)
                .toList();
    }

    public UserResponse updateUserStatus(Long userId, boolean isActive, String adminEmail) {
        User admin = userRepository.findByEmailAndIsActiveTrue(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found"));

        if (admin.getRole() != Role.ADMIN) {
            throw new UnauthorizedActionException("Only ADMIN can manage users");
        }

        User target = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (target.getEmail().equalsIgnoreCase(adminEmail) && !isActive) {
            throw new IllegalArgumentException("Admin cannot deactivate own account");
        }

        target.setIsActive(isActive);
        return toUserResponse(userRepository.save(target));
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
