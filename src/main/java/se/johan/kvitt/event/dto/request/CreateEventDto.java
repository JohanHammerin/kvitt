package se.johan.kvitt.event.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;


public record CreateEventDto(
        @NotBlank(message = "Title is required")
        String title,

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be positive")
        BigDecimal amount,

        boolean expense,

        boolean paid,

        @NotBlank(message = "Username is required")
        String username
) {}