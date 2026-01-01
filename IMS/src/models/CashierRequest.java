package models;

// ============= CASHIER REQUEST MODEL =============
public class CashierRequest {
    private int id;
    private int managerId;
    private int cashierId;
    private String cashierName;
    private String username;
    private String phone;
    private String cnic;
    private String managerName;
    private String status;
    private java.util.Date requestDate;

    public CashierRequest(int id, int managerId, int cashierId, String cashierName,
                          String username, String phone, String cnic, String managerName,
                          String status, java.util.Date requestDate) {
        this.id = id;
        this.managerId = managerId;
        this.cashierId = cashierId;
        this.cashierName = cashierName;
        this.username = username;
        this.phone = phone;
        this.cnic = cnic;
        this.managerName = managerName;
        this.status = status;
        this.requestDate = requestDate;
    }

    // Getters
    public int getId() { return id; }
    public int getManagerId() { return managerId; }
    public int getCashierId() { return cashierId; }
    public String getCashierName() { return cashierName; }
    public String getUsername() { return username; }
    public String getPhone() { return phone; }
    public String getCnic() { return cnic; }
    public String getManagerName() { return managerName; }
    public String getStatus() { return status; }
    public java.util.Date getRequestDate() { return requestDate; }
}

