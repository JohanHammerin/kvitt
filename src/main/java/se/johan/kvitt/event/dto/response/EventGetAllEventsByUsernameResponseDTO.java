package se.johan.kvitt.event.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventGetAllEventsByUsernameResponseDTO(
        String id, // <-- Lägg till detta
        String title,
        BigDecimal amount,
        boolean expense,
        LocalDateTime dateTime,
        boolean paid
) {}