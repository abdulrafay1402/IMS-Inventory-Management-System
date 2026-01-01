package database;

import models.ManagerInventory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// ============= MANAGER INVENTORY DATA ACCESS LAYER =============
public class ManagerInventoryDAO {

    // Get manager's inventory
    public static List<ManagerInventory> getManagerInventory(int managerId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<ManagerInventory> inventory = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT mi.*, ci.product_name, ci.min_stock_level, ci.buying_price " +
                    "FROM manager_inventory mi " +
                    "JOIN ceo_inventory ci ON mi.ceo_inventory_id = ci.id " +
                    "WHERE mi.manager_id = ? " +
                    "ORDER BY ci.product_name";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, managerId);
            rs = ps.executeQuery();

            while (rs.next()) {
                ManagerInventory item = new ManagerInventory(
                        rs.getInt("id"),
                        rs.getInt("manager_id"),
                        rs.getInt("ceo_inventory_id"),
                        rs.getString("product_name"),
                        rs.getDouble("buying_price"),
                        rs.getDouble("selling_price"),
                        rs.getInt("current_quantity"),
                        rs.getInt("min_stock_level"),
                        rs.getTimestamp("last_updated")
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

    // Add product to manager's inventory (or update if exists)
    public static boolean addOrUpdateManagerInventory(int managerId, int ceoInventoryId,
                                                      int quantity, double sellingPrice) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Check if product already exists in manager's inventory
            String checkSql = "SELECT id, current_quantity FROM manager_inventory " +
                    "WHERE manager_id = ? AND ceo_inventory_id = ?";
            ps = conn.prepareStatement(checkSql);
            ps.setInt(1, managerId);
            ps.setInt(2, ceoInventoryId);
            rs = ps.executeQuery();

            if (rs.next()) {
                // Update existing product
                int existingId = rs.getInt("id");
                int existingQty = rs.getInt("current_quantity");
                String updateSql = "UPDATE manager_inventory SET current_quantity = ?, " +
                        "selling_price = ?, last_updated = CURRENT_TIMESTAMP " +
                        "WHERE id = ?";
                ps = conn.prepareStatement(updateSql);
                ps.setInt(1, existingQty + quantity);
                ps.setDouble(2, sellingPrice);
                ps.setInt(3, existingId);
                ps.executeUpdate();
            } else {
                // Insert new product
                String insertSql = "INSERT INTO manager_inventory (manager_id, ceo_inventory_id, " +
                        "selling_price, current_quantity) VALUES (?, ?, ?, ?)";
                ps = conn.prepareStatement(insertSql);
                ps.setInt(1, managerId);
                ps.setInt(2, ceoInventoryId);
                ps.setDouble(3, sellingPrice);
                ps.setInt(4, quantity);
                ps.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(rs, ps, conn);
        }
        return false;
    }

    // Update product quantity when bill is created
    public static boolean updateProductQuantity(int managerInventoryId, int quantitySold) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "UPDATE manager_inventory SET current_quantity = current_quantity - ?, " +
                    "last_updated = CURRENT_TIMESTAMP WHERE id = ? AND current_quantity >= ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, quantitySold);
            ps.setInt(2, managerInventoryId);
            ps.setInt(3, quantitySold);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(null, ps, conn);
        }
        return false;
    }

    // Get low stock items for manager
    public static List<ManagerInventory> getLowStockInventory(int managerId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<ManagerInventory> lowStock = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT mi.*, ci.product_name, ci.min_stock_level, ci.buying_price " +
                    "FROM manager_inventory mi " +
                    "JOIN ceo_inventory ci ON mi.ceo_inventory_id = ci.id " +
                    "WHERE mi.manager_id = ? AND mi.current_quantity <= ci.min_stock_level " +
                    "ORDER BY mi.current_quantity ASC";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, managerId);
            rs = ps.executeQuery();

            while (rs.next()) {
                ManagerInventory item = new ManagerInventory(
                        rs.getInt("id"),
                        rs.getInt("manager_id"),
                        rs.getInt("ceo_inventory_id"),
                        rs.getString("product_name"),
                        rs.getDouble("buying_price"),
                        rs.getDouble("selling_price"),
                        rs.getInt("current_quantity"),
                        rs.getInt("min_stock_level"),
                        rs.getTimestamp("last_updated")
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

    // Get product by ID
    public static ManagerInventory getProductById(int managerInventoryId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT mi.*, ci.product_name, ci.min_stock_level, ci.buying_price " +
                    "FROM manager_inventory mi " +
                    "JOIN ceo_inventory ci ON mi.ceo_inventory_id = ci.id " +
                    "WHERE mi.id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, managerInventoryId);
            rs = ps.executeQuery();

            if (rs.next()) {
                return new ManagerInventory(
                        rs.getInt("id"),
                        rs.getInt("manager_id"),
                        rs.getInt("ceo_inventory_id"),
                        rs.getString("product_name"),
                        rs.getDouble("buying_price"),
                        rs.getDouble("selling_price"),
                        rs.getInt("current_quantity"),
                        rs.getInt("min_stock_level"),
                        rs.getTimestamp("last_updated")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(rs, ps, conn);
        }
        return null;
    }
    // Add this method to ManagerInventoryDAO class
    public static ManagerInventory getProductByManagerAndCEOId(int managerId, int ceoInventoryId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT mi.*, ci.product_name, ci.min_stock_level, ci.buying_price " +
                    "FROM manager_inventory mi " +
                    "JOIN ceo_inventory ci ON mi.ceo_inventory_id = ci.id " +
                    "WHERE mi.manager_id = ? AND mi.ceo_inventory_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, managerId);
            ps.setInt(2, ceoInventoryId);
            rs = ps.executeQuery();

            if (rs.next()) {
                return new ManagerInventory(
                        rs.getInt("id"),
                        rs.getInt("manager_id"),
                        rs.getInt("ceo_inventory_id"),
                        rs.getString("product_name"),
                        rs.getDouble("buying_price"),
                        rs.getDouble("selling_price"),
                        rs.getInt("current_quantity"),
                        rs.getInt("min_stock_level"),
                        rs.getTimestamp("last_updated")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(rs, ps, conn);
        }
        return null;
    }
}

