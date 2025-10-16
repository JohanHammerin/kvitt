package se.johan.kvitt.kvittUser.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import se.johan.kvitt.kvittUser.dto.request.KvittUserCreationRequestDTO;
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
    public Mono<ResponseEntity<KvittUser>> createKvittUser(@Valid @RequestBody KvittUserCreationRequestDTO kvittUserCreationRequestDTO) {
        return kvittUserService.createKvittUser(kvittUserCreationRequestDTO)
                .map(kvittUser -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(kvittUser)
                );
    }
}
