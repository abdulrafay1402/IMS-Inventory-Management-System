package models;

import java.time.LocalDateTime;

// ============= USER MODELS =============
public abstract class User {
    protected int id;
    protected String username;
    protected String password;
    protected String role;
    protected String name;
    protected String phone;
    protected String cnic;
    protected double salary;
    protected String status;
    protected LocalDateTime createdDate;
    protected LocalDateTime lastUpdated;

    public User(int id, String username, String password, String role, String name,
                String phone, String cnic, String status) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.name = name;
        this.phone = phone;
        this.cnic = cnic;
        this.salary = 0.0;
        this.status = status;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getCnic() {
        return cnic;
    }

    public double getSalary() {
        return salary;
    }

    public String getStatus() {
        return status;
    }

    // Setters (only for editable fields)
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }
}

