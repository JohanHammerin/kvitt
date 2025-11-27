package se.johan.kvitt.event.objectMapper;

import org.springframework.stereotype.Component;
import se.johan.kvitt.event.dto.request.EventCreateEventRequestDTO;
import se.johan.kvitt.event.dto.response.EventGetAllEventsByUsernameResponseDTO;
import se.johan.kvitt.event.model.Event;

import java.time.LocalDateTime;

@Component
public class EventMapper {

    public Event toEntity(EventCreateEventRequestDTO eventCreateEventRequestDTO) {
        return new Event(
                null,
                eventCreateEventRequestDTO.title(),
                eventCreateEventRequestDTO.amount(),
                eventCreateEventRequestDTO.expense(),
                LocalDateTime.now(),
                eventCreateEventRequestDTO.paid(),
                eventCreateEventRequestDTO.username()
        );
    }

    public EventGetAllEventsByUsernameResponseDTO toGetAllEventsByIdDTO(Event event) {
        return new EventGetAllEventsByUsernameResponseDTO(
                event.getTitle(),
                event.getAmount(),
                event.isExpense(),
                event.getDateTime(),
                event.isPaid()
        );
    }
}