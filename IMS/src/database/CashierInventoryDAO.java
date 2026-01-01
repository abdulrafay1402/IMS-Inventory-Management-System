package database;

import models.Bill;
import models.BillItem;
import models.BillItemDetail;
import models.ManagerInventory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// ============= CASHIER INVENTORY DATA ACCESS LAYER =============
public class CashierInventoryDAO {

    // Get manager ID for this cashier
    public static int getManagerIdForCashier(int cashierId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT manager_id FROM cashier_manager " +
                    "WHERE cashier_id = ? AND status = 'ACTIVE' LIMIT 1";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, cashierId);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("manager_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(rs, ps, conn);
        }
        return -1; // No manager found
    }

    // Get available products from cashier's manager inventory
    public static List<ManagerInventory> getAvailableProductsForCashier(int cashierId) {
        int managerId = getManagerIdForCashier(cashierId);
        if (managerId == -1) {
            return new ArrayList<>();
        }

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<ManagerInventory> inventory = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT mi.*, ci.product_name, ci.min_stock_level, ci.buying_price " +
                    "FROM manager_inventory mi " +
                    "JOIN ceo_inventory ci ON mi.ceo_inventory_id = ci.id " +
                    "WHERE mi.manager_id = ? AND mi.current_quantity > 0 " +
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

    // Create a new bill with items (transaction)
    public static boolean createBill(int cashierId, int managerId,
                                     List<BillItem> items, double totalAmount) {
        Connection conn = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;
        PreparedStatement ps3 = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Generate bill number
            String billNumber = generateBillNumber();

            // 1. Insert bill
            String sql1 = "INSERT INTO bills (bill_number, cashier_id, manager_id, total_amount, status) " +
                    "VALUES (?, ?, ?, ?, 'COMPLETED')";
            ps1 = conn.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS);
            ps1.setString(1, billNumber);
            ps1.setInt(2, cashierId);
            ps1.setInt(3, managerId);
            ps1.setDouble(4, totalAmount);

            int billRows = ps1.executeUpdate();
            if (billRows == 0) {
                conn.rollback();
                return false;
            }

            rs = ps1.getGeneratedKeys();
            if (!rs.next()) {
                conn.rollback();
                return false;
            }
            int billId = rs.getInt(1);

            // 2. Insert bill items and update inventory
            String sql2 = "INSERT INTO bill_items (bill_id, manager_inventory_id, quantity, unit_price, subtotal) " +
                    "VALUES (?, ?, ?, ?, ?)";
            String sql3 = "UPDATE manager_inventory SET current_quantity = current_quantity - ?, " +
                    "last_updated = CURRENT_TIMESTAMP WHERE id = ? AND current_quantity >= ?";

            ps2 = conn.prepareStatement(sql2);
            ps3 = conn.prepareStatement(sql3);

            for (BillItem item : items) {
                // Insert bill item
                ps2.setInt(1, billId);
                ps2.setInt(2, item.getManagerInventoryId());
                ps2.setInt(3, item.getQuantity());
                ps2.setDouble(4, item.getUnitPrice());
                ps2.setDouble(5, item.getSubtotal());
                ps2.executeUpdate();

                // Update inventory
                ps3.setInt(1, item.getQuantity());
                ps3.setInt(2, item.getManagerInventoryId());
                ps3.setInt(3, item.getQuantity());
                int updateRows = ps3.executeUpdate();

                if (updateRows == 0) {
                    // Not enough stock
                    conn.rollback();
                    return false;
                }
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
            DatabaseConnection.closeResources(rs, ps1, conn);
            if (ps2 != null) {
                try { ps2.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (ps3 != null) {
                try { ps3.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
        return false;
    }

    // Generate unique bill number
    private static String generateBillNumber() {
        return "BILL-" + System.currentTimeMillis();
    }

    // Get bills for a specific cashier
    public static List<Bill> getCashierBills(int cashierId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Bill> bills = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT b.*, u.name as cashier_name, m.name as manager_name " +
                    "FROM bills b " +
                    "JOIN users u ON b.cashier_id = u.id " +
                    "JOIN users m ON b.manager_id = m.id " +
                    "WHERE b.cashier_id = ? " +
                    "ORDER BY b.bill_date DESC";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, cashierId);
            rs = ps.executeQuery();

            while (rs.next()) {
                Bill bill = new Bill(
                        rs.getInt("id"),
                        rs.getString("bill_number"),
                        rs.getInt("cashier_id"),
                        rs.getInt("manager_id"),
                        rs.getDouble("total_amount"),
                        rs.getTimestamp("bill_date"),
                        rs.getString("status"),
                        rs.getString("cashier_name"),
                        rs.getString("manager_name")
                );
                bills.add(bill);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(rs, ps, conn);
        }
        return bills;
    }

    // Get bill items for a specific bill
    public static List<BillItemDetail> getBillItems(int billId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<BillItemDetail> items = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT bi.*, ci.product_name " +
                    "FROM bill_items bi " +
                    "JOIN manager_inventory mi ON bi.manager_inventory_id = mi.id " +
                    "JOIN ceo_inventory ci ON mi.ceo_inventory_id = ci.id " +
                    "WHERE bi.bill_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, billId);
            rs = ps.executeQuery();

            while (rs.next()) {
                BillItemDetail item = new BillItemDetail(
                        rs.getInt("id"),
                        rs.getInt("bill_id"),
                        rs.getString("product_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("unit_price"),
                        rs.getDouble("subtotal")
                );
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(rs, ps, conn);
        }
        return items;
    }
}

