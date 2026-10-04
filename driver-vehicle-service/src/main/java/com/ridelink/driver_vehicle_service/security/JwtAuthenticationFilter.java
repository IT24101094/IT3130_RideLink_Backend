package com.ridelink.driver_vehicle_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final SecurityContextRepository securityContextRepository = new RequestAttributeSecurityContextRepository();

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7).trim();

        try {
            if (jwtUtil.validateToken(jwt)) {
                String role = jwtUtil.extractRole(jwt);
                String principal;

                List<SimpleGrantedAuthority> authorities = new ArrayList<>();

                if (role != null) {
                    String cleanRole = role.toUpperCase().replace("ROLE_", "");
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + cleanRole));
                    authorities.add(new SimpleGrantedAuthority(cleanRole));

                    if ("DRIVER".equalsIgnoreCase(cleanRole)) {
                        principal = jwtUtil.extractDriverId(jwt);
                    } else if ("PASSENGER".equalsIgnoreCase(cleanRole)) {
                        principal = jwtUtil.extractUserId(jwt);
                    } else {
                        principal = jwtUtil.extractUsername(jwt);
                    }
                } else {
                    principal = jwtUtil.extractUsername(jwt);
                }

                if (principal == null || principal.isBlank()) {
                    principal = jwtUtil.extractUsername(jwt);
                }

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        authorities
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authToken);
                SecurityContextHolder.setContext(context);
                securityContextRepository.saveContext(context, request, response);
            }
        } catch (Exception ignored) {
            // Invalid JWT leaves SecurityContext empty, returning 401/403
        }

        filterChain.doFilter(request, response);
    }
}
