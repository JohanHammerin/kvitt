package se.johan.kvitt.event.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import se.johan.kvitt.event.model.Event;

@Repository
public interface EventRepository extends ReactiveMongoRepository<Event, String> {

}
