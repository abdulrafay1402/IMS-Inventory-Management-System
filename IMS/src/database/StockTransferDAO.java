package database;

import java.sql.*;

// ============= STOCK TRANSFER DATA ACCESS LAYER =============
public class StockTransferDAO {

    // Transfer stock from CEO inventory to manager inventory
    public static boolean transferStockFromCEO(int managerId, int ceoProductId,
                                               int quantity, double sellingPrice) {
        Connection conn = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;
        PreparedStatement ps3 = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Check if CEO has enough stock
            String checkStockSql = "SELECT total_quantity FROM ceo_inventory WHERE id = ? AND total_quantity >= ?";
            ps1 = conn.prepareStatement(checkStockSql);
            ps1.setInt(1, ceoProductId);
            ps1.setInt(2, quantity);
            ResultSet rs = ps1.executeQuery();

            if (!rs.next()) {
                conn.rollback();
                return false; // Not enough stock
            }

            // 2. Deduct from CEO inventory
            String updateCeoSql = "UPDATE ceo_inventory SET total_quantity = total_quantity - ?, " +
                    "last_updated = CURRENT_TIMESTAMP WHERE id = ?";
            ps2 = conn.prepareStatement(updateCeoSql);
            ps2.setInt(1, quantity);
            ps2.setInt(2, ceoProductId);
            int ceoUpdated = ps2.executeUpdate();

            if (ceoUpdated == 0) {
                conn.rollback();
                return false;
            }

            // 3. Add to manager inventory (or update if exists)
            String managerSql = "INSERT INTO manager_inventory (manager_id, ceo_inventory_id, " +
                    "selling_price, current_quantity) " +
                    "VALUES (?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "current_quantity = current_quantity + VALUES(current_quantity), " +
                    "selling_price = VALUES(selling_price), " +
                    "last_updated = CURRENT_TIMESTAMP";
            ps3 = conn.prepareStatement(managerSql);
            ps3.setInt(1, managerId);
            ps3.setInt(2, ceoProductId);
            ps3.setDouble(3, sellingPrice);
            ps3.setInt(4, quantity);
            ps3.executeUpdate();

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
            DatabaseConnection.closeResources(null, ps1, conn);
            if (ps2 != null) {
                try { ps2.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (ps3 != null) {
                try { ps3.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
        return false;
    }

    // Check if manager can get stock (enough quantity available)
    public static boolean canTransferStock(int ceoProductId, int quantity) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT total_quantity FROM ceo_inventory WHERE id = ? AND total_quantity >= ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, ceoProductId);
            ps.setInt(2, quantity);
            rs = ps.executeQuery();

            return rs.next(); // Returns true if enough stock available
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(rs, ps, conn);
        }
        return false;
    }

    // Get available quantity from CEO for a product
    public static int getAvailableQuantity(int ceoProductId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT total_quantity FROM ceo_inventory WHERE id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, ceoProductId);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("total_quantity");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(rs, ps, conn);
        }
        return 0;
    }
}

