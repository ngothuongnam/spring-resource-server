package com.example.spring_resource_server.controller;

import com.example.spring_resource_server.dto.*;
import com.example.spring_resource_server.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

// ===== USER CONTROLLER - Cần SCOPE_read =====
@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {

    // GET /api/users - Danh sách users
    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> getUsers(@AuthenticationPrincipal Jwt jwt) {
        log.info("Client {} đang truy cập danh sách users", jwt.getClaimAsString("sub"));

        List<User> users = List.of(
                User.builder()
                        .id(1L)
                        .username("john_doe")
                        .email("john@example.com")
                        .firstName("John")
                        .lastName("Doe")
                        .role(UserRole.USER)
                        .createdAt(LocalDateTime.now().minusDays(30))
                        .build(),
                User.builder()
                        .id(2L)
                        .username("jane_smith")
                        .email("jane@example.com")
                        .firstName("Jane")
                        .lastName("Smith")
                        .role(UserRole.MODERATOR)
                        .createdAt(LocalDateTime.now().minusDays(15))
                        .build()
        );

        return ResponseEntity.ok(ApiResponse.success("Danh sách users", users));
    }

    // GET /api/users/{id} - Chi tiết user
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUser(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        log.info("Client {} đang xem user ID: {}", jwt.getClaimAsString("sub"), id);

        User user = User.builder()
                .id(id)
                .username("user_" + id)
                .email("user" + id + "@example.com")
                .firstName("User")
                .lastName("Number " + id)
                .phoneNumber("+84901234567")
                .role(UserRole.USER)
                .createdAt(LocalDateTime.now().minusDays(id * 2))
                .updatedAt(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(ApiResponse.success(user));
    }
}