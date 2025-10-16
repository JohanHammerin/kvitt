package se.johan.kvitt.event.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import se.johan.kvitt.event.dto.request.EventCreationRequestDTO;
import se.johan.kvitt.event.model.Event;
import se.johan.kvitt.event.service.EventService;

@RequestMapping("api/v1/event")
@RestController
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/create")
    public Mono<ResponseEntity<Event>> createEvent(@Valid @RequestBody EventCreationRequestDTO eventCreationRequestDTO) {
        return eventService.createEvent(eventCreationRequestDTO)
                .map(event -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(event)
                );
    }
}
