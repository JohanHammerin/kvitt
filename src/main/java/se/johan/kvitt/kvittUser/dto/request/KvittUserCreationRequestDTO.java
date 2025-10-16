package se.johan.kvitt.kvittUser.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record KvittUserCreationRequestDTO(
        @Size(max = 50, message = "Användarnamnet får inte vara längre än 255 tecken")
        @NotBlank(message = "Användarnamnet får inte vara tomt")
        String username,
        @Size(max = 50, message = "Lösenordet får inte vara längre än 255 tecken")
        @NotBlank(message = "Lösenordet får inte vara tomt")
        String password)
        {}
