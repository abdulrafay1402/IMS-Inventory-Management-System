package models;

import java.util.Date;

// ============= BILL MODEL =============
public class Bill {
    private int id;
    private String billNumber;
    private int cashierId;
    private int managerId;
    private double totalAmount;
    private Date billDate;
    private String status;
    private String cashierName;
    private String managerName;

    public Bill(int id, String billNumber, int cashierId, int managerId,
                double totalAmount, Date billDate, String status,
                String cashierName, String managerName) {
        this.id = id;
        this.billNumber = billNumber;
        this.cashierId = cashierId;
        this.managerId = managerId;
        this.totalAmount = totalAmount;
        this.billDate = billDate;
        this.status = status;
        this.cashierName = cashierName;
        this.managerName = managerName;
    }

    // Getters
    public int getId() { return id; }
    public String getBillNumber() { return billNumber; }
    public int getCashierId() { return cashierId; }
    public int getManagerId() { return managerId; }
    public double getTotalAmount() { return totalAmount; }
    public Date getBillDate() { return billDate; }
    public String getStatus() { return status; }
    public String getCashierName() { return cashierName; }
    public String getManagerName() { return managerName; }
}

