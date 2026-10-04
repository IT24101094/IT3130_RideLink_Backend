package com.ridelink.farepayment.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}")
    private String secret;

    private Key getSigningKey() {
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(secret);
            if (keyBytes.length < 32) {
                keyBytes = secret.getBytes(StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Map<String, Object> extraClaims, String subject) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(getSigningKey(), io.jsonwebtoken.SignatureAlgorithm.HS256)
                .compact();
    }

    public String generatePassengerToken(String userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", "ROLE_PASSENGER");
        return generateToken(claims, username);
    }

    public String generateDriverToken(String driverId, String licenseNumber, String name) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("driverId", driverId);
        claims.put("licenseNumber", licenseNumber);
        claims.put("name", name);
        claims.put("role", "DRIVER");
        return generateToken(claims, licenseNumber != null ? licenseNumber : driverId);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractUsername(String token) {
        return extractClaim(token, claims -> claims.getSubject());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, claims -> claims.getExpiration());
    }

    public boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    public String extractRole(String token) {
        try {
            Claims claims = extractAllClaims(token);
            if (claims.get("role") != null) {
                return claims.get("role").toString();
            }
            if (claims.get("roles") != null) {
                return claims.get("roles").toString();
            }
            if (claims.get("authorities") != null) {
                return claims.get("authorities").toString();
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public String extractUserId(String token) {
        try {
            Claims claims = extractAllClaims(token);
            if (claims.get("userId") != null) {
                return claims.get("userId").toString();
            }
            if (claims.get("id") != null) {
                return claims.get("id").toString();
            }
            return claims.getSubject();
        } catch (Exception ignored) {
        }
        return null;
    }

    public String extractDriverId(String token) {
        try {
            Claims claims = extractAllClaims(token);
            if (claims.get("driverId") != null) {
                return claims.get("driverId").toString();
            }
            if (claims.get("id") != null) {
                return claims.get("id").toString();
            }
            return claims.getSubject();
        } catch (Exception ignored) {
        }
        return null;
    }
}
