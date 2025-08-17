package com.example.spring_resource_server.controller;

import com.example.spring_resource_server.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
@Slf4j
public class PublicController {

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("Resource Server đang hoạt động bình thường"));
    }

    @GetMapping("/info")
    public ResponseEntity<ApiResponse<Map<String, String>>> info() {
        Map<String, String> info = Map.of(
                "service", "Spring Resource Server",
                "version", "1.0.0",
                "description", "OAuth2 Resource Server tích hợp với Authorization Server",
                "auth_server", "http://localhost:9000"
        );

        return ResponseEntity.ok(ApiResponse.success("Thông tin hệ thống", info));
    }
}
