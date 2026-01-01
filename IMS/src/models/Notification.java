package models;

import java.time.LocalDateTime;

public class Notification {
    private int id;
    private int userId;
    private String type;  // CASHIER_REQUEST, CASHIER_APPROVED, BILL_CREATED, MONTHLY_REPORT
    private String title;
    private String message;
    private String relatedId;  // cashier_id, bill_number, etc.
    private boolean isRead;
    private LocalDateTime createdAt;

    public Notification() {}

    public Notification(int userId, String type, String title, String message, String relatedId) {
        this.userId = userId;
        this.type = type;
        this.title = title;
        this.message = message;
        this.relatedId = relatedId;
        this.isRead = false;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRelatedId() {
        return relatedId;
    }

    public void setRelatedId(String relatedId) {
        this.relatedId = relatedId;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
