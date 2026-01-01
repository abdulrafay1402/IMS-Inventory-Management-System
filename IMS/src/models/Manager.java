package models;

public class Manager extends User {
    private int ceoId;

    public Manager(int id, String username, String password, String name,
                   String phone, String cnic, String status) {
        super(id, username, password, "MANAGER", name, phone, cnic, status);
    }

    public int getCeoId() {
        return ceoId;
    }

    public void setCeoId(int ceoId) {
        this.ceoId = ceoId;
    }
}

