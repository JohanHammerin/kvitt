package se.johan.kvitt.kvittUser.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.johan.kvitt.kvittUser.dto.KvittUserCreateKvittUserRequestDTO;
import se.johan.kvitt.kvittUser.dto.KvittUserLoginRequestDTO;
import se.johan.kvitt.kvittUser.dto.KvittUserLoginResponseDTO;
import se.johan.kvitt.kvittUser.model.KvittUser;
import se.johan.kvitt.kvittUser.service.KvittUserService;

import java.time.Duration;

@RequestMapping("api/v1/kvittUser")
@RestController
public class KvittUserController {
    private final KvittUserService kvittUserService;

    @Autowired
    public KvittUserController(KvittUserService kvittUserService) {
        this.kvittUserService = kvittUserService;
    }

    @PostMapping("/create")
    public ResponseEntity<KvittUser> createKvittUser(@Valid @RequestBody KvittUserCreateKvittUserRequestDTO kvittUserCreateKvittUserRequestDTO) {
        KvittUser created = kvittUserService.createKvittUser(kvittUserCreateKvittUserRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /*
    @PostMapping("/login")
    public ResponseEntity<KvittUserLoginResponseDTO> login(@Valid @RequestBody KvittUserLoginRequestDTO dto) {
        try {
            String token = kvittUserService.login(dto);

            ResponseCookie jwtCookie = ResponseCookie.from("jwtToken", token)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(Duration.ofHours(24))
                    .sameSite("None")
                    .build();

            // Lägg till Partitioned manuellt för att stödja moderna webbläsarkrav (CHIPS)
            String cookieWithPartitioned = jwtCookie + "; Partitioned";

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookieWithPartitioned)
                    .body(new KvittUserLoginResponseDTO(dto.username(), null));

        } catch (Exception e) {
            throw e;
        }
    }
    */

    @PostMapping("/login")
    public ResponseEntity<KvittUserLoginResponseDTO> login(@Valid @RequestBody KvittUserLoginRequestDTO dto) {
        try {
            // Generera token precis som förut
            String token = kvittUserService.login(dto);

            // Vi skippar ResponseCookie helt!

            System.out.println("✅ Token generated and sent in response body");

            // Skicka tillbaka token i bodyn istället för som en kaka
            return ResponseEntity.ok(new KvittUserLoginResponseDTO(dto.username(), token));

        } catch (Exception e) {
            System.err.println("❌ Login failed: " + e.getMessage());
            throw e;
        }
    }
}