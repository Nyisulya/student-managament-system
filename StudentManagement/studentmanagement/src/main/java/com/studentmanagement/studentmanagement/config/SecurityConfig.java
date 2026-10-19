package com.studentmanagement.studentmanagement.config;

import com.studentmanagement.studentmanagement.security.CustomUserDetailsService;
import com.studentmanagement.studentmanagement.security.JwtAuthenticationFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            CustomUserDetailsService userDetailsService,
            JwtAuthenticationFilter jwtAuthenticationFilter) {

        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .sessionManagement(session ->
                    session.sessionCreationPolicy(
                            SessionCreationPolicy.STATELESS
                    )
            )

            .authorizeHttpRequests(auth -> auth

                .requestMatchers(
                        "/api/auth/register",
                        "/api/auth/login",
                        "/api/auth/refresh",
                        "/api/auth/logout"
                ).permitAll()

                        // View students
                        .requestMatchers(
                                org.springframework.http.HttpMethod.GET,
                                "/api/students/**")
                        .hasAnyRole(
                                "ADMIN",
                                "TEACHER",
                                "STUDENT")

                        // Create students
                        .requestMatchers(
                                org.springframework.http.HttpMethod.POST,
                                "/api/students",
                                "/api/students/**")
                        .hasAnyRole(
                                "ADMIN",
                                "TEACHER")

                        // Update students
                        .requestMatchers(
                                org.springframework.http.HttpMethod.PUT,
                                "/api/students/**")
                        .hasAnyRole(
                                "ADMIN",
                                "TEACHER")

                        // Delete students
                        .requestMatchers(
                                org.springframework.http.HttpMethod.DELETE,
                                "/api/students/**")
                        .hasRole("ADMIN")

                         // Manage users - ADMIN only
                        .requestMatchers("/api/users/**")
                        .hasRole("ADMIN")

                .anyRequest().authenticated()
            )

            .addFilterBefore(
                    jwtAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}