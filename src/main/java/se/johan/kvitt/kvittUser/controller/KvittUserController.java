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
    public ResponseEntity<KvittUser> createKvittUser(
            @Valid @RequestBody KvittUserCreateKvittUserRequestDTO kvittUserCreateKvittUserRequestDTO
    ) {
        KvittUser created = kvittUserService.createKvittUser(kvittUserCreateKvittUserRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/login")
    public ResponseEntity<KvittUserLoginResponseDTO> login(
            @Valid @RequestBody KvittUserLoginRequestDTO dto
    ) {
        System.out.println("🔑 LOGIN endpoint reached! Username: " + dto.username());
        try {
            // 1. Hämta token strängen från service
            String token = kvittUserService.login(dto);

            // 2. Skapa en HttpOnly Cookie
            ResponseCookie jwtCookie = ResponseCookie.from("jwtToken", token)
                    .httpOnly(true)       // Gör att JavaScript inte kan läsa kakan (skyddar mot XSS)
                    .secure(false)        // Sätt till true om du kör HTTPS (i prod)
                    .path("/")            // Kakan gäller för hela applikationen
                    .maxAge(Duration.ofHours(24)) // Samma tid som din JWT giltighet
                    .sameSite("None")   // Skyddar mot CSRF
                    .build();

            System.out.println("✅ Token generated and cookie created");

            // 3. Returnera Username i body, men Token i Header
            // Notera: Vi skickar null eller tom sträng för token i DTO:n eftersom den ligger i kakan nu
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .body(new KvittUserLoginResponseDTO(dto.username(), null));

        } catch (Exception e) {
            System.err.println("❌ Login failed: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}