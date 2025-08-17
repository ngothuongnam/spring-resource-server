package com.example.spring_resource_server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class ResourceServerSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())

                // Cấu hình OAuth2 Resource Server với JWT
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                // Lấy public key từ Authorization Server của bạn (port 9000)
                                .jwkSetUri("http://localhost:9000/oauth2/jwks")
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                )

                // Phân quyền theo scope từ Authorization Server
                .authorizeHttpRequests(authz -> authz
                        // Public endpoints
                        .requestMatchers("/api/public/**", "/actuator/**").permitAll()

                        // User endpoints - cần scope 'read'
                        .requestMatchers("GET", "/api/users/**").hasAuthority("SCOPE_read")

                        // Post endpoints
                        .requestMatchers("GET", "/api/posts/**").hasAuthority("SCOPE_read")
                        .requestMatchers("POST", "/api/posts/**").hasAuthority("SCOPE_write")
                        .requestMatchers("PUT", "/api/posts/**").hasAuthority("SCOPE_write")
                        .requestMatchers("DELETE", "/api/posts/**").hasAuthority("SCOPE_write")

                        // Product endpoints (mới)
                        .requestMatchers("GET", "/api/products/**").hasAuthority("SCOPE_read")
                        .requestMatchers("POST", "/api/products/**").hasAuthority("SCOPE_write")
                        .requestMatchers("PUT", "/api/products/**").hasAuthority("SCOPE_write")
                        .requestMatchers("DELETE", "/api/products/**").hasAuthority("SCOPE_write")

                        // Admin endpoints - cần scope admin (nếu có)
                        .requestMatchers("/api/admin/**").hasAuthority("SCOPE_admin")

                        // Token info endpoint
                        .requestMatchers("/api/token/**").authenticated()

                        // Tất cả request khác cần authenticated
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        // Chuyển đổi JWT scopes thành Spring Security authorities
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthorityPrefix("SCOPE_");
        authoritiesConverter.setAuthoritiesClaimName("scope");

        JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
        authenticationConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);

        return authenticationConverter;
    }
}