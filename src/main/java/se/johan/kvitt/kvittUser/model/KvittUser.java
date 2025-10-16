package se.johan.kvitt.kvittUser.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document(collection = "kvittCollection")
public record KvittUser(
        @Id
        String id,
        String username,
        String password,
        double totalIncome,
        double totalExpenses
) {
}
