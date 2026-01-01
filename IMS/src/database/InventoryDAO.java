package database;

import models.CEOInventory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// ============= INVENTORY DATA ACCESS LAYER =============
public class InventoryDAO {

    // Add product to CEO's master inventory
    public static boolean addProductToCEOInventory(String productName, double buyingPrice,
                                                   int quantity, int minStockLevel) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO ceo_inventory (product_name, buying_price, total_quantity, min_stock_level) " +
                    "VALUES (?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, productName);
            ps.setDouble(2, buyingPrice);
            ps.setInt(3, quantity);
            ps.setInt(4, minStockLevel);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(null, ps, conn);
        }
        return false;
    }

    // Get all products from CEO's inventory
    public static List<CEOInventory> getAllCEOInventory() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<CEOInventory> inventory = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM ceo_inventory ORDER BY product_name";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                CEOInventory item = new CEOInventory(
                        rs.getInt("id"),
                        rs.getString("product_name"),
                        rs.getDouble("buying_price"),
                        rs.getInt("total_quantity"),
                        rs.getInt("min_stock_level"),
                        rs.getTimestamp("created_date")
                );
                inventory.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(rs, ps, conn);
        }
        return inventory;
    }

    // Update product in CEO's inventory
    public static boolean updateCEOInventory(int productId, String productName, double buyingPrice,
                                             int quantity, int minStockLevel) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "UPDATE ceo_inventory SET product_name = ?, buying_price = ?, " +
                    "total_quantity = ?, min_stock_level = ? WHERE id = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, productName);
            ps.setDouble(2, buyingPrice);
            ps.setInt(3, quantity);
            ps.setInt(4, minStockLevel);
            ps.setInt(5, productId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(null, ps, conn);
        }
        return false;
    }

    // Delete product from CEO's inventory
    public static boolean deleteFromCEOInventory(int productId) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "DELETE FROM ceo_inventory WHERE id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, productId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(null, ps, conn);
        }
        return false;
    }

    // Get low stock products from CEO inventory
    public static List<CEOInventory> getLowStockCEOInventory() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<CEOInventory> lowStock = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM ceo_inventory WHERE total_quantity <= min_stock_level ORDER BY total_quantity ASC";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                CEOInventory item = new CEOInventory(
                        rs.getInt("id"),
                        rs.getString("product_name"),
                        rs.getDouble("buying_price"),
                        rs.getInt("total_quantity"),
                        rs.getInt("min_stock_level"),
                        rs.getTimestamp("created_date")
                );
                lowStock.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(rs, ps, conn);
        }
        return lowStock;
    }
}

