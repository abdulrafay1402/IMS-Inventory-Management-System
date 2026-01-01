package database;

import java.sql.*;
import java.io.File;

// ============= DATABASE CONNECTION - SQLite =============
public class DatabaseConnection {
    // SQLite database file path (relative to where application runs)
    // Will be created in: ./data/inventory_db.sqlite
    // Dynamic database path resolution
    private static String getDatabasePath() {
        // Try multiple paths for compatibility (IDE vs jpackage)
        String[] possiblePaths = {
            "data/inventory_db.sqlite",                    // IDE
            "app/data/inventory_db.sqlite",                // jpackage
            "../data/inventory_db.sqlite",                 // From app subfolder
            System.getProperty("user.dir") + "/data/inventory_db.sqlite",
            System.getProperty("user.dir") + "/app/data/inventory_db.sqlite"
        };
        
        for (String path : possiblePaths) {
            File f = new File(path);
            if (f.exists()) {
                System.out.println("[DatabaseConnection] Found database at: " + f.getAbsolutePath());
                return f.getAbsolutePath();
            }
        }
        
        // Default to app/data for jpackage environment if not found
        File appDataDir = new File(System.getProperty("user.dir"), "app/data");
        if (appDataDir.exists() || (appDataDir.getParentFile() != null && appDataDir.getParentFile().getName().equals("app"))) {
            return appDataDir.getAbsolutePath() + File.separator + "inventory_db.sqlite";
        }
        
        // Fallback to standard data directory
        return new File(System.getProperty("user.dir"), "data/inventory_db.sqlite").getAbsolutePath();
    }

    private static final String URL_PREFIX = "jdbc:sqlite:";

    public static Connection getConnection() throws SQLException {
        try {
            // Get dynamic database path
            String dbPath = getDatabasePath();
            System.out.println("[DatabaseConnection] Using database at: " + dbPath);
            
            // Ensure database directory exists
            File dbFile = new File(dbPath);
            File dbDir = dbFile.getParentFile();
            if (dbDir != null && !dbDir.exists()) {
                dbDir.mkdirs();
            }
            
            // Load SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
            
            // Create connection (SQLite will create file if it doesn't exist)
            Connection conn = DriverManager.getConnection(URL_PREFIX + dbPath);
            
            // Enable foreign keys (SQLite requires this to be enabled)
            conn.createStatement().execute("PRAGMA foreign_keys = ON");
            
            return conn;
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC Driver not found. Please add sqlite-jdbc.jar to your classpath.", e);
        }
    }

    public static void closeResources(ResultSet rs, PreparedStatement ps, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

