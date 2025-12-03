package com.visitly.assignment.events;

import java.io.Serializable;
import java.time.Instant;

public class UserEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private String eventType;
    private Long userId;
    private String email;
    private Instant timestamp;

    public UserEvent() {}

    public UserEvent(String eventType, Long userId, String email, Instant timestamp) {
        this.eventType = eventType;
        this.userId = userId;
        this.email = email;
        this.timestamp = timestamp;
    }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}