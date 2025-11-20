package se.johan.kvitt.kvittUser.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
            String token = kvittUserService.login(dto);
            System.out.println("✅ Token generated successfully");
            return ResponseEntity.ok(new KvittUserLoginResponseDTO(dto.username(), token));
        } catch (Exception e) {
            System.err.println("❌ Login failed: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
