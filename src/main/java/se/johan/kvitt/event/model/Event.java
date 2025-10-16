package se.johan.kvitt.event.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
@Document(collection = "kvittCollection")
public record Event(
    @Id
    String id,
    String title,
    double amount,
    boolean expense,
    LocalDateTime dateTime,
    String kvittUserId
)
{}


