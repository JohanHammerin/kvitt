package se.johan.kvitt.kvittUser.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import se.johan.kvitt.auth.UserRole;
import se.johan.kvitt.kvittUser.model.KvittUserDetailsService;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtUtils jwtUtils;
    private final KvittUserDetailsService kvittUserDetailsService;

    //@Autowired
    public JwtAuthenticationFilter(JwtUtils jwtUtils, KvittUserDetailsService kvittUserDetailsService) {
        this.jwtUtils = jwtUtils;
        this.kvittUserDetailsService = kvittUserDetailsService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        boolean shouldSkip = path.equals("/api/v1/kvittUser/create") ||
                path.equals("/api/v1/kvittUser/login");

        System.out.println("🔍 JWT Filter - Path: '" + path + "' | Should skip: " + shouldSkip);

        return shouldSkip;
    }

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        logger.debug("---------JwtAuthenticationFilter START---------");

        String token = extractJwtFromCookie(request);
        if (token == null) {
            token = extractJwtFromRequest(request);
        }

        if (token != null && jwtUtils.validateJwtToken(token)) {
            String username = jwtUtils.getUsernameFromJwtToken(token);
            Set<UserRole> userRoles = jwtUtils.getRolesFromJwtToken(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                List<SimpleGrantedAuthority> authorities = userRoles.stream()
                        .flatMap(role -> role.getUserAuthorities().stream())
                        .toList();

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                logger.debug("Authenticated user: '{}' with roles {}", username, userRoles);
            }
        } else {
            logger.debug("No valid JWT token found");
        }

        // Kör alltid filterkedjan
        filterChain.doFilter(request, response);
        logger.debug("---------JwtAuthenticationFilter END---------");
    }



    private String extractJwtFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            // ÄNDRING: Byt "authToken" mot "jwtToken"
            if ("jwtToken".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }


}