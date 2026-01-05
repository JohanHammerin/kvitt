package se.johan.kvitt.event.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "events")
public class Event {

    @Id
    private String id;
    private String title;
    private BigDecimal amount;
    private boolean expense;
    private LocalDateTime dateTime;
    private boolean paid;
    private String username;

    public Event() {
    }

    public Event(String id, String title, BigDecimal amount, boolean expense, LocalDateTime dateTime, boolean paid, String username) {
        this.id = id;
        this.title = title;
        this.amount = amount;
        this.expense = expense;
        this.dateTime = dateTime;
        this.paid = paid;
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public boolean isExpense() {
        return expense;
    }

    public void setExpense(boolean expense) {
        this.expense = expense;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}