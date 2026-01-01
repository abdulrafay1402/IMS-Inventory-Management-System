package database;

import models.Bill;
import models.BillItemDetail;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// ============= BILL DATA ACCESS LAYER =============
public class BillDAO {

    // Get total revenue from all bills
    public static double getTotalRevenue() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT SUM(total_amount) as total FROM bills WHERE status = 'COMPLETED'";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(rs, ps, conn);
        }
        return 0.0;
    }

    // Get revenue by manager
    public static java.util.Map<Integer, Double> getRevenueByManager() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        java.util.Map<Integer, Double> managerRevenue = new java.util.HashMap<>();

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT manager_id, SUM(total_amount) as total FROM bills " +
                    "WHERE status = 'COMPLETED' GROUP BY manager_id";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                managerRevenue.put(rs.getInt("manager_id"), rs.getDouble("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(rs, ps, conn);
        }
        return managerRevenue;
    }

    // Get all bills for reporting
    public static List<Bill> getAllBills() {
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
                    "ORDER BY b.bill_date DESC";
            ps = conn.prepareStatement(sql);
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

    // Get bill items by bill number
    public static List<BillItemDetail> getBillItems(String billNumber) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<BillItemDetail> items = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT bi.id, bi.bill_id, ci.product_name, bi.quantity, bi.unit_price, bi.subtotal " +
                    "FROM bill_items bi " +
                    "JOIN bills b ON bi.bill_id = b.id " +
                    "JOIN manager_inventory mi ON bi.manager_inventory_id = mi.id " +
                    "JOIN ceo_inventory ci ON mi.ceo_inventory_id = ci.id " +
                    "WHERE b.bill_number = ? " +
                    "ORDER BY bi.id";
            ps = conn.prepareStatement(sql);
            ps.setString(1, billNumber);
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

    // Get bill by bill number
    public static Bill getBillByNumber(String billNumber) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT b.*, u.name as cashier_name, m.name as manager_name " +
                    "FROM bills b " +
                    "JOIN users u ON b.cashier_id = u.id " +
                    "JOIN users m ON b.manager_id = m.id " +
                    "WHERE b.bill_number = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, billNumber);
            rs = ps.executeQuery();

            if (rs.next()) {
                return new Bill(
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(rs, ps, conn);
        }
        return null;
    }
}
