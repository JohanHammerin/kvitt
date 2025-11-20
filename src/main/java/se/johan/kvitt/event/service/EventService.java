package se.johan.kvitt.event.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.johan.kvitt.event.dto.request.EventCreateEventRequestDTO;
import se.johan.kvitt.event.dto.response.EventGetAllEventsByIdResponseDTO;
import se.johan.kvitt.event.objectMapper.EventMapper;
import se.johan.kvitt.event.model.Event;
import se.johan.kvitt.event.repository.EventRepository;

import java.math.BigDecimal;
import java.util.List;

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
                .filter(e -> !e.expense())
                .map(Event::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    public BigDecimal getTotalExpense(String kvittUserId) {
        return eventRepository.findAllEventsByKvittUserId(kvittUserId)
                .stream()
                .filter(Event::expense)
                .map(Event::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add); // summerar
    }

    public BigDecimal getFinancials(String kvittUserId) {
        BigDecimal totalIncome = getTotalIncome(kvittUserId);
        BigDecimal totalExpense = getTotalExpense(kvittUserId);

        return totalIncome.subtract(totalExpense);
    }





}
