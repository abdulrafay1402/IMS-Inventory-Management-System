package database;

import models.SalaryPayment;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SalaryDAO {
    
    // Process salary payment for a single employee with bonus and adjustments
    public static boolean processSingleSalaryWithAdjustments(int managerId, int userId, String paymentMonth, 
                                                             double baseSalary, double bonus, double adjustment, double finalAmount) {
        Connection conn = null;
        PreparedStatement psUser = null;
        PreparedStatement psPayment = null;
        PreparedStatement psExpense = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Get employee details and check joining date
            String sqlUser = "SELECT name, role, joining_date FROM users WHERE id = ? AND status = 'ACTIVE'";
            psUser = conn.prepareStatement(sqlUser);
            psUser.setInt(1, userId);
            rs = psUser.executeQuery();
            
            if (!rs.next()) {
                conn.rollback();
                return false;
            }
            
            String name = rs.getString("name");
            String role = rs.getString("role");
            String joiningDateStr = rs.getString("joining_date");
            
            // Check if employee was working in the payment month
            if (joiningDateStr != null) {
                String joiningYearMonth = joiningDateStr.substring(0, 7);
                if (paymentMonth.compareTo(joiningYearMonth) < 0) {
                    conn.rollback();
                    return false;
                }
            }
            
            // Check if payment already exists
            String checkSql = "SELECT COUNT(*) FROM salary_payments WHERE user_id = ? AND payment_month = ?";
            try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                psCheck.setInt(1, userId);
                psCheck.setString(2, paymentMonth);
                try (ResultSet rsCheck = psCheck.executeQuery()) {
                    if (rsCheck.next() && rsCheck.getInt(1) > 0) {
                        conn.rollback();
                        return false;
                    }
                }
            }
            
            // Build payment notes with breakdown
            String notes = "Individual payment for " + paymentMonth + " (Base: Rs. " + String.format("%.2f", baseSalary);
            if (bonus > 0) notes += ", Bonus: Rs. " + String.format("%.2f", bonus);
            if (adjustment != 0) {
                notes += ", " + (adjustment > 0 ? "Increment" : "Decrement") + ": Rs. " + String.format("%.2f", Math.abs(adjustment));
            }
            notes += ")";
            
            // Insert salary payment record with final amount
            String sqlPayment = "INSERT INTO salary_payments (user_id, amount, payment_month, status, notes, created_by) " +
                              "VALUES (?, ?, ?, 'PAID', ?, ?)";
            psPayment = conn.prepareStatement(sqlPayment);
            psPayment.setInt(1, userId);
            psPayment.setDouble(2, finalAmount);
            psPayment.setString(3, paymentMonth);
            psPayment.setString(4, notes);
            psPayment.setInt(5, managerId);
            psPayment.executeUpdate();
            
            // Create expense entry
            String expenseDesc = "Salary for " + name + " (" + role + ") - " + paymentMonth;
            if (bonus > 0 || adjustment != 0) {
                expenseDesc += " with adjustments";
            }
            
            String sqlExpense = "INSERT INTO expenses (manager_id, category, amount, description, expense_date) " +
                              "VALUES (?, 'SALARIES', ?, ?, CURRENT_TIMESTAMP)";
            psExpense = conn.prepareStatement(sqlExpense);
            psExpense.setInt(1, managerId);
            psExpense.setDouble(2, finalAmount);
            psExpense.setString(3, expenseDesc);
            psExpense.executeUpdate();
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            DatabaseConnection.closeResources(rs, psUser, null);
            if (psPayment != null) {
                try { psPayment.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (psExpense != null) {
                try { psExpense.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    // Process salary payment for a single employee
    public static boolean processSingleSalary(int managerId, int userId, String paymentMonth) {
        Connection conn = null;
        PreparedStatement psUser = null;
        PreparedStatement psPayment = null;
        PreparedStatement psExpense = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Get employee details and check joining date
            String sqlUser = "SELECT name, role, salary, joining_date FROM users WHERE id = ? AND status = 'ACTIVE' AND salary > 0";
            psUser = conn.prepareStatement(sqlUser);
            psUser.setInt(1, userId);
            rs = psUser.executeQuery();
            
            if (!rs.next()) {
                conn.rollback();
                return false;
            }
            
            String name = rs.getString("name");
            String role = rs.getString("role");
            double salary = rs.getDouble("salary");
            String joiningDateStr = rs.getString("joining_date");
            
            // Check if employee was working in the payment month
            if (joiningDateStr != null) {
                // Extract year-month from joining date (YYYY-MM-DD format)
                String joiningYearMonth = joiningDateStr.substring(0, 7);
                if (paymentMonth.compareTo(joiningYearMonth) < 0) {
                    // Payment month is before joining month
                    conn.rollback();
                    return false;
                }
            }
            
            // Check if payment already exists
            String checkSql = "SELECT COUNT(*) FROM salary_payments WHERE user_id = ? AND payment_month = ?";
            try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                psCheck.setInt(1, userId);
                psCheck.setString(2, paymentMonth);
                try (ResultSet rsCheck = psCheck.executeQuery()) {
                    if (rsCheck.next() && rsCheck.getInt(1) > 0) {
                        conn.rollback();
                        return false; // Already paid
                    }
                }
            }
            
            // Insert salary payment record
            String sqlPayment = "INSERT INTO salary_payments (user_id, amount, payment_month, status, notes, created_by) " +
                              "VALUES (?, ?, ?, 'PAID', ?, ?)";
            psPayment = conn.prepareStatement(sqlPayment);
            psPayment.setInt(1, userId);
            psPayment.setDouble(2, salary);
            psPayment.setString(3, paymentMonth);
            psPayment.setString(4, "Individual salary payment for " + paymentMonth);
            psPayment.setInt(5, managerId);
            psPayment.executeUpdate();
            
            // Create expense entry
            String sqlExpense = "INSERT INTO expenses (manager_id, category, amount, description, expense_date) " +
                              "VALUES (?, 'SALARIES', ?, ?, CURRENT_TIMESTAMP)";
            psExpense = conn.prepareStatement(sqlExpense);
            psExpense.setInt(1, managerId);
            psExpense.setDouble(2, salary);
            psExpense.setString(3, "Salary payment for " + name + " (" + role + ") - " + paymentMonth);
            psExpense.executeUpdate();
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            DatabaseConnection.closeResources(rs, psUser, null);
            if (psPayment != null) {
                try { psPayment.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (psExpense != null) {
                try { psExpense.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    // Process salary payments for all active employees for a specific month
    public static boolean processMonthlySalaries(int managerId, String paymentMonth) {
        Connection conn = null;
        PreparedStatement psUsers = null;
        PreparedStatement psPayment = null;
        PreparedStatement psExpense = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Get all active employees (excluding CEO) with their salaries
            String sqlUsers = "SELECT id, name, role, salary, joining_date FROM users WHERE status = 'ACTIVE' AND role != 'CEO' AND salary > 0";
            psUsers = conn.prepareStatement(sqlUsers);
            rs = psUsers.executeQuery();
            
            int paymentsProcessed = 0;
            double totalSalaryExpense = 0.0;
            
            while (rs.next()) {
                int userId = rs.getInt("id");
                String name = rs.getString("name");
                String role = rs.getString("role");
                double salary = rs.getDouble("salary");
                String joiningDateStr = rs.getString("joining_date");
                
                // Check if employee was working in the payment month
                if (joiningDateStr != null) {
                    String joiningYearMonth = joiningDateStr.substring(0, 7);
                    if (paymentMonth.compareTo(joiningYearMonth) < 0) {
                        continue; // Skip if payment month is before joining month
                    }
                }
                
                // Check if payment already exists for this month
                String checkSql = "SELECT COUNT(*) FROM salary_payments WHERE user_id = ? AND payment_month = ?";
                try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                    psCheck.setInt(1, userId);
                    psCheck.setString(2, paymentMonth);
                    try (ResultSet rsCheck = psCheck.executeQuery()) {
                        if (rsCheck.next() && rsCheck.getInt(1) > 0) {
                            continue; // Payment already exists, skip
                        }
                    }
                }
                
                // Insert salary payment record
                String sqlPayment = "INSERT INTO salary_payments (user_id, amount, payment_month, status, notes, created_by) " +
                                  "VALUES (?, ?, ?, 'PAID', ?, ?)";
                psPayment = conn.prepareStatement(sqlPayment);
                psPayment.setInt(1, userId);
                psPayment.setDouble(2, salary);
                psPayment.setString(3, paymentMonth);
                psPayment.setString(4, "Salary for " + paymentMonth);
                psPayment.setInt(5, managerId);
                psPayment.executeUpdate();
                
                totalSalaryExpense += salary;
                paymentsProcessed++;
            }
            
            // Create expense entry for total salary payments
            if (paymentsProcessed > 0) {
                String sqlExpense = "INSERT INTO expenses (manager_id, category, amount, description, expense_date) " +
                                  "VALUES (?, 'SALARIES', ?, ?, CURRENT_TIMESTAMP)";
                psExpense = conn.prepareStatement(sqlExpense);
                psExpense.setInt(1, managerId);
                psExpense.setDouble(2, totalSalaryExpense);
                psExpense.setString(3, "Monthly salary payments for " + paymentMonth + " (" + paymentsProcessed + " employees)");
                psExpense.executeUpdate();
            }
            
            conn.commit();
            return paymentsProcessed > 0;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            DatabaseConnection.closeResources(rs, psUsers, null);
            if (psPayment != null) {
                try { psPayment.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (psExpense != null) {
                try { psExpense.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    // Check if salaries have been paid for a specific month
    public static boolean areSalariesPaidForMonth(String paymentMonth) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT COUNT(*) FROM salary_payments WHERE payment_month = ? AND status = 'PAID'";
            ps = conn.prepareStatement(sql);
            ps.setString(1, paymentMonth);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DatabaseConnection.closeResources(rs, ps, conn);
        }
    }
    
    // Get all salary payments for a specific month
    public static List<SalaryPayment> getSalaryPaymentsByMonth(String paymentMonth) {
        List<SalaryPayment> payments = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT sp.id, sp.user_id, u.name, u.role, sp.amount, sp.payment_month, " +
                        "sp.payment_date, sp.status, sp.notes, sp.created_by " +
                        "FROM salary_payments sp " +
                        "JOIN users u ON sp.user_id = u.id " +
                        "WHERE sp.payment_month = ? " +
                        "ORDER BY u.role, u.name";
            ps = conn.prepareStatement(sql);
            ps.setString(1, paymentMonth);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                SalaryPayment payment = new SalaryPayment(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getString("name"),
                    rs.getString("role"),
                    rs.getDouble("amount"),
                    rs.getString("payment_month"),
                    rs.getTimestamp("payment_date").toLocalDateTime(),
                    rs.getString("status"),
                    rs.getString("notes"),
                    rs.getInt("created_by")
                );
                payments.add(payment);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(rs, ps, conn);
        }
        
        return payments;
    }
    
    // Get pending employees for salary payment (who haven't been paid this month)
    public static List<UserSalaryInfo> getPendingEmployeesForMonth(String paymentMonth) {
        List<UserSalaryInfo> employees = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT u.id, u.name, u.role, u.salary, u.joining_date " +
                        "FROM users u " +
                        "WHERE u.status = 'ACTIVE' AND u.role != 'CEO' AND u.salary > 0 " +
                        "AND u.id NOT IN (SELECT user_id FROM salary_payments WHERE payment_month = ?) " +
                        "ORDER BY u.role, u.name";
            ps = conn.prepareStatement(sql);
            ps.setString(1, paymentMonth);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                String joiningDateStr = rs.getString("joining_date");
                
                // Check if employee was working in the payment month
                if (joiningDateStr != null) {
                    String joiningYearMonth = joiningDateStr.substring(0, 7);
                    if (paymentMonth.compareTo(joiningYearMonth) < 0) {
                        continue; // Skip if payment month is before joining month
                    }
                }
                
                // Create a simple object with needed info
                employees.add(new UserSalaryInfo(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("role"),
                    rs.getDouble("salary"),
                    joiningDateStr
                ));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(rs, ps, conn);
        }
        
        return employees;
    }
    
    // Inner class for user salary info
    public static class UserSalaryInfo {
        private int id;
        private String name;
        private String role;
        private double salary;
        private String joiningDate;
        
        public UserSalaryInfo(int id, String name, String role, double salary, String joiningDate) {
            this.id = id;
            this.name = name;
            this.role = role;
            this.salary = salary;
            this.joiningDate = joiningDate;
        }
        
        public int getId() { return id; }
        public String getName() { return name; }
        public String getRole() { return role; }
        public double getSalary() { return salary; }
        public String getJoiningDate() { return joiningDate; }
    }
}
