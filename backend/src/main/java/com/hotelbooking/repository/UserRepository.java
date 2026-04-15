package com.hotelbooking.repository;

import com.hotelbooking.entity.Role;
import com.hotelbooking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndIsActiveTrue(String email);
    List<User> findAllByIsActiveTrueOrderByCreatedAtDesc();
    List<User> findAllByOrderByCreatedAtDesc();
    boolean existsByEmail(String email);
    boolean existsByRoleAndIsActiveTrue(Role role);
}
