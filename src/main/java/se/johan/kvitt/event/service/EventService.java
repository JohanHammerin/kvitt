package se.johan.kvitt.event.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.johan.kvitt.event.dto.request.EventCreateEventRequestDTO;
import se.johan.kvitt.event.dto.response.EventGetAllEventsByUsernameResponseDTO;
import se.johan.kvitt.event.dto.response.KvittStatusResponseDTO;
import se.johan.kvitt.event.objectMapper.EventMapper;
import se.johan.kvitt.event.model.Event;
import se.johan.kvitt.event.repository.EventRepository;
import se.johan.kvitt.kvittUser.repository.KvittUserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
        logger.info("New Event was created & saved");
        if(!eventCreateEventRequestDTO.expense()) {
            calculateUnPaidEvents(eventCreateEventRequestDTO.username());
        }
        return eventRepository.save(eventMapper.toEntity(eventCreateEventRequestDTO));
    }

    public List<EventGetAllEventsByUsernameResponseDTO> getAllEventsByUsername(String username) {
        logger.info("{} requested all events", username);

        // Kontrollera först om användaren finns
        if (!userExists(username)) {
            logger.warn("User not found: {}", username);
            throw new RuntimeException("User not found: " + username);
        }

        List<Event> events = eventRepository.findByUsername(username);

        if (events.isEmpty()) {
            logger.info("No events found for user: {}", username);
        } else {
            logger.info("Found {} events for user: {}", events.size(), username);
        }

        return events.stream()
                .map(eventMapper::toGetAllEventsByIdDTO)
                .toList();
    }

    public BigDecimal getTotalIncome(String username) {
        // Kontrollera först om användaren finns
        if (!userExists(username)) {
            throw new RuntimeException("User not found: " + username);
        }

        return eventRepository.findByUsername(username)
                .stream()
                .filter(event -> !event.isExpense())
                .map(Event::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalExpense(String username) {
        // Kontrollera först om användaren finns
        if (!userExists(username)) {
            throw new RuntimeException("User not found: " + username);
        }

        return eventRepository.findByUsername(username)
                .stream()
                .filter(Event::isExpense)
                .map(Event::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getFinancials(String username) {
        // Kontrollera först om användaren finns
        if (!userExists(username)) {
            throw new RuntimeException("User not found: " + username);
        }

        BigDecimal totalIncome = getTotalIncome(username);
        BigDecimal totalExpense = getTotalExpense(username);

        return totalIncome.subtract(totalExpense);
    }

    public List<Event> paidEvents(String username) {
        // Kontrollera först om användaren finns
        if (!userExists(username)) {
            throw new RuntimeException("User not found: " + username);
        }

        return eventRepository.findByUsername(username)
                .stream()
                .filter(Event::isExpense)
                .filter(Event::isPaid)
                .toList();
    }

    public List<Event> unPaidEvents(String username) {
        // Kontrollera först om användaren finns
        if (!userExists(username)) {
            throw new RuntimeException("User not found: " + username);
        }

        return eventRepository.findByUsername(username)
                .stream()
                .filter(Event::isExpense)
                .filter(event -> !event.isPaid())
                .toList();
    }

    private void calculateUnPaidEvents(String username) {
        // Kontrollera först om användaren finns
        if (!userExists(username)) {
            throw new RuntimeException("User not found: " + username);
        }

        // Hämta ALLA inkomster för att ha en budget
        BigDecimal availableIncome = getTotalIncome(username);

        // Hämta nu OCH filtrera i databasen:
        // Hämta Utgifter (Expense=true) som inte är Betalda (Paid=false), sorterade ÄLDSTA FÖRST!
        List<Event> unpaidExpenses = eventRepository.findByUsernameAndExpenseTrueAndPaidFalseOrderByDateTimeAsc(username);

        logger.info("Starting calculateUnPaidEvents. Found {} DB-filtered unpaid expenses. Available income: {}",
                unpaidExpenses.size(), availableIncome);

        List<Event> updatedEvents = new ArrayList<>();
        int expensesPaidCount = 0; // Spåra antalet betalda utgifter

        for (Event expense : unpaidExpenses) {
            // Vi använder >= 0 för att tillåta saldot att bli exakt 0 efter betalning.
            if (availableIncome.compareTo(expense.getAmount()) >= 0) {

                expense.setPaid(true);
                updatedEvents.add(expense);
                availableIncome = availableIncome.subtract(expense.getAmount());
                expensesPaidCount++;

                logger.info("✅ MARKED AS PAID (#{}): '{}' (ID: {}, Amount: {}). Remaining income: {}",
                        expensesPaidCount,
                        expense.getTitle(),
                        expense.getId(),
                        expense.getAmount(),
                        availableIncome);

            } else {
                logger.warn("⚠️ INSUFFICIENT FUNDS for event '{}' (ID: {}). Needed: {}, Available: {}",
                        expense.getTitle(), expense.getId(), expense.getAmount(), availableIncome);
                break;
            }
        }

        if (!updatedEvents.isEmpty()) {
            // Detta borde nu spara de 2 eventen korrekt om availableIncome var 200 kr
            eventRepository.saveAll(updatedEvents);
            logger.info("🎉 SUCCESS: Marked {} events as paid (oldest first) and SAVED to database.",
                    updatedEvents.size());
        } else {
            logger.info("No events could be paid for user {}. Available income: {}",
                    username, availableIncome);
        }
    }

    // --- KORRIGERAD METOD FÖR KVITT STATUS (Lösningen som fungerar) ---
    public KvittStatusResponseDTO getKvittStatus(String username) {
        // Kontrollera om användaren finns
        if (!userExists(username)) {
            throw new RuntimeException("User not found: " + username);
        }

        // 1. Hämta ALLA utgifter, sorterade från NYASTE till ÄLDSTA (DESCENDING)
        List<Event> allExpenses = eventRepository.findByUsername(username).stream()
                .filter(Event::isExpense)
                .sorted(Comparator.comparing(Event::getDateTime).reversed()) // Nyaste först
                .toList();

        BigDecimal tempFunds = getTotalIncome(username);
        int coveredCount = 0;
        LocalDate lastKvittDate = LocalDate.now();

        // 2. Iterera genom de NYASTE utgifterna och simulera betalning för att hitta brytpunkten
        for (Event expense : allExpenses) {
            if (tempFunds.compareTo(expense.getAmount()) >= 0) {
                tempFunds = tempFunds.subtract(expense.getAmount());
                coveredCount++;
                // Spara datumet för den äldsta utgiften som täcktes i denna simulering
                lastKvittDate = expense.getDateTime().toLocalDate();
            } else {
                // BRYT: Fonderna har tagit slut. Alla återstående utgifter kan inte täckas.
                break;
            }
        }

        // Antal utgifter man är back = Totala utgifter - Antal täckta utgifter
        long expensesBack = allExpenses.size() - coveredCount;

        // Om alla utgifter täcktes, ska lastKvittDate vara idag (startvärdet).
        if (expensesBack == 0) {
            lastKvittDate = LocalDate.now();
        }

        return new KvittStatusResponseDTO(
                expensesBack,
                lastKvittDate
        );
    }


    private boolean userExists(String username) {
        return kvittUserRepository.findByUsername(username).isPresent();
    }
}