package se.johan.kvitt.event.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "kvittCollection")
public class Event {

    @Id
    private String id;
    private String title;
    private BigDecimal amount;
    private boolean expense;
    private LocalDateTime dateTime;
    boolean paid;
    String kvittUserId;


    public Event(String id, String title, BigDecimal amount, boolean expense, LocalDateTime dateTime, boolean paid, String kvittUserId) {
        this.id = id;
        this.title = title;
        this.amount = amount;
        this.expense = expense;
        this.dateTime = dateTime;
        this.paid = paid;
        this.kvittUserId = kvittUserId;
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

    public String getKvittUserId() {
        return kvittUserId;
    }

    public void setKvittUserId(String kvittUserId) {
        this.kvittUserId = kvittUserId;
    }
}


