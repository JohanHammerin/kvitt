package se.johan.kvitt.kvittUser.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import se.johan.kvitt.kvittUser.model.KvittUser;

import java.util.Optional;

@Repository
public interface KvittUserRepository extends MongoRepository<KvittUser, String> {
    // Fungerar perfekt - söker nu bara i "users" collection
    Optional<KvittUser> findByUsername(String username);
}
