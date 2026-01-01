package utils;

import database.DatabaseConnection;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;

public class InitializeDatabase {
    public static void main(String[] args) {
        initialize();
    }

    public static void initialize() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Check if database is already initialized
            if (isDatabaseInitialized(conn)) {
                System.out.println("[InitializeDatabase] Database already initialized.");
                return;
            }

            System.out.println("[InitializeDatabase] Starting database initialization...");

            // Try to load schema from multiple locations
            BufferedReader br = null;
            
            // 1. Try classpath resource (from JAR)
            InputStream is = InitializeDatabase.class.getResourceAsStream("/data/schema.sql");
            if (is != null) {
                System.out.println("[InitializeDatabase] Loading schema from classpath");
                br = new BufferedReader(new InputStreamReader(is));
            } else {
                // 2. Try file system
                File schemaFile = findSchemaFile();
                if (schemaFile != null && schemaFile.exists()) {
                    System.out.println("[InitializeDatabase] Loading schema from: " + schemaFile.getAbsolutePath());
                    br = new BufferedReader(new FileReader(schemaFile));
                }
            }

            if (br == null) {
                System.err.println("[InitializeDatabase] ERROR: schema.sql not found!");
                return;
            }

            // Execute schema
            try (Statement stmt = conn.createStatement()) {
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
                            stmt.execute(command);
                        }
                        sql.setLength(0);
                    }
                }

                System.out.println("[InitializeDatabase] Schema created successfully!");
            }
            br.close();

            // Now run init_db.sql to add CEO account
            br = null;
            is = InitializeDatabase.class.getResourceAsStream("/data/init_db.sql");
            if (is != null) {
                System.out.println("[InitializeDatabase] Loading initial data from classpath");
                br = new BufferedReader(new InputStreamReader(is));
            } else {
                File initFile = findInitFile();
                if (initFile != null && initFile.exists()) {
                    System.out.println("[InitializeDatabase] Loading initial data from: " + initFile.getAbsolutePath());
                    br = new BufferedReader(new FileReader(initFile));
                }
            }

            if (br != null) {
                try (Statement stmt = conn.createStatement()) {
                    StringBuilder sql = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (line.trim().startsWith("--"))
                            continue;

                        sql.append(line).append("\n");

                        if (line.trim().endsWith(";")) {
                            String command = sql.toString().trim();
                            if (!command.isEmpty()) {
                                stmt.execute(command);
                            }
                            sql.setLength(0);
                        }
                    }

                    System.out.println("[InitializeDatabase] Initial data loaded successfully!");
                }
                br.close();
            }

            System.out.println("[InitializeDatabase] Database initialization completed!");

        } catch (Exception e) {
            System.err.println("[InitializeDatabase] ERROR during initialization:");
            e.printStackTrace();
        }
    }

    private static boolean isDatabaseInitialized(Connection conn) {
        try {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet rs = meta.getTables(null, null, "users", null);
            boolean exists = rs.next();
            rs.close();
            return exists;
        } catch (Exception e) {
            return false;
        }
    }

    private static File findSchemaFile() {
        String[] possiblePaths = {
            "data/schema.sql",
            "../data/schema.sql",
            "app/data/schema.sql",
            "IMS-App/data/schema.sql",
            System.getProperty("user.dir") + "/data/schema.sql",
            System.getProperty("user.dir") + "/app/data/schema.sql"
        };
        
        for (String path : possiblePaths) {
            File f = new File(path);
            if (f.exists()) {
                return f;
            }
        }
        return null;
    }

    private static File findInitFile() {
        String[] possiblePaths = {
            "data/init_db.sql",
            "../data/init_db.sql",
            "app/data/init_db.sql",
            "IMS-App/data/init_db.sql",
            System.getProperty("user.dir") + "/data/init_db.sql",
            System.getProperty("user.dir") + "/app/data/init_db.sql"
        };
        
        for (String path : possiblePaths) {
            File f = new File(path);
            if (f.exists()) {
                return f;
            }
        }
        return null;
    }
}
