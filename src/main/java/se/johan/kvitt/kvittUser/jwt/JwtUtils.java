package se.johan.kvitt.kvittUser.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import se.johan.kvitt.auth.UserRole;
import se.johan.kvitt.kvittUser.model.KvittUser;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    // Hämtar värdet från application.properties (secretBase64= ${BASE64})
    @Value("${secretBase64}")
    private String base64EncodedSecretKey;

    private SecretKey key;

    private final int jwtExpirationMs = (int) TimeUnit.HOURS.toMillis(24);

    /**
     * Denna metod körs automatiskt när Spring har injicerat alla värden.
     * Vi skapar den kryptografiska nyckeln härifrån istället för direkt vid variabeln.
     */
    @PostConstruct
    public void init() {
        try {
            // Trimma eventuella mellanslag som kan uppstå vid miljövariabel-injektion
            byte[] keyBytes = Base64.getDecoder().decode(base64EncodedSecretKey.trim());
            this.key = Keys.hmacShaKeyFor(keyBytes);
            logger.info("✅ JwtUtils initialized: Cryptographic key generated successfully.");
        } catch (Exception e) {
            logger.error("❌ Failed to initialize JWT key: {}", e.getMessage());
            throw new RuntimeException("Could not initialize JWT key", e);
        }
    }


    public String generateJwtToken(KvittUser kvittUser) {

        List<String> roles = kvittUser.getRoles().stream()
                .map(UserRole::getRoleName)
                .toList();

        String token = Jwts.builder().subject(kvittUser.getUsername())
                .claim("authorities", roles).issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key)
                .compact();


        logger.info("JWT generated successfully for user {}", kvittUser.getUsername());
        return token;
    }


    public String getUsernameFromJwtToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String username = claims.getSubject();
            logger.debug("Extracted username '{}' from JWT token", username);
            return username;

        } catch (Exception e) {
            logger.warn("Failed to extract username from token: {}", token);
            return null;
        }
    }


    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(authToken);

            logger.debug("JWT validation succeeded");
            return true;
        } catch (Exception e) {
            logger.error("JWT validation failed: {}", e.getMessage());
        }
        return false;
    }

    public Set<UserRole> getRolesFromJwtToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        List<?> authoritiesClaim = claims.get("authorities", List.class);

        if (authoritiesClaim == null || authoritiesClaim.isEmpty()) {
            logger.warn("No authorities found in the JWT token");
            return Set.of();
        }

        Set<UserRole> roles = authoritiesClaim.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(role -> role.replace("ROLE_", ""))
                .map(String::toUpperCase)
                .map(UserRole::valueOf) // Map to ENUM
                .collect(Collectors.toSet());

        logger.debug("Extracted roles from JWT token: {}", roles);
        return roles;


    }


}
