package se.johan.kvitt.kvittUser.dto;

import jakarta.validation.constraints.NotBlank;

public record KvittUserLoginRequestDTO(
        @NotBlank String username,
        @NotBlank String password
) {}
