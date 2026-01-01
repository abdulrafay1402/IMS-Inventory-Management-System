package utils;

import database.DatabaseConnection;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.Statement;

public class InitializeDatabase {
    public static void main(String[] args) {
        initialize();
    }

    public static void initialize() {
        System.out.println("Starting database initialization...");

        // Find schema.sql
        File schemaFile = new File("data/schema.sql");
        if (!schemaFile.exists()) {
            // Try relative path
            schemaFile = new File("../data/schema.sql");
        }

        if (!schemaFile.exists()) {
            System.err.println("Error: schema.sql not found!");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                BufferedReader br = new BufferedReader(new FileReader(schemaFile))) {

            conn.setAutoCommit(false);

            StringBuilder sql = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                // Strip comments
                if (line.trim().startsWith("--"))
                    continue;

                sql.append(line).append("\n");

                if (line.trim().endsWith(";")) {
                    String command = sql.toString().trim();
                    if (!command.isEmpty()) {
                        System.out
                                .println("Executing: " + command.substring(0, Math.min(command.length(), 50)) + "...");
                        stmt.execute(command);
                    }
                    sql.setLength(0);
                }
            }

            conn.commit();
            System.out.println("Database initialization completed successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
