package se.johan.kvitt.event.objectMapper;

import org.springframework.stereotype.Component;
import se.johan.kvitt.event.dto.request.EventCreationRequestDTO;
import se.johan.kvitt.event.model.Event;

import java.time.LocalDateTime;

@Component
public class EventMapper {

    public Event toEntity(EventCreationRequestDTO eventCreationRequestDTO) {
        return new Event(
                null,
                eventCreationRequestDTO.title(),
                eventCreationRequestDTO.amount(),
                eventCreationRequestDTO.expense(),
                LocalDateTime.now(),
                eventCreationRequestDTO.kvittUserId()
        );
    }

}
