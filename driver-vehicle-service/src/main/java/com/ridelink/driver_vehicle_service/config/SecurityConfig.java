package com.ridelink.driver_vehicle_service.config;

import com.ridelink.driver_vehicle_service.security.JwtAuthenticationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public OpenAPI / Swagger Documentation & Actuator
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/actuator/**"
                        ).permitAll()

                        // POST, PATCH, and PUT requests to /api/drivers/** strictly require 'DRIVER' authority/role
                        .requestMatchers(HttpMethod.POST, "/api/drivers/**").hasAnyAuthority("ROLE_DRIVER", "DRIVER")
                        .requestMatchers(HttpMethod.PATCH, "/api/drivers/**").hasAnyAuthority("ROLE_DRIVER", "DRIVER")
                        .requestMatchers(HttpMethod.PUT, "/api/drivers/**").hasAnyAuthority("ROLE_DRIVER", "DRIVER")

                        // GET requests (e.g., /api/drivers/available or /api/drivers/eligible) require 'PASSENGER' or 'DRIVER' authority
                        .requestMatchers(HttpMethod.GET, "/api/drivers/**").hasAnyAuthority("ROLE_PASSENGER", "PASSENGER", "ROLE_DRIVER", "DRIVER")

                        // DELETE requests to /api/drivers/** require 'DRIVER' authority
                        .requestMatchers(HttpMethod.DELETE, "/api/drivers/**").hasAnyAuthority("ROLE_DRIVER", "DRIVER")

                        // Any other request must be authenticated
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration(JwtAuthenticationFilter filter) {
        FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
