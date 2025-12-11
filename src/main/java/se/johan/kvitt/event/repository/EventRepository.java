package se.johan.kvitt.event.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import se.johan.kvitt.event.model.Event;

import java.util.List;

@Repository
public interface EventRepository extends MongoRepository<Event, String> {

    // Används för att hämta ALLA events för en användare (bra för getTotalX etc.)
    List<Event> findByUsername(String username);


    // NY METOD: Hämta utgifter (expense=true) som inte är betalda (paid=false)
    List<Event> findByUsernameAndExpenseTrueAndPaidFalseOrderByDateTimeAsc(String username);
}