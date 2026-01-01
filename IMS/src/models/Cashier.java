package models;

// Update Cashier class constructor
public class Cashier extends User {
    private int managerId;
    private String requestStatus;

    public Cashier(int id, String username, String password, String name,
                   String phone, String cnic, String status) {
        super(id, username, password, "CASHIER", name, phone, cnic, status);
    }

    // Add this additional constructor if needed
    public Cashier(int id, String username, String password, String role, String name,
                   String phone, String cnic, String status) {
        super(id, username, password, role, name, phone, cnic, status);
    }

    public int getManagerId() {
        return managerId;
    }

    public void setManagerId(int managerId) {
        this.managerId = managerId;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }
}

