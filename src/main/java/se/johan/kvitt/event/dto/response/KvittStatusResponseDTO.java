package se.johan.kvitt.event.dto.response;

import java.time.LocalDate;

public record KvittStatusResponseDTO(
        long expensesBack, // Antal utgifter man är back
        LocalDate lastKvittDate // Datumet för den sista utgift som ditt saldo täckte
) {}