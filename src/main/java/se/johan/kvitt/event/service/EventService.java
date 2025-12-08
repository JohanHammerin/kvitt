package se.johan.kvitt.event.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.johan.kvitt.event.dto.request.EventCreateEventRequestDTO;
import se.johan.kvitt.event.dto.response.EventGetAllEventsByUsernameResponseDTO;
import se.johan.kvitt.event.dto.response.KvittStatusResponseDTO;
import se.johan.kvitt.event.model.Event;
import se.johan.kvitt.event.objectMapper.EventMapper;
import se.johan.kvitt.event.repository.EventRepository;
import se.johan.kvitt.kvittUser.repository.KvittUserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final KvittUserRepository kvittUserRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public EventService(EventRepository eventRepository, EventMapper eventMapper, KvittUserRepository kvittUserRepository) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.kvittUserRepository = kvittUserRepository;
    }

    public Event createEvent(EventCreateEventRequestDTO eventCreateEventRequestDTO) {
        // 1. Spara eventet först för att uppdatera saldot
        Event savedEvent = eventRepository.save(eventMapper.toEntity(eventCreateEventRequestDTO));
        logger.info("New Event created & saved: {}", savedEvent.getTitle());

        // 2. Försök alltid betala obetalda utgifter (oavsett om det var inkomst eller utgift som lades till)
        calculateUnPaidEvents(savedEvent.getUsername());

        return savedEvent;
    }

    public List<EventGetAllEventsByUsernameResponseDTO> getAllEventsByUsername(String username) {
        logger.info("{} requested all events", username);
        validateUser(username);

        List<Event> events = eventRepository.findByUsername(username);
        logger.info("Found {} events for user: {}", events.size(), username);

        return events.stream()
                .map(eventMapper::toGetAllEventsByIdDTO)
                .toList();
    }

    public BigDecimal getTotalIncome(String username) {
        validateUser(username);
        return eventRepository.findByUsername(username)
                .stream()
                .filter(event -> !event.isExpense())
                .map(Event::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalExpense(String username) {
        validateUser(username);
        return eventRepository.findByUsername(username)
                .stream()
                .filter(Event::isExpense)
                .map(Event::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getFinancials(String username) {
        // validateUser anropas inuti getTotalIncome/Expense
        BigDecimal totalIncome = getTotalIncome(username);
        BigDecimal totalExpense = getTotalExpense(username);

        return totalIncome.subtract(totalExpense);
    }

    public List<Event> getPaidEvents(String username) {
        validateUser(username);
        return eventRepository.findByUsername(username)
                .stream()
                .filter(Event::isExpense)
                .filter(Event::isPaid)
                .toList();
    }

    public List<Event> getUnPaidEvents(String username) {
        validateUser(username);
        return eventRepository.findByUsername(username)
                .stream()
                .filter(Event::isExpense)
                .filter(event -> !event.isPaid())
                .toList();
    }

    public KvittStatusResponseDTO getKvittStatus(String username) {
        validateUser(username);

        BigDecimal totalIncome = getTotalIncome(username);
        BigDecimal totalExpenses = getTotalExpense(username);

        // Om Inkomst >= Utgifter är vi helt KVITT
        // Detta täcker fallet där allt är betalt och saldot är positivt eller noll.
        if (totalIncome.compareTo(totalExpenses) >= 0) {
            return new KvittStatusResponseDTO(
                    0L,
                    LocalDate.now()
            );
        }

        // Om vi är back (Inkomst < Utgifter):
        // Räkna helt enkelt hur många utgifter som har status paid=false i databasen.
        long expensesBack = eventRepository.findByUsername(username).stream()
                .filter(Event::isExpense)
                .filter(event -> !event.isPaid()) // Filtrera fram de som faktiskt är obetalda
                .count();

        // Hitta datumet för den senaste utgiften som faktiskt BLEV betald
        LocalDate lastKvittDate = eventRepository.findByUsername(username).stream()
                .filter(Event::isExpense)
                .filter(Event::isPaid) // Bara betalda
                .max(Comparator.comparing(Event::getDateTime)) // Ta den nyaste av de betalda
                .map(event -> event.getDateTime().toLocalDate())
                .orElse(LocalDate.now()); // Om inga betalda finns, använd dagens datum

        return new KvittStatusResponseDTO(
                expensesBack,
                lastKvittDate
        );
    }

    // --- Privata Hjälpmetoder ---

    private void calculateUnPaidEvents(String username) {
        validateUser(username);

        BigDecimal totalIncome = getTotalIncome(username);
        BigDecimal alreadyPaidAmount = getTotalPaidExpensesAmount(username);
        BigDecimal availableFunds = totalIncome.subtract(alreadyPaidAmount);

        List<Event> unpaidExpenses = eventRepository.findByUsernameAndExpenseTrueAndPaidFalseOrderByDateTimeAsc(username);

        logger.info("Calculating unpaid events. Available funds: {}", availableFunds);

        List<Event> updatedEvents = new ArrayList<>();

        for (Event expense : unpaidExpenses) {
            if (availableFunds.compareTo(expense.getAmount()) >= 0) {
                expense.setPaid(true);
                updatedEvents.add(expense);

                availableFunds = availableFunds.subtract(expense.getAmount());

                logger.debug("Marked expense '{}' ({}) as PAID.", expense.getTitle(), expense.getAmount());
            } else {
                // Pengarna räcker inte till nästa utgift
                break;
            }
        }

        if (!updatedEvents.isEmpty()) {
            eventRepository.saveAll(updatedEvents);
            logger.info("Updated {} events to PAID status.", updatedEvents.size());
        }
    }

    private BigDecimal getTotalPaidExpensesAmount(String username) {
        return eventRepository.findByUsername(username)
                .stream()
                .filter(Event::isExpense)
                .filter(Event::isPaid)
                .map(Event::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void validateUser(String username) {
        if (kvittUserRepository.findByUsername(username).isEmpty()) {
            logger.warn("User not found: {}", username);
            throw new RuntimeException("User not found: " + username);
        }
    }
}