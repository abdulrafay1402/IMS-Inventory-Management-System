package models;

import java.time.LocalDateTime;

public class SalaryPayment {
    private int id;
    private int userId;
    private String userName;
    private String userRole;
    private double amount;
    private String paymentMonth; // Format: 'YYYY-MM'
    private LocalDateTime paymentDate;
    private String status;
    private String notes;
    private int createdBy;

    public SalaryPayment(int id, int userId, String userName, String userRole, double amount,
                        String paymentMonth, LocalDateTime paymentDate, String status,
                        String notes, int createdBy) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.userRole = userRole;
        this.amount = amount;
        this.paymentMonth = paymentMonth;
        this.paymentDate = paymentDate;
        this.status = status;
        this.notes = notes;
        this.createdBy = createdBy;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserRole() {
        return userRole;
    }

    public double getAmount() {
        return amount;
    }

    public String getPaymentMonth() {
        return paymentMonth;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public String getStatus() {
        return status;
    }

    public String getNotes() {
        return notes;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    // Setters
    public void setStatus(String status) {
        this.status = status;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
