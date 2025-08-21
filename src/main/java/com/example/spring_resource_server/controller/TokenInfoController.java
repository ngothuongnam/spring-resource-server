package com.example.spring_resource_server.controller;

import com.example.spring_resource_server.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/token")
@Slf4j
public class TokenInfoController {
    @GetMapping("/info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTokenInfo(@AuthenticationPrincipal Jwt jwt) {
        Map<String, Object> tokenInfo = Map.of(
                "sub", jwt.getClaimAsString("sub"),
                "aud", jwt.getClaimAsString("aud"),
                "nbf", jwt.getClaimAsString("nbf"),
                "scope", jwt.getClaimAsString("scope"),
                "iss", jwt.getClaimAsString("iss"),
                "exp", jwt.getClaimAsString("exp"),
                "iat", jwt.getClaimAsString("iat"),
                "jti", jwt.getClaimAsString("jti")
        );

        return ResponseEntity.ok(ApiResponse.success("Thông tin JWT Token", tokenInfo));
    }
}
