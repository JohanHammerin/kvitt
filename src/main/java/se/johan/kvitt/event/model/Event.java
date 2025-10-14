package se.johan.kvitt.event.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
@Document(collection = "kvittCollection")
public class Event {
    @Id
    private String id;
    private String title;
    private double amount;
    private boolean expense;
    private LocalDateTime dateTime;
    private String userId;
}
