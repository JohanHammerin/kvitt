package se.johan.kvitt.event.objectMapper;

import org.springframework.stereotype.Component;
import se.johan.kvitt.event.dto.request.CreateEventDto;
import se.johan.kvitt.event.dto.response.EventGetAllEventsByUsernameResponseDTO;
import se.johan.kvitt.event.model.Event;

import java.time.LocalDateTime;

@Component
public class EventMapper {

    public Event toEntity(CreateEventDto createEventDto) {
        return new Event(
                null,
                createEventDto.title(),
                createEventDto.amount(),
                createEventDto.expense(),
                LocalDateTime.now(),
                createEventDto.paid(),
                createEventDto.username()
        );
    }

    // Uppdatera toGetAllEventsByIdDTO metoden
    public EventGetAllEventsByUsernameResponseDTO toGetAllEventsByIdDTO(Event event) {
        return new EventGetAllEventsByUsernameResponseDTO(
                event.getId(), // <-- Lägg till detta
                event.getTitle(),
                event.getAmount(),
                event.isExpense(),
                event.getDateTime(),
                event.isPaid()
        );
    }
}