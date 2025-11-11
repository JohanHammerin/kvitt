package se.johan.kvitt.event.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<Event> createEvent(@Valid @RequestBody EventCreateEventRequestDTO eventCreateEventRequestDTO) {
        Event created = eventService.createEvent(eventCreateEventRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/getAllEvents")
    public ResponseEntity<List<EventGetAllEventsByIdResponseDTO>> getAllEventsById(@RequestParam String kvittUserId) {
        List<EventGetAllEventsByIdResponseDTO> events = eventService.getAllEventsById(kvittUserId);
        return ResponseEntity.ok(events);
    }



    @GetMapping("/getTotalIncome")
    public ResponseEntity<BigDecimal> getTotalIncome(@RequestParam String kvittUserId) {
        BigDecimal totalIncome = eventService.getTotalIncome(kvittUserId);
        return ResponseEntity.ok(totalIncome);
    }


    @GetMapping("/getTotalExpense")
    public ResponseEntity<BigDecimal> getTotalExpense(@RequestParam String kvittUserId) {
        BigDecimal totalExpense = eventService.getTotalExpense(kvittUserId);
        return ResponseEntity.ok(totalExpense);
    }

    @GetMapping("/getFinancials")
    public ResponseEntity<BigDecimal> getFinancials(@RequestParam String kvittUserId) {
        BigDecimal financials = eventService.getFinancials(kvittUserId);
        return ResponseEntity.ok(financials);
    }


    @GetMapping("/test")
    public String test() {
        return "test";
    }
}
