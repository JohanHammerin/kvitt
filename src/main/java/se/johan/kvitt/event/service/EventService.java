package se.johan.kvitt.event.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.johan.kvitt.event.dto.request.EventCreateEventRequestDTO;
import se.johan.kvitt.event.dto.response.EventGetAllEventsByIdResponseDTO;
import se.johan.kvitt.event.objectMapper.EventMapper;
import se.johan.kvitt.event.model.Event;
import se.johan.kvitt.event.repository.EventRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public EventService(EventRepository eventRepository, EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
    }

    public Event createEvent(EventCreateEventRequestDTO eventCreateEventRequestDTO) {
        logger.info("New Event was created & saved");
        return eventRepository.save(eventMapper.toEntity(eventCreateEventRequestDTO));
    }

    public List<EventGetAllEventsByIdResponseDTO> getAllEventsById(String kvittUserId) {
        logger.info("{} requested all events", kvittUserId);

        return eventRepository.findAllEventsByKvittUserId(kvittUserId)
                .stream()
                .map(eventMapper::toGetAllEventsByIdDTO)
                .toList();
    }


    public BigDecimal getTotalIncome(String kvittUserId) {
        return eventRepository.findAllEventsByKvittUserId(kvittUserId)
                .stream()
                .filter(event -> !event.isExpense())
                .map(Event::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    public BigDecimal getTotalExpense(String kvittUserId) {
        return eventRepository.findAllEventsByKvittUserId(kvittUserId)
                .stream()
                .filter(Event::isExpense)
                .map(Event::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add); // summerar
    }

    public BigDecimal getFinancials(String kvittUserId) {
        BigDecimal totalIncome = getTotalIncome(kvittUserId);
        BigDecimal totalExpense = getTotalExpense(kvittUserId);

        return totalIncome.subtract(totalExpense);
    }


    public List<Event> paidEvents (String kvittUserId) {
        return eventRepository.findAllEventsByKvittUserId(kvittUserId)
                .stream()
                .filter(Event::isExpense)
                .filter(Event::isPaid)
                .toList();
    }

    public List<Event> unPaidEvents (String kvittUserId) {
        return eventRepository.findAllEventsByKvittUserId(kvittUserId)
                .stream()
                .filter(Event::isExpense)
                .filter(event -> !event.isPaid())
                .toList();
    }

    @Transactional
    public void calculateUnPaidEvents(String kvittUserId) {
        BigDecimal availableIncome = getTotalIncome(kvittUserId);

        // Hämta och sortera efter äldsta först
        List<Event> unpaidExpenses = eventRepository.findAllEventsByKvittUserId(kvittUserId)
                .stream()
                .filter(Event::isExpense)
                .filter(event -> !event.isPaid())
                .sorted(Comparator.comparing(Event::getDateTime)) // Äldsta först
                .collect(Collectors.toList());

        List<Event> updatedEvents = new ArrayList<>();

        for (Event expense : unpaidExpenses) {
            if (availableIncome.compareTo(expense.getAmount()) >= 0) {
                // Betala denna utgift
                expense.setPaid(true);
                updatedEvents.add(expense);
                availableIncome = availableIncome.subtract(expense.getAmount());

                logger.info("Paid event '{}' (created: {}): {}. Remaining income: {}",
                        expense.getTitle(),
                        expense.getDateTime(),
                        expense.getAmount(),
                        availableIncome);
            } else {
                // Inga fler pengar kvar
                logger.info("Insufficient funds for event '{}'. Needed: {}, Available: {}",
                        expense.getTitle(), expense.getAmount(), availableIncome);
                break;
            }
        }

        if (!updatedEvents.isEmpty()) {
            eventRepository.saveAll(updatedEvents);
            logger.info("Marked {} events as paid for user {} (oldest first)",
                    updatedEvents.size(), kvittUserId);
        } else {
            logger.info("No events could be paid for user {}. Available income: {}",
                    kvittUserId, availableIncome);
        }
    }

}
