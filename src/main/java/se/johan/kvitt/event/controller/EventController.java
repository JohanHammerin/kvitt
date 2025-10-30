package se.johan.kvitt.event.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import se.johan.kvitt.event.dto.request.EventCreateEventRequestDTO;
import se.johan.kvitt.event.dto.response.EventGetAllEventsByIdResponseDTO;
import se.johan.kvitt.event.model.Event;
import se.johan.kvitt.event.service.EventService;
import org.springframework.web.bind.annotation.RequestParam;


import java.math.BigDecimal;
import java.util.List;

@RequestMapping("api/v1/event")
@RestController
public class EventController {


    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/create")
    public Mono<ResponseEntity<Event>> createEvent(@Valid @RequestBody EventCreateEventRequestDTO eventCreateEventRequestDTO) {
        return eventService.createEvent(eventCreateEventRequestDTO)
                .map(event -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(event)
                );
    }

    @GetMapping("/getAllEvents")
    public Mono<ResponseEntity<List<EventGetAllEventsByIdResponseDTO>>> getAllEventsById(@RequestParam String kvittUserId) {
        return eventService.getAllEventsById(kvittUserId)
                .map(events -> ResponseEntity
                        .status(HttpStatus.OK)
                        .body(events)

                );
    }


    @GetMapping("/getTotalIncome")
    public Mono<ResponseEntity<BigDecimal>> getTotalIncome(@RequestParam String kvittUserId) {
        return eventService.getTotalIncome(kvittUserId)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/getTotalExpense")
    public Mono<ResponseEntity<BigDecimal>> getTotalExpense(@RequestParam String kvittUserId) {
        return eventService.getTotalExpense(kvittUserId)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/getFinancials")
    public Mono<ResponseEntity<BigDecimal>> getFinancials(@RequestParam String kvittUserId) {
        return eventService.getFinancials(kvittUserId)
                .map(ResponseEntity::ok);
    }


    @GetMapping("/test")
    public String test() {
        return "test";
    }


}
