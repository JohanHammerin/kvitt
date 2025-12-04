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
        Event savedEvent = eventRepository.save(eventMapper.toEntity(eventCreateEventRequestDTO));
        logger.info("New Event was created & saved: {}", savedEvent.getTitle());

        // 2. FÖRSÖK BETALA (Kör alltid denna check!)
        // Oavsett om vi lade till pengar eller en ny räkning, så ska vi se om vi kan balansera böckerna.
        calculateUnPaidEvents(savedEvent.getUsername());

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

        // 1. Hämta ALLA utgifter, sorterade från NYASTE till ÄLDSTA
        List<Event> allExpenses = eventRepository.findByUsername(username).stream()
                .filter(Event::isExpense)
                .sorted(Comparator.comparing(Event::getDateTime).reversed())
                .toList();

        // --- HÄR VAR FELET ---
        // Gammal kod: BigDecimal tempFunds = getTotalIncome(username);
        // Vi använde bruttoinkomsten, men vi måste använda SALDOT (Inkomst - Utgifter).
        // Men vänta! Logiken i KvittStatus är lite speciell.
        // Vi vill se hur många av de *senaste* utgifterna som täcks av *all* inkomst.

        // RÄTT TÄNK:
        // Vi har en total pott pengar in (Total Income).
        // Vi har en lista på alla utgifter någonsin.
        // Vi vill se: Om vi betalar utgifterna baklänges (nyaste först), när tar pengarna slut?

        BigDecimal availableFunds = getTotalIncome(username);

        // MEN! I din app verkar "Saldo" vara det som är kvar.
        // Om du har 1 miljon in, och 500k ut, så är saldot 500k.
        // De 500k som är "Utgift" är ju redan förbrukade pengar.

        // Så frågan är: Är de röda "Obetalda" utgifterna i din lista verkligen obetalda?
        // I din logik markerar du dem som 'paid=true' om de täcks.

        // Låt oss titta på din screenshot:
        // Total Inkomst: 1 000 050
        // Total Utgift: 501 025
        // Saldo: 499 025

        // Du har en utgift "testing" på 1000 kr som är "Obetald".
        // Varför markerades den inte som betald när du skapade den?
        // Jo, för att du hade buggen i 'calculateUnPaidEvents' då!

        // NU när du har fixat 'calculateUnPaidEvents' (som körs vid create),
        // så kommer *nya* utgifter bli rätt.
        // Men 'getKvittStatus' försöker räkna ut statusen dynamiskt.

        // Låt oss använda samma logik som i calculateUnPaidEvents för konsekvens:
        // Vi ska se om vi har råd med alla utgifter totalt sett.

        BigDecimal totalIncome = getTotalIncome(username);
        BigDecimal totalExpenses = getTotalExpense(username);

        // Om Inkomst >= Utgifter, då är vi KVITT!
        if (totalIncome.compareTo(totalExpenses) >= 0) {
            return new KvittStatusResponseDTO(
                    0L, // 0 utgifter back
                    LocalDate.now() // Idag
            );
        }

        // Om vi kommer hit så är Inkomst < Utgifter (Vi är back på riktigt).
        // Då kör vi loopen för att se HUR många utgifter vi är back.

        BigDecimal tempFunds = totalIncome;
        int coveredCount = 0;
        LocalDate lastKvittDate = LocalDate.now();

        // Eftersom vi vet att pengarna INTE räcker till allt (vi kollade ovan),
        // så kommer denna loop garanterat att breaka någonstans.
        // Men vi sorterar ÄLDSTA först nu för att se hur långt pengarna räcker "normalt".
        // ELLER vill du ha logiken "Nyaste först"?
        // Din "back"-logik brukar vara: "Du har inte råd med de senaste X sakerna".

        // Låt oss behålla din "Nyaste först"-loop men bara köra den om vi faktiskt är back totalt.

        for (Event expense : allExpenses) {
            if (tempFunds.compareTo(expense.getAmount()) >= 0) {
                tempFunds = tempFunds.subtract(expense.getAmount());
                coveredCount++;
                lastKvittDate = expense.getDateTime().toLocalDate();
            } else {
                break;
            }
        }

        long expensesBack = allExpenses.size() - coveredCount;

        return new KvittStatusResponseDTO(
                expensesBack,
                lastKvittDate
        );
    }

    private boolean userExists(String username) {
        return kvittUserRepository.findByUsername(username).isPresent();
    }
}