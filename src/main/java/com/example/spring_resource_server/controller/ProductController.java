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

@RestController
@RequestMapping("/api/products")
@Slf4j
public class ProductController {

    @GetMapping
    public ResponseEntity<ApiResponse<List<Product>>> getProducts(@AuthenticationPrincipal Jwt jwt) {
        List<Product> products = List.of(
                Product.builder()
                        .id(1L)
                        .name("iPhone 15")
                        .description("Apple iPhone 15 128GB")
                        .price(999.99)
                        .stock(50)
                        .category("Electronics")
                        .status(ProductStatus.AVAILABLE)
                        .createdAt(LocalDateTime.now().minusDays(10))
                        .build(),
                Product.builder()
                        .id(2L)
                        .name("Samsung Galaxy S24")
                        .description("Samsung Galaxy S24 256GB")
                        .price(899.99)
                        .stock(30)
                        .category("Electronics")
                        .status(ProductStatus.AVAILABLE)
                        .createdAt(LocalDateTime.now().minusDays(8))
                        .build()
        );

        return ResponseEntity.ok(ApiResponse.success("Danh sách sản phẩm", products));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Product>> createProduct(@RequestBody CreateProductRequest request,
                                                              @AuthenticationPrincipal Jwt jwt) {
        log.info("Client {} đang tạo sản phẩm mới: {}",
                jwt.getClaimAsString("client_id"), request.getName());

        Product newProduct = Product.builder()
                .id(999L)
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .category(request.getCategory())
                .status(ProductStatus.AVAILABLE)
                .createdAt(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(ApiResponse.success("Tạo sản phẩm thành công", newProduct));
    }
}
