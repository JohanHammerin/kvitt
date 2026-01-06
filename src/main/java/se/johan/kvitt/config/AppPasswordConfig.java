package se.johan.kvitt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Konfigurationsklass för hantering av lösenordskryptering i applikationen.
 * Denna klass definierar vilken algoritm som ska användas för att säkert lagra
 * och verifiera användarlösenord.
 */
@Configuration
public class AppPasswordConfig {

    /**
     * Skapar en bean för lösenordskryptering med BCrypt-algoritmen.
     * BCrypt använder en saltad hash-funktion vilket skyddar mot brute force-attacker.
     ** @return En PasswordEncoder-instans (BCrypt) med en styrka på 10.
     */
    @Bean
    public PasswordEncoder passwordEncoder () {
        return new BCryptPasswordEncoder(10);
    }
}