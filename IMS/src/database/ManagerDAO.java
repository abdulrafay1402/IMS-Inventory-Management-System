package database;

import models.Cashier;
import models.CashierRequest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// ============= MANAGER DATA ACCESS LAYER =============
public class ManagerDAO {

    // Get number of cashiers under this manager
    public static int getCashiersCount(int managerId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT COUNT(*) as count FROM cashier_manager " +
                    "WHERE manager_id = ? AND status = 'ACTIVE'";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, managerId);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(rs, ps, conn);
        }
        return 0;
    }

    // Get number of products in manager's inventory
    public static int getProductsCount(int managerId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT COUNT(*) as count FROM manager_inventory " +
                    "WHERE manager_id = ? AND current_quantity > 0";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, managerId);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(rs, ps, conn);
        }
        return 0;
    }

    // Get manager's total revenue
    public static double getManagerRevenue(int managerId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT SUM(total_amount) as total FROM bills " +
                    "WHERE manager_id = ? AND status = 'COMPLETED'";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, managerId);
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

    // Get manager's total expenses
    public static double getManagerExpenses(int managerId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT SUM(amount) as total FROM expenses " +
                    "WHERE manager_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, managerId);
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

    // Get low stock count for manager
    public static int getLowStockCount(int managerId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT COUNT(*) as count FROM manager_inventory mi " +
                    "JOIN ceo_inventory ci ON mi.ceo_inventory_id = ci.id " +
                    "WHERE mi.manager_id = ? AND mi.current_quantity <= ci.min_stock_level";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, managerId);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(rs, ps, conn);
        }
        return 0;
    }

    // Request new cashier (add to cashier_manager with PENDING status)
    public static boolean requestCashier(int managerId, String name, String username,
                                         String password, String phone, String cnic, double salary) {
        Connection conn = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // First insert the cashier user with salary
            String sql1 = "INSERT INTO users (username, password, role, name, phone, cnic, salary, status) " +
                    "VALUES (?, ?, 'CASHIER', ?, ?, ?, ?, 'PENDING')";
            ps1 = conn.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS);
            ps1.setString(1, username);
            ps1.setString(2, password);
            ps1.setString(3, name);
            ps1.setString(4, phone);
            ps1.setString(5, cnic);
            ps1.setDouble(6, salary);

            int rows = ps1.executeUpdate();
            if (rows > 0) {
                rs = ps1.getGeneratedKeys();
                if (rs.next()) {
                    int cashierId = rs.getInt(1);

                    // Create manager-cashier relationship with PENDING status
                    String sql2 = "INSERT INTO cashier_manager (manager_id, cashier_id, status) VALUES (?, ?, 'PENDING_APPROVAL')";
                    ps2 = conn.prepareStatement(sql2);
                    ps2.setInt(1, managerId);
                    ps2.setInt(2, cashierId);
                    ps2.executeUpdate();

                    conn.commit();
                    return true;
                }
            }
            conn.rollback();
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
        }
        return false;
    }
    // Get pending cashier requests for this manager
    public static List<CashierRequest> getPendingCashierRequests(int managerId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<CashierRequest> requests = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT cm.*, u.name as cashier_name, u.username, u.phone, u.cnic " +
                    "FROM cashier_manager cm " +
                    "JOIN users u ON cm.cashier_id = u.id " +
                    "WHERE cm.manager_id = ? AND cm.status = 'PENDING_APPROVAL'";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, managerId);
            rs = ps.executeQuery();

            while (rs.next()) {
                CashierRequest request = new CashierRequest(
                        rs.getInt("id"),
                        rs.getInt("manager_id"),
                        rs.getInt("cashier_id"),
                        rs.getString("cashier_name"),
                        rs.getString("username"),
                        rs.getString("phone"),
                        rs.getString("cnic"),
                        "Manager", // Since we're getting from manager's perspective
                        rs.getString("status"),
                        rs.getDate("request_date")
                );
                requests.add(request);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(rs, ps, conn);
        }
        return requests;
    }
    // Get approved cashiers for this manager
    public static List<Cashier> getApprovedCashiers(int managerId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Cashier> cashiers = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT u.* FROM users u " +
                    "JOIN cashier_manager cm ON u.id = cm.cashier_id " +
                    "WHERE cm.manager_id = ? AND cm.status = 'ACTIVE' AND u.status = 'ACTIVE'";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, managerId);
            rs = ps.executeQuery();

            while (rs.next()) {
                Cashier cashier = new Cashier(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("cnic"),
                        rs.getString("status")
                );
                cashier.setManagerId(managerId);
                cashier.setRequestStatus("ACTIVE");
                cashiers.add(cashier);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(rs, ps, conn);
        }
        return cashiers;
    }
}

