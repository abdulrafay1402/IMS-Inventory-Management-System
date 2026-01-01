package utils;

import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ListTables {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table'")) {

            System.out.println("--- TABLES IN DATABASE ---");
            while (rs.next()) {
                System.out.println(rs.getString("name"));
            }
            System.out.println("--------------------------");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
