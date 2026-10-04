package com.ridelink.ridelinkmanagementservice.config;

import com.ridelink.ridelinkmanagementservice.security.CustomAccessDeniedHandler;
import com.ridelink.ridelinkmanagementservice.security.CustomAuthenticationEntryPoint;
import com.ridelink.ridelinkmanagementservice.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .authorizeHttpRequests(auth -> auth
                        // Public Documentation & Health Endpoints
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/actuator/**"
                        ).permitAll()

                        // Role-based authorization for /api/rides
                        // Only PASSENGER can create rides
                        .requestMatchers(HttpMethod.POST, "/api/rides", "/api/rides/").hasAnyAuthority("ROLE_PASSENGER", "PASSENGER")

                        // Only DRIVER can accept, start, complete, and assign rides
                        .requestMatchers(HttpMethod.PUT, "/api/rides/*/accept").hasAnyAuthority("ROLE_DRIVER", "DRIVER")
                        .requestMatchers(HttpMethod.PUT, "/api/rides/*/start").hasAnyAuthority("ROLE_DRIVER", "DRIVER")
                        .requestMatchers(HttpMethod.PUT, "/api/rides/*/complete").hasAnyAuthority("ROLE_DRIVER", "DRIVER")
                        .requestMatchers(HttpMethod.PUT, "/api/rides/*/assign").hasAnyAuthority("ROLE_DRIVER", "DRIVER")

                        // Passenger ride history and driver stats endpoints (require JWT authentication)
                        .requestMatchers(HttpMethod.GET, "/api/rides/passenger/*/history", "/api/rides/passenger/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/rides/driver/*/stats", "/api/rides/driver/**").authenticated()

                        // All other /api/rides endpoints require authentication (no permitAll)
                        .requestMatchers("/api/rides/**").authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
