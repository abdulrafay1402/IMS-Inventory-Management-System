package database;

import models.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// ============= DATA ACCESS LAYER =============
public class UserDAO {
    // Authenticate user
    public static User authenticate(String username, String password) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND status = 'ACTIVE'";
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            rs = ps.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                switch (role) {
                    case "CEO":
                        return new CEO(
                                rs.getInt("id"),
                                rs.getString("username"),
                                rs.getString("password"),
                                rs.getString("name"),
                                rs.getString("phone"),
                                rs.getString("cnic"),
                                rs.getString("status")
                        );
                    case "MANAGER":
                        return new Manager(
                                rs.getInt("id"),
                                rs.getString("username"),
                                rs.getString("password"),
                                rs.getString("name"),
                                rs.getString("phone"),
                                rs.getString("cnic"),
                                rs.getString("status")
                        );
                    case "CASHIER":
                        Cashier cashier = new Cashier(
                                rs.getInt("id"),
                                rs.getString("username"),
                                rs.getString("password"),
                                rs.getString("name"),
                                rs.getString("phone"),
                                rs.getString("cnic"),
                                rs.getString("status")
                        );
                        // Set manager ID for cashier
                        cashier.setManagerId(getManagerIdForCashier(rs.getInt("id")));
                        return cashier;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(rs, ps, conn);
        }
        return null;
    }

    // Get manager ID for a cashier
    private static int getManagerIdForCashier(int cashierId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT manager_id FROM cashier_manager WHERE cashier_id = ? AND status = 'ACTIVE'";
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
        return -1;
    }

    // Check if username is unique (exclude specific user ID for updates)
    public static boolean isUsernameUnique(String username, Integer excludeUserId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = excludeUserId == null ?
                    "SELECT COUNT(*) FROM users WHERE username = ?" :
                    "SELECT COUNT(*) FROM users WHERE username = ? AND id != ?";

            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            if (excludeUserId != null) {
                ps.setInt(2, excludeUserId);
            }

            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(rs, ps, conn);
        }
        return false;
    }

    // Check if CNIC is unique (exclude specific user ID for updates)
    public static boolean isCnicUnique(String cnic, Integer excludeUserId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = excludeUserId == null ?
                    "SELECT COUNT(*) FROM users WHERE cnic = ?" :
                    "SELECT COUNT(*) FROM users WHERE cnic = ? AND id != ?";

            ps = conn.prepareStatement(sql);
            ps.setString(1, cnic);
            if (excludeUserId != null) {
                ps.setInt(2, excludeUserId);
            }

            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(rs, ps, conn);
        }
        return false;
    }

    // Add new manager (by CEO) - KEEP ONLY ONE VERSION OF THIS METHOD
    public static boolean addManager(String username, String password, String name,
                                     String phone, String cnic, int ceoId) {
        Connection conn = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Insert manager
            String sql1 = "INSERT INTO users (username, password, role, name, phone, cnic, status) " +
                    "VALUES (?, ?, 'MANAGER', ?, ?, ?, 'ACTIVE')";
            ps1 = conn.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS);
            ps1.setString(1, username);
            ps1.setString(2, password);
            ps1.setString(3, name);
            ps1.setString(4, phone);
            ps1.setString(5, cnic);

            int rows = ps1.executeUpdate();
            if (rows > 0) {
                rs = ps1.getGeneratedKeys();
                if (rs.next()) {
                    int managerId = rs.getInt(1);

                    // Create CEO-Manager relationship
                    String sql2 = "INSERT INTO manager_ceo (ceo_id, manager_id) VALUES (?, ?)";
                    ps2 = conn.prepareStatement(sql2);
                    ps2.setInt(1, ceoId);
                    ps2.setInt(2, managerId);
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
                try {
                    ps2.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    // Update user profile (username, password, phone only)
    public static boolean updateUserProfile(int userId, String username,
                                            String password, String phone) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "UPDATE users SET username = ?, password = ?, phone = ? WHERE id = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, phone);
            ps.setInt(4, userId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(null, ps, conn);
        }
        return false;
    }

    // Get all managers (for CEO)
    public static List<Manager> getAllManagers() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Manager> managers = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT u.*, mc.ceo_id FROM users u " +
                    "JOIN manager_ceo mc ON u.id = mc.manager_id " +
                    "WHERE u.role = 'MANAGER' AND u.status = 'ACTIVE'";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Manager manager = new Manager(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("cnic"),
                        rs.getString("status")
                );
                manager.setCeoId(rs.getInt("ceo_id"));
                managers.add(manager);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(rs, ps, conn);
        }
        return managers;
    }

    // Get pending cashier requests
    public static List<CashierRequest> getPendingCashierRequests() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<CashierRequest> requests = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT cm.*, u.name as cashier_name, u.username, u.phone, u.cnic, " +
                    "m.name as manager_name " +
                    "FROM cashier_manager cm " +
                    "JOIN users u ON cm.cashier_id = u.id " +
                    "JOIN users m ON cm.manager_id = m.id " +
                    "WHERE cm.status = 'PENDING_APPROVAL'";
            ps = conn.prepareStatement(sql);
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
                        rs.getString("manager_name"),
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

    // Approve/Reject cashier request
    public static boolean updateCashierRequestStatus(int requestId, boolean approve, String reason) {
        Connection conn = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Update cashier_manager status
            String sql1 = "UPDATE cashier_manager SET status = ?, approval_date = CURRENT_TIMESTAMP, " +
                    "rejection_reason = ? WHERE id = ?";
            ps1 = conn.prepareStatement(sql1);
            ps1.setString(1, approve ? "ACTIVE" : "REJECTED");
            ps1.setString(2, approve ? null : reason);
            ps1.setInt(3, requestId);

            int rows = ps1.executeUpdate();

            if (rows > 0 && approve) {
                // Update user status to ACTIVE
                String sql2 = "UPDATE users u JOIN cashier_manager cm ON u.id = cm.cashier_id " +
                        "SET u.status = 'ACTIVE' WHERE cm.id = ?";
                ps2 = conn.prepareStatement(sql2);
                ps2.setInt(1, requestId);
                ps2.executeUpdate();
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
            DatabaseConnection.closeResources(null, ps1, conn);
            if (ps2 != null) {
                try { ps2.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
        return false;
    }
}

