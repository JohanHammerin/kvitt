package se.johan.kvitt.event.dto.response;

import java.time.LocalDate;

public record KvittStatusResponseDTO(
        long expensesBack,
        LocalDate lastKvittDate // Framtid funktion om man vill se senaste datumet
) {}