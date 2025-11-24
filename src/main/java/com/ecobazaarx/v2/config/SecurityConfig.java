package com.ecobazaarx.v2.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth

                        // --- 1. CORE PUBLIC & OPTIONS ACCESS ---
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/leaderboard/**").permitAll()

                        // --- 3. CORE AUTHENTICATED ACCESS ---
                        .requestMatchers("/api/v1/profile/**").authenticated()
                        .requestMatchers("/api/v1/seller/**").authenticated()
                        .requestMatchers("/api/v1/cart/**").hasRole("CUSTOMER")
                        .requestMatchers("/api/v1/checkout/**").hasRole("CUSTOMER")
                        .requestMatchers("/api/v1/insights/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/insights/seller/**").hasRole("SELLER")
                        .requestMatchers("/api/v1/insights/profile/**").hasRole("CUSTOMER")
                        .requestMatchers("/api/v1/tracking/**").hasRole("CUSTOMER")
                        .requestMatchers("/api/v1/recommendations/me/**").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/seller/orders").hasRole("SELLER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/recommendations/**").permitAll()

                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/admin/categories",
                                "/api/v1/admin/materials",
                                "/api/v1/admin/manufacturing-processes",
                                "/api/v1/admin/packaging-materials",
                                "/api/v1/admin/transport-zones"
                        ).hasAnyRole("ADMIN", "SELLER") // <--- Allows Sellers to read these 5 lists
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        // --- 5. FALLBACK ---
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // --- CORS Configuration (Unchanged) ---
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "http://10.179.87.175:5173/"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}