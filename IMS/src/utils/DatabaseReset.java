package utils;

import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseReset {
    public static void main(String[] args) {
        resetDatabase();
    }

    public static void resetDatabase() {
        System.out.println("Starting database reset...");
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            Statement stmt = conn.createStatement();

            // Clear tables (SQLite uses DELETE FROM instead of TRUNCATE)
            String[] tables = {
                "bill_items", "bills", "expenses", 
                "manager_inventory", "cashier_manager", 
                "manager_ceo", "ceo_inventory"
            };

            for (String table : tables) {
                System.out.println("Clearing table: " + table);
                stmt.executeUpdate("DELETE FROM " + table);
                // Reset auto-increment
                stmt.executeUpdate("DELETE FROM sqlite_sequence WHERE name='" + table + "'");
            }

            // Handle Users
            System.out.println("Cleaning users table...");
            stmt.executeUpdate("DELETE FROM users WHERE role != 'CEO'");

            // Check if CEO exists
            boolean ceoExists = conn.createStatement().executeQuery("SELECT count(*) FROM users WHERE role = 'CEO'").getInt(1) > 0;

            if (ceoExists) {
                System.out.println("Updating CEO user...");
                String updateSQL = "UPDATE users SET " +
                        "username = 'ceo', " +
                        "password = 'ceo123', " +
                        "name = 'System Administrator', " +
                        "phone = '03001234567', " +
                        "cnic = '1234567890123', " +
                        "status = 'ACTIVE' " +
                        "WHERE role = 'CEO'";
                stmt.executeUpdate(updateSQL);
            } else {
                System.out.println("Creating CEO user...");
                String insertSQL = "INSERT INTO users (username, password, role, name, phone, cnic, status) " +
                        "VALUES ('ceo', 'ceo123', 'CEO', 'System Administrator', '03001234567', '1234567890123', 'ACTIVE')";
                stmt.executeUpdate(insertSQL);
            }

            conn.commit();
            System.out.println("Database reset completed successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
