package models;

import java.util.Date;

// ============= EXPENSE MODEL =============
public class Expense {
    private int id;
    private int managerId;
    private String description;
    private double amount;
    private String category;
    private String managerName;
    private Date expenseDate;
    private Date recordedDate;

    public Expense(int id, int managerId, String description, double amount,
                   String category, String managerName, Date expenseDate, Date recordedDate) {
        this.id = id;
        this.managerId = managerId;
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.managerName = managerName;
        this.expenseDate = expenseDate;
        this.recordedDate = recordedDate;
    }

    // Getters
    public int getId() { return id; }
    public int getManagerId() { return managerId; }
    public String getDescription() { return description; }
    public double getAmount() { return amount; }
    public String getCategory() { return category; }
    public String getManagerName() { return managerName; }
    public Date getExpenseDate() { return expenseDate; }
    public Date getRecordedDate() { return recordedDate; }
}

