package se.johan.kvitt.event.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.johan.kvitt.event.dto.request.EventCreateEventRequestDTO;
import se.johan.kvitt.event.dto.response.EventGetAllEventsByUsernameResponseDTO;
import se.johan.kvitt.event.objectMapper.EventMapper;
import se.johan.kvitt.event.model.Event;
import se.johan.kvitt.event.repository.EventRepository;
import se.johan.kvitt.kvittUser.repository.KvittUserRepository;

import java.math.BigDecimal;
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

        List<Event> events = eventRepository.findAllEventsByUsername(username);

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

        return eventRepository.findAllEventsByUsername(username)
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

        return eventRepository.findAllEventsByUsername(username)
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

        return eventRepository.findAllEventsByUsername(username)
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

        return eventRepository.findAllEventsByUsername(username)
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

        BigDecimal availableIncome = getTotalIncome(username);

        List<Event> unpaidExpenses = eventRepository.findAllEventsByUsername(username)
                .stream()
                .filter(Event::isExpense)
                .filter(event -> !event.isPaid())
                .sorted(Comparator.comparing(Event::getDateTime))
                .collect(Collectors.toList());

        List<Event> updatedEvents = new ArrayList<>();

        for (Event expense : unpaidExpenses) {
            if (availableIncome.compareTo(expense.getAmount()) >= 0) {
                expense.setPaid(true);
                updatedEvents.add(expense);
                availableIncome = availableIncome.subtract(expense.getAmount());

                logger.info("Paid event '{}' (created: {}): {}. Remaining income: {}",
                        expense.getTitle(),
                        expense.getDateTime(),
                        expense.getAmount(),
                        availableIncome);
            } else {
                logger.info("Insufficient funds for event '{}'. Needed: {}, Available: {}",
                        expense.getTitle(), expense.getAmount(), availableIncome);
                break;
            }
        }

        if (!updatedEvents.isEmpty()) {
            eventRepository.saveAll(updatedEvents);
            logger.info("Marked {} events as paid for user {} (oldest first)",
                    updatedEvents.size(), username);
        } else {
            logger.info("No events could be paid for user {}. Available income: {}",
                    username, availableIncome);
        }
    }

    private boolean userExists(String username) {
        return kvittUserRepository.findByUsername(username).isPresent();
    }
}