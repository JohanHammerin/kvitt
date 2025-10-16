package se.johan.kvitt.kvittUser.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import se.johan.kvitt.kvittUser.model.KvittUser;

@Repository
public interface KvittUserRepository extends ReactiveMongoRepository<KvittUser, String> {
}
