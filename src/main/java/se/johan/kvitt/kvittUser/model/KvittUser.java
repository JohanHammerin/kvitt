package se.johan.kvitt.kvittUser.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document(collection = "kvittCollection")
public class KvittUser {
    @Id
    private String id;
    private String username;
    private String password;
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
}
