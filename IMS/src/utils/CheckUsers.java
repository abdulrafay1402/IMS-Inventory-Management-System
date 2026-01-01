package utils;

import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class CheckUsers {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM users")) {

            System.out.println("--- USERS TABLE CONTENT ---");
            boolean hasUsers = false;
            while (rs.next()) {
                hasUsers = true;
                System.out.printf("ID: %d | User: %s | Pass: %s | Role: %s | Status: %s%n",
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("status"));
            }
            if (!hasUsers) {
                System.out.println("No users found in the database.");
            }
            System.out.println("---------------------------");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
