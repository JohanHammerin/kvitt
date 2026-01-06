package se.johan.kvitt.event.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EditEventDto(
        String id,
        String title,
        BigDecimal amount,
        boolean expense,
        LocalDateTime dateTime

) {
}