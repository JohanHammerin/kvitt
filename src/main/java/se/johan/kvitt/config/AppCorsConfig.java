package se.johan.kvitt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Konfigurationsklass (CORS).
 * Denna klass definierar vilka externa domäner, metoder och headers som tillåts
 * interagera med API:et / Webservice.
 */
@Configuration
public class AppCorsConfig {

    /**
     * Skapar och konfigurerar en källa för CORS-inställningar.
     * Inställningarna tillåter kommunikation mellan frontend (Vercel/Localhost) och backend.
     ** @return En konfigurerad CorsConfigurationSource som tillåter specifika ursprung och metoder.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // Definierar tillåtna domäner för anrop
        corsConfiguration.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "https://kvitt-frontend.vercel.app",
                "https://kvitt-frontend.vercel.app/",
                "https://kvitt-frontend.onrender.com",
                "https://kvitt-frontend.onrender.com/"
        ));

        // Tillåtna HTTP-metoder
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));

        // Tillåtna HTTP-headers
        corsConfiguration.setAllowedHeaders(List.of("Content-Type", "Authorization", "X-Requested-With"));

        // Möjliggör hantering av cookies och autentiseringstokens över domäner
        corsConfiguration.setAllowCredentials(true);


        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return source;
    }

}