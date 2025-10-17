package se.johan.kvitt.event.dto.response;

import java.time.LocalDateTime;

public record EventGetAllEventsByIdDTO(
        String title,
        double amount,
        boolean expense,
        LocalDateTime dateTime
) {
}
