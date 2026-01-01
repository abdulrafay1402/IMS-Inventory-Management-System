package models;

public class CEO extends User {
    public CEO(int id, String username, String password, String name,
               String phone, String cnic, String status) {
        super(id, username, password, "CEO", name, phone, cnic, status);
    }
}

