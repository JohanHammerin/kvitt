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

/**
 * REST-controller för hantering av ekonomiska händelser (events).
 * Tillhandahåller endpoints för att skapa, redigera, radera och hämta statistik kring händelser.
 */
@RequestMapping("api/v1/event")
@RestController
public class EventController {

    private final EventService eventService;

    /**
     * Konstruktor för EventController.
     * @param eventService Servicen som hanterar affärslogik för händelser.
     */
    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Skapar en ny ekonomisk händelse.
     * @param dto Dataobjekt innehållande information för den nya händelsen.
     * @return Den skapade händelsen med status 201 Created.
     */
    @PostMapping("/create")
    public ResponseEntity<Event> createEvent(@Valid @RequestBody CreateEventDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createEvent(dto));
    }

    /**
     * Uppdaterar en befintlig ekonomisk händelse.
     * @param dto Dataobjekt med uppdaterad information.
     * @return Den uppdaterade händelsen med status 202 Accepted.
     */
    @PutMapping("/edit")
    public ResponseEntity<Event> editEvent(@Valid @RequestBody EditEventDto dto) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(eventService.editEvent(dto));
    }

    /**
     * Raderar en händelse baserat på dess ID.
     * @param id Identifieraren för händelsen som ska tas bort.
     * @return ResponseEntity med status 204 No Content.
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteEvent(@RequestParam String id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Hämtar alla händelser kopplade till ett specifikt användarnamn.
     * @param username Användarnamnet vars händelser ska hämtas.
     * @return En lista med händelser för användaren.
     */
    @GetMapping("/getAllEvents")
    public ResponseEntity<List<EventGetAllEventsByUsernameResponseDTO>> getAllEventsByUsername(@RequestParam String username) {
        return ResponseEntity.ok(eventService.getAllEventsByUsername(username));
    }

    /**
     * Hämtar den totala summan av inkomster för en användare.
     * @param username Användarnamnet för beräkningen.
     * @return Total inkomst som BigDecimal.
     */
    @GetMapping("/getTotalIncome")
    public ResponseEntity<BigDecimal> getTotalIncome(@RequestParam String username) {
        return ResponseEntity.ok(eventService.getTotalIncome(username));
    }

    /**
     * Hämtar den totala summan av utgifter för en användare.
     * @param username Användarnamnet för beräkningen.
     * @return Total utgift som BigDecimal.
     */
    @GetMapping("/getTotalExpense")
    public ResponseEntity<BigDecimal> getTotalExpense(@RequestParam String username) {
        return ResponseEntity.ok(eventService.getTotalExpense(username));
    }

    /**
     * Hämtar det ekonomiska nettot (balansen) för en användare.
     * @param username Användarnamnet för beräkningen.
     * @return Ekonomiskt netto som BigDecimal.
     */
    @GetMapping("/getFinancials")
    public ResponseEntity<BigDecimal> getFinancials(@RequestParam String username) {
        return ResponseEntity.ok(eventService.getFinancials(username));
    }

    /**
     * Hämtar en sammanfattande status (KvittStatus) för en användare.
     * @param username Användarnamnet för statuskontrollen.
     * @return Ett DTO-objekt med användarens ekonomiska status.
     */
    @GetMapping("/getKvittStatus")
    public ResponseEntity<KvittStatusResponseDTO> getKvittStatus(@RequestParam String username) {
        return ResponseEntity.ok(eventService.getKvittStatus(username));
    }

    /**
     * Hämtar alla händelser som är markerade som betalda för en användare.
     * @param username Användarnamnet vars betalda händelser ska hämtas.
     * @return En lista med betalda händelser.
     */
    @GetMapping("/getPaidEvents")
    public ResponseEntity<List<Event>> getPaidEvents(@RequestParam String username) {
        return ResponseEntity.ok(eventService.getPaidEvents(username));
    }

    /**
     * Hämtar alla händelser som ännu inte är markerade som betalda för en användare.
     * @param username Användarnamnet vars obetalda händelser ska hämtas.
     * @return En lista med obetalda händelser.
     */
    @GetMapping("/getUnPaidEvents")
    public ResponseEntity<List<Event>> getUnPaidEvents(@RequestParam String username) {
        return ResponseEntity.ok(eventService.getUnPaidEvents(username));
    }
}