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
@RequestMapping("/api/posts")
@Slf4j
public class PostController {

    // GET /api/posts - Danh sách bài viết (SCOPE_read)
    @GetMapping
    public ResponseEntity<ApiResponse<List<Post>>> getPosts(@AuthenticationPrincipal Jwt jwt) {
        log.info("Client {} với scopes: {} đang xem posts",
                jwt.getClaimAsString("sub"),
                jwt.getClaimAsString("scope"));

        List<Post> posts = List.of(
                Post.builder()
                        .id(1L)
                        .title("Giới thiệu về OAuth2")
                        .content("OAuth2 là một framework authorization...")
                        .status(PostStatus.PUBLISHED)
                        .authorId(1L)
                        .authorName("John Doe")
                        .createdAt(LocalDateTime.now().minusDays(5))
                        .build(),
                Post.builder()
                        .id(2L)
                        .title("Spring Security Resource Server")
                        .content("Hướng dẫn tạo Resource Server...")
                        .status(PostStatus.PUBLISHED)
                        .authorId(2L)
                        .authorName("Jane Smith")
                        .createdAt(LocalDateTime.now().minusDays(2))
                        .build()
        );

        return ResponseEntity.ok(ApiResponse.success("Danh sách bài viết", posts));
    }

    // GET /api/posts/{id} - Chi tiết bài viết (SCOPE_read)
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Post>> getPost(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        Post post = Post.builder()
                .id(id)
                .title("Bài viết số " + id)
                .content("Nội dung chi tiết của bài viết số " + id)
                .status(PostStatus.PUBLISHED)
                .authorId(1L)
                .authorName("Admin User")
                .createdAt(LocalDateTime.now().minusDays(id))
                .updatedAt(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(ApiResponse.success(post));
    }

    // POST /api/posts - Tạo bài viết mới (SCOPE_write)
    @PostMapping
    public ResponseEntity<ApiResponse<Post>> createPost(@RequestBody CreatePostRequest request,
                                                        @AuthenticationPrincipal Jwt jwt) {
        log.info("Client {} đang tạo bài viết mới: {}",
                jwt.getClaimAsString("sub"), request.getTitle());

        Post newPost = Post.builder()
                .id(999L)
                .title(request.getTitle())
                .content(request.getContent())
                .status(PostStatus.DRAFT)
                .authorId(1L)
                .authorName(jwt.getClaimAsString("sub"))
                .createdAt(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(ApiResponse.success("Tạo bài viết thành công", newPost));
    }

    // PUT /api/posts/{id} - Cập nhật bài viết (SCOPE_write)
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Post>> updatePost(@PathVariable Long id,
                                                        @RequestBody UpdatePostRequest request,
                                                        @AuthenticationPrincipal Jwt jwt) {
        log.info("Client {} đang cập nhật bài viết ID: {}",
                jwt.getClaimAsString("sub"), id);

        Post updatedPost = Post.builder()
                .id(id)
                .title(request.getTitle())
                .content(request.getContent())
                .status(PostStatus.valueOf(request.getStatus().toUpperCase()))
                .authorId(1L)
                .updatedAt(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(ApiResponse.success("Cập nhật thành công", updatedPost));
    }

    // DELETE /api/posts/{id} - Xóa bài viết (SCOPE_write)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deletePost(@PathVariable Long id,
                                                          @AuthenticationPrincipal Jwt jwt) {
        log.info("Client {} đang xóa bài viết ID: {}",
                jwt.getClaimAsString("sub"), id);

        return ResponseEntity.ok(ApiResponse.success("Đã xóa bài viết ID: " + id));
    }
}
