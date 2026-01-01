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
                double salary = rs.getDouble("salary");
                User user = null;
                
                switch (role) {
                    case "CEO":
                        user = new CEO(
                                rs.getInt("id"),
                                rs.getString("username"),
                                rs.getString("password"),
                                rs.getString("name"),
                                rs.getString("phone"),
                                rs.getString("cnic"),
                                rs.getString("status")
                        );
                        break;
                    case "MANAGER":
                        user = new Manager(
                                rs.getInt("id"),
                                rs.getString("username"),
                                rs.getString("password"),
                                rs.getString("name"),
                                rs.getString("phone"),
                                rs.getString("cnic"),
                                rs.getString("status")
                        );
                        break;
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
                        user = cashier;
                        break;
                }
                
                if (user != null) {
                    user.setSalary(salary);
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(rs, ps, conn);
        }
        return null;
    }

    // Get joining date for a user
    public static String getJoiningDate(int userId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT joining_date FROM users WHERE id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("joining_date");
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
                                     String phone, String cnic, double salary, int ceoId) {
        Connection conn = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Insert manager with salary
            String sql1 = "INSERT INTO users (username, password, role, name, phone, cnic, salary, status) " +
                    "VALUES (?, ?, 'MANAGER', ?, ?, ?, ?, 'ACTIVE')";
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

            // Get cashier and manager info before updating
            String getInfoSql = "SELECT cm.cashier_id, cm.manager_id, u.name as cashier_name, u.username as cashier_username " +
                               "FROM cashier_manager cm " +
                               "JOIN users u ON cm.cashier_id = u.id " +
                               "WHERE cm.id = ?";
            PreparedStatement getInfoPs = conn.prepareStatement(getInfoSql);
            getInfoPs.setInt(1, requestId);
            ResultSet infoRs = getInfoPs.executeQuery();
            
            int cashierId = 0;
            int managerId = 0;
            String cashierName = "";
            String cashierUsername = "";
            
            if (infoRs.next()) {
                cashierId = infoRs.getInt("cashier_id");
                managerId = infoRs.getInt("manager_id");
                cashierName = infoRs.getString("cashier_name");
                cashierUsername = infoRs.getString("cashier_username");
            }
            infoRs.close();
            getInfoPs.close();

            // Update cashier_manager status
            String sql1 = "UPDATE cashier_manager SET status = ?, approval_date = CURRENT_TIMESTAMP, " +
                    "rejection_reason = ? WHERE id = ?";
            ps1 = conn.prepareStatement(sql1);
            ps1.setString(1, approve ? "ACTIVE" : "REJECTED");
            ps1.setString(2, approve ? null : reason);
            ps1.setInt(3, requestId);

            int rows = ps1.executeUpdate();

            if (rows > 0 && approve && cashierId > 0) {
                // Update user status to ACTIVE
                String sql2 = "UPDATE users SET status = 'ACTIVE' WHERE id = ?";
                ps2 = conn.prepareStatement(sql2);
                ps2.setInt(1, cashierId);
                ps2.executeUpdate();
            }

            // Create notification for manager
            if (rows > 0 && managerId > 0) {
                try {
                    if (approve) {
                        NotificationDAO.createNotification(
                            managerId,
                            "CASHIER_APPROVED",
                            "Cashier Request Approved",
                            "Your cashier request for " + cashierName + " has been approved by CEO.",
                            cashierUsername
                        );
                    } else {
                        NotificationDAO.createNotification(
                            managerId,
                            "CASHIER_REJECTED",
                            "Cashier Request Rejected",
                            "Your cashier request for " + cashierName + " has been rejected. Reason: " + (reason != null ? reason : "Not specified"),
                            cashierUsername
                        );
                    }
                } catch (Exception e) {
                    // Log but don't fail the operation
                    System.err.println("Failed to create notification: " + e.getMessage());
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
            DatabaseConnection.closeResources(null, ps1, conn);
            if (ps2 != null) {
                try { ps2.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
        return false;
    }

    // Get CEO user ID
    public static int getCEOUserId() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT id FROM users WHERE role = 'CEO' AND status = 'ACTIVE' LIMIT 1";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(rs, ps, conn);
        }
        return 0;
    }
}
