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
import java.util.Map;

@RestController
@RequestMapping("/api/token")
@Slf4j
public class TokenInfoController {
    @GetMapping("/info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTokenInfo(@AuthenticationPrincipal Jwt jwt) {
        Map<String, Object> tokenInfo = Map.of(
                "client_id", jwt.getClaimAsString("client_id"),
                "subject", jwt.getSubject(),
                "scopes", jwt.getClaimAsString("scope"),
                "issued_at", jwt.getIssuedAt(),
                "expires_at", jwt.getExpiresAt(),
                "issuer", jwt.getIssuer(),
                "audience", jwt.getAudience(),
                "all_claims", jwt.getClaims()
        );

        return ResponseEntity.ok(ApiResponse.success("Thông tin JWT Token", tokenInfo));
    }
}
