package com.andrei.stockportfoliobackend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import io.github.cdimascio.dotenv.Dotenv;
import java.security.Key;
import java.util.Date;

/**
 * Utility class for handling JSON Web Tokens (JWT).
 * Responsible for generating tokens upon login and validating them for subsequent requests.
 */
@Component
public class JwtUtils {
    // 256-bit Hex secret key for HS256 algorithm
    private static final String JWT_SECRET = Dotenv.configure()
            .ignoreIfMissing()
            .load()
            .get("JWT_SECRET");

    // Token validity duration (24 Hours)
    private static final int EXPIRATION_MS = 86400000;

    /**
     * Generates a JWT token for a specific user.
     * The token contains the username as the subject and an expiration date.
     *
     * @param username The authenticated username.
     * @return A signed JWT string.
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + EXPIRATION_MS)) // Expira in 24 ore
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(JWT_SECRET));
    }

    /**
     * Extracts the username (subject) from the JWT token.
     *
     * @param token The JWT string.
     * @return The username embedded in the token.
     */
    public String getUsernameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * Validates the JWT token.
     * Checks if the signature is correct and if the token has not expired.
     *
     * @param authToken The JWT string to validate.
     * @return true if valid, false otherwise.
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.err.println("Invalid JWT token: " + e.getMessage());
        }
        return false;
    }
}