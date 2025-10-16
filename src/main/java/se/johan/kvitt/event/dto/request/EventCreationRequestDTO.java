package se.johan.kvitt.event.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public record EventCreationRequestDTO(
        @Size(max = 255, message = "Titeln får inte vara längre än 255 tecken")
        @NotBlank(message = "Titeln får inte vara tomt")
        String title,
        @NotNull
        double amount,
        @NotNull
        boolean expense,
        @Size(max = 255, message = "ID får inte vara längre än 255 tecken")
        @NotBlank(message = "ID får inte vara tomt")
        String kvittUserId

) {
}
