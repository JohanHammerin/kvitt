package se.johan.kvitt.event.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import se.johan.kvitt.event.model.Event;

import java.util.List;

@Repository
public interface EventRepository extends MongoRepository<Event, String> {

    List<Event> findByUsername(String username);

    List<Event> findByUsernameAndExpenseTrueAndPaidFalseOrderByDateTimeAsc(String username);
}