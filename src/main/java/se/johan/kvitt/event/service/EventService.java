package se.johan.kvitt.event.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.johan.kvitt.event.dto.request.CreateEventDto;
import se.johan.kvitt.event.dto.request.EditEventDto;
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
import java.util.Optional;

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

    public Event createEvent(CreateEventDto dto) {
        Event savedEvent = eventRepository.save(eventMapper.toEntity(dto));
        logger.info("New Event created & saved: {}", savedEvent.getTitle());

        calculateUnPaidEvents(savedEvent.getUsername());

        return savedEvent;
    }

    public Event editEvent(EditEventDto dto) {
        Optional<Event> editedEvent = eventRepository.findById(dto.id());
        if (editedEvent.isPresent()) {
            Event event = editedEvent.get();
            event.setTitle(dto.title());
            event.setAmount(dto.amount());
            event.setExpense(dto.expense());
            event.setDateTime(dto.dateTime());
            eventRepository.save(event);

            rebootPaidStatus(event.getUsername());
            calculateUnPaidEvents(event.getUsername());
            getKvittStatus(event.getUsername());
            return event;
        }
        return null;
    }

    private void rebootPaidStatus(String username) {
        List<Event> allEvents = eventRepository.findByUsername(username);
        allEvents.stream()
                .filter(Event::isExpense)
                .forEach(event -> event.setPaid(false));

        eventRepository.saveAll(allEvents);
    }

    public void deleteEvent(String id) {
        Optional<Event> eventOptional = eventRepository.findById(id);

        if (eventOptional.isPresent()) {
            String username = eventOptional.get().getUsername();

            eventRepository.deleteById(id);
            rebootPaidStatus(username);
            calculateUnPaidEvents(username);
        }
    }

    public List<EventGetAllEventsByUsernameResponseDTO> getAllEventsByUsername(String username) {

        List<Event> events = eventRepository.findByUsername(username);
        logger.info("Found {} events for user: {}", events.size(), username);

        return events.stream()
                .map(eventMapper::toGetAllEventsByIdDTO)
                .toList();
    }

    public BigDecimal getTotalIncome(String username) {
        return eventRepository.findByUsername(username)
                .stream()
                .filter(event -> !event.isExpense())
                .map(Event::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalExpense(String username) {
        return eventRepository.findByUsername(username)
                .stream()
                .filter(Event::isExpense)
                .map(Event::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getFinancials(String username) {
        BigDecimal totalIncome = getTotalIncome(username);
        BigDecimal totalExpense = getTotalExpense(username);

        return totalIncome.subtract(totalExpense);
    }

    public List<Event> getPaidEvents(String username) {
        return eventRepository.findByUsername(username)
                .stream()
                .filter(Event::isExpense)
                .filter(Event::isPaid)
                .toList();
    }

    public List<Event> getUnPaidEvents(String username) {
        return eventRepository.findByUsername(username)
                .stream()
                .filter(Event::isExpense)
                .filter(event -> !event.isPaid())
                .toList();
    }

    public KvittStatusResponseDTO getKvittStatus(String username) {


        long expensesBack = eventRepository.findByUsername(username).stream()
                .filter(Event::isExpense)
                .filter(event -> !event.isPaid())
                .count();

        LocalDate lastKvittDate = eventRepository.findByUsername(username).stream()
                .filter(Event::isExpense)
                .filter(Event::isPaid)
                .map(event -> event.getDateTime().toLocalDate())
                .max(Comparator.naturalOrder())
                .orElse(LocalDate.now());

        return new KvittStatusResponseDTO(
                expensesBack,
                lastKvittDate
        );
    }

    // --- Privata Hjälpmetoder ---

    private void calculateUnPaidEvents(String username) {
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


}