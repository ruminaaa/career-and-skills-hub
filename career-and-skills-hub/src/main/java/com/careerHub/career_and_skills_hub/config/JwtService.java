package com.careerHub.career_and_skills_hub.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}") // ✅ Inject the secret key from application.properties
    private String secretKey;

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    public String extractName(String token) {
        return extractClaim(token, claims -> claims.get("name", String.class));
    }

    public String generateToken(String email, String name) {
        return Jwts.builder()
                .claim("name", name)  // Store name in token
                .setSubject(email)     // Store email in subject
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }




    public boolean isTokenValid(String token, String userEmail) {
        return extractName(token).equals(userEmail) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }
}
