package se.johan.kvitt.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import se.johan.kvitt.kvittUser.jwt.JwtAuthenticationFilter;

/**
 * Huvudkonfiguration för applikationens säkerhet med Spring Security.
 * Klassen definierar filterkedjan, auktoriseringsregler och hantering av JWT-autentisering.
 */
@Configuration
@EnableWebSecurity
public class AppSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    /**
     * Konstruktor för AppSecurityConfig.
     * @param jwtAuthenticationFilter Filter för att validera JWT-tokens i inkommande anrop.
     * @param corsConfigurationSource Källa för CORS-inställningar som tillåter cross-origin anrop.
     */
    @Autowired
    public AppSecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, CorsConfigurationSource corsConfigurationSource) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    /**
     * Exponerar AuthenticationManager som en bean.
     * Denna används för att hantera autentiseringsprocessen vid inloggning.
     * @param configuration Befintlig autentiseringskonfiguration.
     * @return En AuthenticationManager-instans.
     * @throws Exception Om konfigurationen misslyckas.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * Definierar Security Filter Chain.
     * Konfigurerar CORS, inaktiverar CSRF, sätter behörighetskrav på endpoints
     * och tvingar fram en stateless sessionshantering.
     * @param httpSecurity Objekt för att bygga säkerhetsinställningar.
     * @return En färdigbyggd SecurityFilterChain.
     * @throws Exception Om bygget av säkerhetskedjan misslyckas.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/kvittUser/create",
                                "/api/v1/kvittUser/login",
                                "/api/v1/event/test"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }
}