package se.johan.kvitt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class AppCorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // Whitelist
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:3000", "https://kvitt-frontend-v5no.vercel.app", "https://kvitt-frontend-v5no.vercel.app/")); // VERCEL ADDRESS / DOMAIN
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));                                    // HTTP METHODS
        corsConfiguration.setAllowedHeaders(List.of("Content-Type", "Authorization", "X-Requested-With"));
        corsConfiguration.setAllowCredentials(true); // Send Cookies

        // Backend related endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration); // ENABLE EVERYTHING

        return source;
    }

}

