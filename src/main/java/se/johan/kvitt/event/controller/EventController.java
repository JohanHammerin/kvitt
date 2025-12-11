package se.johan.kvitt.event.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.johan.kvitt.event.dto.request.CreateEventDto;
import se.johan.kvitt.event.dto.request.EditEventDto;
import se.johan.kvitt.event.dto.response.EventGetAllEventsByUsernameResponseDTO;
// 👈 NY IMPORT: Lägg till din KvittStatus DTO
import se.johan.kvitt.event.dto.response.KvittStatusResponseDTO;
import se.johan.kvitt.event.model.Event;
import se.johan.kvitt.event.service.EventService;
import se.johan.kvitt.kvittUser.repository.KvittUserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("api/v1/event")
@RestController
public class EventController {

    private final EventService eventService;
    private final KvittUserRepository kvittUserRepository;

    @Autowired
    public EventController(EventService eventService, KvittUserRepository kvittUserRepository) {
        this.eventService = eventService;
        this.kvittUserRepository = kvittUserRepository;
    }

    @PostMapping("/create")
    public ResponseEntity<Event> createEvent(@Valid @RequestBody CreateEventDto dto) {
        Event created = eventService.createEvent(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/edit")
    public ResponseEntity<Event> editEvent(@Valid @RequestBody EditEventDto dto) {
        Event edited = eventService.editEvent(dto);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(edited);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteEvent(@RequestParam String id) {
        eventService.deleteEvent(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(id);
    }

    @GetMapping("/getAllEvents")
    public ResponseEntity<Map<String, Object>> getAllEventsByUsername(@RequestParam String username) {
        // Kontrollera om användaren finns
        boolean userExists = kvittUserRepository.findByUsername(username).isPresent();

        if (!userExists) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "User not found");
            errorResponse.put("message", "User '" + username + "' does not exist");
            errorResponse.put("timestamp", LocalDateTime.now());
            errorResponse.put("status", HttpStatus.NOT_FOUND.value());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        List<EventGetAllEventsByUsernameResponseDTO> events = eventService.getAllEventsByUsername(username);

        Map<String, Object> response = new HashMap<>();
        response.put("username", username);
        response.put("events", events);
        response.put("count", events.size());
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.OK.value());
        response.put("message", events.isEmpty() ? "No events found for user" : "Events retrieved successfully");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/getTotalIncome")
    public ResponseEntity<Map<String, Object>> getTotalIncome(@RequestParam String username) {
        try {
            BigDecimal totalIncome = eventService.getTotalIncome(username);

            Map<String, Object> response = new HashMap<>();
            response.put("username", username);
            response.put("totalIncome", totalIncome);
            response.put("timestamp", LocalDateTime.now());
            response.put("message", "Total income retrieved successfully");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("User not found")) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "User not found");
                errorResponse.put("message", e.getMessage());
                errorResponse.put("timestamp", LocalDateTime.now());
                errorResponse.put("status", HttpStatus.NOT_FOUND.value());

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping("/getTotalExpense")
    public ResponseEntity<Map<String, Object>> getTotalExpense(@RequestParam String username) {
        try {
            BigDecimal totalExpense = eventService.getTotalExpense(username);

            Map<String, Object> response = new HashMap<>();
            response.put("username", username);
            response.put("totalExpense", totalExpense);
            response.put("timestamp", LocalDateTime.now());
            response.put("message", "Total expense retrieved successfully");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("User not found")) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "User not found");
                errorResponse.put("message", e.getMessage());
                errorResponse.put("timestamp", LocalDateTime.now());
                errorResponse.put("status", HttpStatus.NOT_FOUND.value());

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping("/getFinancials")
    public ResponseEntity<Map<String, Object>> getFinancials(@RequestParam String username) {
        try {
            BigDecimal financials = eventService.getFinancials(username);

            Map<String, Object> response = new HashMap<>();
            response.put("username", username);
            response.put("financials", financials);
            response.put("timestamp", LocalDateTime.now());
            response.put("message", "Financials retrieved successfully");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("User not found")) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "User not found");
                errorResponse.put("message", e.getMessage());
                errorResponse.put("timestamp", LocalDateTime.now());
                errorResponse.put("status", HttpStatus.NOT_FOUND.value());

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Internal server error"));
        }
    }

    // --- NY SLUTPUNKT FÖR KVITT STATUS ---
    @GetMapping("/getKvittStatus")
    public ResponseEntity<Map<String, Object>> getKvittStatus(@RequestParam String username) {
        try {
            KvittStatusResponseDTO status = eventService.getKvittStatus(username);

            Map<String, Object> response = new HashMap<>();
            response.put("username", username);
            response.put("expensesBack", status.expensesBack());
            response.put("lastKvittDate", status.lastKvittDate());
            response.put("timestamp", LocalDateTime.now());
            response.put("message", status.expensesBack() == 0 ? "User is Kvitt (paid up)" : "User is back on expenses");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("User not found")) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "User not found");
                errorResponse.put("message", e.getMessage());
                errorResponse.put("timestamp", LocalDateTime.now());
                errorResponse.put("status", HttpStatus.NOT_FOUND.value());

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping("/getPaidEvents")
    public ResponseEntity<Map<String, Object>> getPaidEvents(@RequestParam String username) {
        try {
            List<Event> paidEvents = eventService.getPaidEvents(username);

            Map<String, Object> response = new HashMap<>();
            response.put("username", username);
            response.put("paidEvents", paidEvents);
            response.put("count", paidEvents.size());
            response.put("timestamp", LocalDateTime.now());
            response.put("message", paidEvents.isEmpty() ? "No paid events found" : "Paid events retrieved successfully");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("User not found")) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "User not found");
                errorResponse.put("message", e.getMessage());
                errorResponse.put("timestamp", LocalDateTime.now());
                errorResponse.put("status", HttpStatus.NOT_FOUND.value());

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping("/getUnPaidEvents")
    public ResponseEntity<Map<String, Object>> getUnPaidEvents(@RequestParam String username) {
        try {
            List<Event> unPaidEvents = eventService.getUnPaidEvents(username);

            Map<String, Object> response = new HashMap<>();
            response.put("username", username);
            response.put("unPaidEvents", unPaidEvents);
            response.put("count", unPaidEvents.size());
            response.put("timestamp", LocalDateTime.now());
            response.put("message", unPaidEvents.isEmpty() ? "No unpaid events found" : "Unpaid events retrieved successfully");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("User not found")) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "User not found");
                errorResponse.put("message", e.getMessage());
                errorResponse.put("timestamp", LocalDateTime.now());
                errorResponse.put("status", HttpStatus.NOT_FOUND.value());

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Internal server error"));
        }
    }

    @GetMapping("/test")
    public String test() {
        return "Event controller is working!";
    }
}