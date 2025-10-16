package se.johan.kvitt.event.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;
import se.johan.kvitt.event.dto.request.EventCreationRequestDTO;
import se.johan.kvitt.event.objectMapper.EventMapper;
import se.johan.kvitt.event.model.Event;
import se.johan.kvitt.event.repository.EventRepository;

@Service
@RequestMapping("/event")
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public EventService(EventRepository eventRepository, EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
    }

    public Mono<Event> createEvent(EventCreationRequestDTO eventCreationRequestDTO) {

        logger.info("New Message was created & saved");

        return eventRepository.save(eventMapper.toEntity(eventCreationRequestDTO));
    }
}
