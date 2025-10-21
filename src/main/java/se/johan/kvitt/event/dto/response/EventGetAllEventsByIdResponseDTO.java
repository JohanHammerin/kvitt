package se.johan.kvitt.event.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventGetAllEventsByIdResponseDTO(
        String title,
        BigDecimal amount,
        boolean expense,
        LocalDateTime dateTime
) {
}
