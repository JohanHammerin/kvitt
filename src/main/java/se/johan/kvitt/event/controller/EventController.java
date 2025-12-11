package se.johan.kvitt.event.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.johan.kvitt.event.dto.request.CreateEventDto;
import se.johan.kvitt.event.dto.request.EditEventDto;
import se.johan.kvitt.event.dto.response.EventGetAllEventsByUsernameResponseDTO;
import se.johan.kvitt.event.dto.response.KvittStatusResponseDTO;
import se.johan.kvitt.event.model.Event;
import se.johan.kvitt.event.service.EventService;

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
    public ResponseEntity<Event> createEvent(@Valid @RequestBody CreateEventDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createEvent(dto));
    }

    @PutMapping("/edit")
    public ResponseEntity<Event> editEvent(@Valid @RequestBody EditEventDto dto) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(eventService.editEvent(dto));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteEvent(@RequestParam String id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/getAllEvents")
    public ResponseEntity<List<EventGetAllEventsByUsernameResponseDTO>> getAllEventsByUsername(@RequestParam String username) {
        // Servicen kastar exception om användaren inte finns -> Global Handler fångar det.
        return ResponseEntity.ok(eventService.getAllEventsByUsername(username));
    }

    @GetMapping("/getTotalIncome")
    public ResponseEntity<BigDecimal> getTotalIncome(@RequestParam String username) {
        return ResponseEntity.ok(eventService.getTotalIncome(username));
    }

    @GetMapping("/getTotalExpense")
    public ResponseEntity<BigDecimal> getTotalExpense(@RequestParam String username) {
        return ResponseEntity.ok(eventService.getTotalExpense(username));
    }

    @GetMapping("/getFinancials")
    public ResponseEntity<BigDecimal> getFinancials(@RequestParam String username) {
        return ResponseEntity.ok(eventService.getFinancials(username));
    }

    @GetMapping("/getKvittStatus")
    public ResponseEntity<KvittStatusResponseDTO> getKvittStatus(@RequestParam String username) {
        return ResponseEntity.ok(eventService.getKvittStatus(username));
    }

    @GetMapping("/getPaidEvents")
    public ResponseEntity<List<Event>> getPaidEvents(@RequestParam String username) {
        return ResponseEntity.ok(eventService.getPaidEvents(username));
    }

    @GetMapping("/getUnPaidEvents")
    public ResponseEntity<List<Event>> getUnPaidEvents(@RequestParam String username) {
        return ResponseEntity.ok(eventService.getUnPaidEvents(username));
    }
}