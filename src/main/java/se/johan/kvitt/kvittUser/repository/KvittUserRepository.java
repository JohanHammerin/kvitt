package se.johan.kvitt.kvittUser.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import se.johan.kvitt.event.model.Event;
import se.johan.kvitt.kvittUser.model.KvittUser;

import java.util.List;
import java.util.Optional;

@Repository
public interface KvittUserRepository extends MongoRepository<KvittUser, String> {
    Optional<KvittUser> findUserByUsername(String username);
}
