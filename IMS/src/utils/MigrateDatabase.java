package utils;

import database.DatabaseConnection;
import java.sql.*;

public class MigrateDatabase {
    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            
            // Check if joining_date column exists
            ResultSet rs = stmt.executeQuery("PRAGMA table_info(users)");
            boolean hasJoiningDate = false;
            
            while (rs.next()) {
                String columnName = rs.getString("name");
                if ("joining_date".equals(columnName)) {
                    hasJoiningDate = true;
                    break;
                }
            }
            rs.close();
            
            if (!hasJoiningDate) {
                System.out.println("Adding joining_date column to users table...");
                stmt.executeUpdate("ALTER TABLE users ADD COLUMN joining_date DATE");
                System.out.println("Successfully added joining_date column!");
                
                // Update existing users with today's date
                stmt.executeUpdate("UPDATE users SET joining_date = date('now') WHERE joining_date IS NULL");
                System.out.println("Updated existing users with current date!");
            } else {
                System.out.println("joining_date column already exists!");
            }
            
            // Verify the column
            System.out.println("\nVerifying users table structure:");
            rs = stmt.executeQuery("PRAGMA table_info(users)");
            while (rs.next()) {
                System.out.println("  - " + rs.getString("name") + " (" + rs.getString("type") + ")");
            }
            rs.close();
            
            System.out.println("\nMigration completed successfully!");
            
        } catch (SQLException e) {
            System.err.println("Migration failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (stmt != null) {
                try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
}
