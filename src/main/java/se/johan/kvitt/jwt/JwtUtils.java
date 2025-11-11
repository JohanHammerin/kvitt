package se.johan.kvitt.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import se.johan.spring_security.user.CustomUser;
import se.johan.spring_security.user.authority.UserRole;

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
    private final String base64EncodedSecretKey =
            "eDJbzt6SgSpS2FS4XDlrVlQohJvlip1PXs/gE/xkaRB4AgOnobDlr5AkRUX8wqN8nr7nB45Q7Dl/";
    private final byte[] keyBytes =
            Base64.getDecoder().decode(base64EncodedSecretKey);
    private final SecretKey key = Keys.hmacShaKeyFor(keyBytes); // Skapar en kryptografisk nyckel, som skyddar oss från John Pork
    // JWT expire in 1 hour
    private final int jwtExpirationMs = (int) TimeUnit.HOURS.toMillis(1);


    public String generateJwtToken(CustomUser customUser) {
        logger.debug("Generating JWT for user: {} wit roles: {}", customUser.getUsername(), customUser.getUserRoles());

        List<String> roles = customUser.getUserRoles().stream().map(
                userRole -> userRole.getRoleName()
        ).toList();



        String token = Jwts.builder()
                .subject(customUser.getUsername()) // sub
                .claim("authorities", roles) // claim: authorities finns inte
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key)
                .compact();

        logger.info("JWT generated successfully for user {}", customUser.getUsername());
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
