package com.camoutech.multivendor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Configuration de sécurité Spring Security
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(CorsConfigurationSource corsConfigurationSource) {
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                // Endpoints publics (sans authentification)
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/health/**").permitAll()
                .requestMatchers("/api/categories/**").permitAll()
                .requestMatchers("/api/products/**").permitAll()
                .requestMatchers("/error").permitAll()
                // Tous les autres endpoints nécessitent une authentification
                .anyRequest().authenticated()
            );

        return http.build();
    }

    // PasswordEncoder est déjà défini dans AppConfig
}
