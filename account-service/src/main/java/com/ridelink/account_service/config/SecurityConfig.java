package com.ridelink.account_service.config;

import com.ridelink.account_service.security.JwtAuthenticationFilter;
import com.ridelink.account_service.util.JwtUtil;
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

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtil jwtUtil) {
        return new JwtAuthenticationFilter(jwtUtil);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
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

                        // Public Passenger Authentication & Registration
                        .requestMatchers(HttpMethod.POST, "/api/passengers/login", "/api/accounts/passengers/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/passengers", "/api/accounts/passengers").permitAll()

                        // Public Driver Authentication & Registration
                        .requestMatchers(HttpMethod.POST, "/api/accounts/drivers/register", "/api/drivers/accounts/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/accounts/drivers/login", "/api/drivers/accounts/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/accounts/drivers", "/api/drivers/accounts").permitAll()

                        // Passenger retrieval strictly requires authentication
                        .requestMatchers(HttpMethod.GET, "/api/passengers/**", "/api/accounts/passengers/**").authenticated()

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
