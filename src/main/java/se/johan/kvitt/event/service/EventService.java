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
        // 1. SPARA FÖRST
        // Vi måste spara eventet i databasen först så att getTotalIncome ser den nya summan.
        Event savedEvent = eventRepository.save(eventMapper.toEntity(eventCreateEventRequestDTO));
        logger.info("New Event was created & saved: {}", savedEvent.getTitle());

        // 2. BERÄKNA SEN
        // Om det var en inkomst (inte en utgift), kolla om vi kan betala gamla skulder.
        if (!savedEvent.isExpense()) {
            calculateUnPaidEvents(savedEvent.getUsername());
        }

        return savedEvent;
    }

    public List<EventGetAllEventsByUsernameResponseDTO> getAllEventsByUsername(String username) {
        logger.info("{} requested all events", username);

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
        if (!userExists(username)) {
            throw new RuntimeException("User not found: " + username);
        }

        BigDecimal totalIncome = getTotalIncome(username);
        BigDecimal totalExpense = getTotalExpense(username);

        return totalIncome.subtract(totalExpense);
    }

    public List<Event> paidEvents(String username) {
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
        if (!userExists(username)) {
            throw new RuntimeException("User not found: " + username);
        }

        return eventRepository.findByUsername(username)
                .stream()
                .filter(Event::isExpense)
                .filter(event -> !event.isPaid())
                .toList();
    }

    /**
     * Hjälpmetod för att beräkna summan av alla utgifter som redan är betalda.
     * Detta behövs för att veta hur mycket av inkomsten som är "låst".
     */
    private BigDecimal getTotalPaidExpensesAmount(String username) {
        return eventRepository.findByUsername(username)
                .stream()
                .filter(Event::isExpense)
                .filter(Event::isPaid)
                .map(Event::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void calculateUnPaidEvents(String username) {
        if (!userExists(username)) {
            throw new RuntimeException("User not found: " + username);
        }

        // 1. Hämta TOTAL inkomst (Inkluderar den nyss insatta inkomsten)
        BigDecimal totalIncome = getTotalIncome(username);

        // 2. Hämta summan av utgifter som REDAN är betalda
        BigDecimal alreadyPaidAmount = getTotalPaidExpensesAmount(username);

        // 3. Beräkna tillgängliga medel (Inkomst - Redan betalt)
        BigDecimal availableFunds = totalIncome.subtract(alreadyPaidAmount);

        // 4. Hämta obetalda utgifter, äldsta först
        List<Event> unpaidExpenses = eventRepository.findByUsernameAndExpenseTrueAndPaidFalseOrderByDateTimeAsc(username);

        logger.info("Starting calculation. Total Income: {}, Already Paid: {}, Available Funds: {}",
                totalIncome, alreadyPaidAmount, availableFunds);

        List<Event> updatedEvents = new ArrayList<>();
        int expensesPaidCount = 0;

        for (Event expense : unpaidExpenses) {
            // Kontrollera om vi har råd att betala denna utgift med våra tillgängliga medel
            if (availableFunds.compareTo(expense.getAmount()) >= 0) {

                expense.setPaid(true);
                updatedEvents.add(expense);

                // Minska potten med tillgängliga pengar
                availableFunds = availableFunds.subtract(expense.getAmount());
                expensesPaidCount++;

                logger.info("MARKED AS PAID (#{}): '{}' (ID: {}, Amount: {}). Remaining funds: {}",
                        expensesPaidCount,
                        expense.getTitle(),
                        expense.getId(),
                        expense.getAmount(),
                        availableFunds);

            } else {
                logger.warn("INSUFFICIENT FUNDS for event '{}' (ID: {}). Needed: {}, Available: {}",
                        expense.getTitle(), expense.getId(), expense.getAmount(), availableFunds);
                break; // Sluta loopen om pengarna är slut
            }
        }

        if (!updatedEvents.isEmpty()) {
            eventRepository.saveAll(updatedEvents);
            logger.info("SUCCESS: Marked {} events as paid (oldest first) and SAVED to database.",
                    updatedEvents.size());
        } else {
            logger.info("No new events could be paid for user {}. Available funds: {}",
                    username, availableFunds);
        }
    }

    public KvittStatusResponseDTO getKvittStatus(String username) {
        if (!userExists(username)) {
            throw new RuntimeException("User not found: " + username);
        }

        // Hämta alla utgifter
        List<Event> allExpenses = eventRepository.findByUsername(username).stream()
                .filter(Event::isExpense)
                .toList();

        // 1. RÄKNA: Hur många är INTE betalda?
        // Detta fixar felet så att den visar 2 istället för 4.
        long expensesBack = allExpenses.stream()
                .filter(event -> !event.isPaid())
                .count();

        // 2. DATUM: Hitta datumet för den SENASTE BETALDA utgiften
        // (För texten "Senaste utgift som täcktes")
        LocalDate lastKvittDate = allExpenses.stream()
                .filter(Event::isPaid)
                .map(e -> e.getDateTime().toLocalDate())
                .max(Comparator.naturalOrder()) // Hitta nyaste datumet
                .orElse(LocalDate.now());       // Om inget är betalt alls, visa idag

        return new KvittStatusResponseDTO(
                expensesBack,
                lastKvittDate
        );
    }

    private boolean userExists(String username) {
        return kvittUserRepository.findByUsername(username).isPresent();
    }
}