package database;

import models.Expense;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// ============= EXPENSE DATA ACCESS LAYER =============
public class ExpenseDAO {

    // Get all expenses from all managers
    public static List<Expense> getAllExpenses() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Expense> expenses = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT e.*, u.name as manager_name FROM expenses e " +
                    "JOIN users u ON e.manager_id = u.id " +
                    "ORDER BY e.expense_date DESC";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Expense expense = new Expense(
                        rs.getInt("id"),
                        rs.getInt("manager_id"),
                        rs.getString("description"),
                        rs.getDouble("amount"),
                        rs.getString("category"),
                        rs.getString("manager_name"),
                        rs.getDate("expense_date"),
                        rs.getTimestamp("recorded_date")
                );
                expenses.add(expense);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(rs, ps, conn);
        }
        return expenses;
    }

    // Get total expenses amount
    public static double getTotalExpenses() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT SUM(amount) as total FROM expenses";
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

    // Get expenses by manager
    public static List<Expense> getExpensesByManager(int managerId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Expense> expenses = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT e.*, u.name as manager_name FROM expenses e " +
                    "JOIN users u ON e.manager_id = u.id " +
                    "WHERE e.manager_id = ? " +
                    "ORDER BY e.expense_date DESC";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, managerId);
            rs = ps.executeQuery();

            while (rs.next()) {
                Expense expense = new Expense(
                        rs.getInt("id"),
                        rs.getInt("manager_id"),
                        rs.getString("description"),
                        rs.getDouble("amount"),
                        rs.getString("category"),
                        rs.getString("manager_name"),
                        rs.getDate("expense_date"),
                        rs.getTimestamp("recorded_date")
                );
                expenses.add(expense);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(rs, ps, conn);
        }
        return expenses;
    }

    // Get expenses by category
    public static java.util.Map<String, Double> getExpensesByCategory() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        java.util.Map<String, Double> categoryTotals = new java.util.HashMap<>();

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT category, SUM(amount) as total FROM expenses GROUP BY category";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                categoryTotals.put(rs.getString("category"), rs.getDouble("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(rs, ps, conn);
        }
        return categoryTotals;
    }
    // Add new expense
    public static boolean addExpense(int managerId, String description, double amount,
                                     String category, java.util.Date expenseDate) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO expenses (manager_id, description, amount, category, expense_date) " +
                    "VALUES (?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, managerId);
            ps.setString(2, description);
            ps.setDouble(3, amount);
            ps.setString(4, category);
            ps.setDate(5, new java.sql.Date(expenseDate.getTime()));

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(null, ps, conn);
        }
        return false;
    }
}

