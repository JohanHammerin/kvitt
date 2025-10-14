package se.johan.kvitt.user.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document(collection = "kvittCollection")
public class User {
    @Id
    private String id;
    private String username;
    private String password;
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
}
