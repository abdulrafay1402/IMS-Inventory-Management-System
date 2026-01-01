package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// ============= ENHANCED PROFIT CALCULATION DAO =============
public class EnhancedProfitDAO {

    // Manager Performance Model
    public static class ManagerPerformance {
        private int managerId;
        private String managerName;
        private double totalRevenue;
        private double totalCostOfGoodsSold;
        private double totalExpenses;
        private double grossProfit;
        private double netProfit;
        private double profitMargin;
        private int totalBills;
        private int totalProductsSold;

        public ManagerPerformance(int managerId, String managerName, double totalRevenue,
                                  double totalCostOfGoodsSold, double totalExpenses,
                                  int totalBills, int totalProductsSold) {
            this.managerId = managerId;
            this.managerName = managerName;
            this.totalRevenue = totalRevenue;
            this.totalCostOfGoodsSold = totalCostOfGoodsSold;
            this.totalExpenses = totalExpenses;
            this.grossProfit = totalRevenue - totalCostOfGoodsSold;
            this.netProfit = grossProfit - totalExpenses;
            this.profitMargin = totalRevenue > 0 ? (netProfit / totalRevenue) * 100 : 0;
            this.totalBills = totalBills;
            this.totalProductsSold = totalProductsSold;
        }

        // Getters
        public int getManagerId() { return managerId; }
        public String getManagerName() { return managerName; }
        public double getTotalRevenue() { return totalRevenue; }
        public double getTotalCostOfGoodsSold() { return totalCostOfGoodsSold; }
        public double getTotalExpenses() { return totalExpenses; }
        public double getGrossProfit() { return grossProfit; }
        public double getNetProfit() { return netProfit; }
        public double getProfitMargin() { return profitMargin; }
        public int getTotalBills() { return totalBills; }
        public int getTotalProductsSold() { return totalProductsSold; }
    }

    // Get comprehensive manager performance data
    public static List<ManagerPerformance> getManagerPerformanceReport() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<ManagerPerformance> performances = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();

            // Complex query to calculate revenue, COGS, and expenses per manager
            String sql = "SELECT " +
                    "m.id as manager_id, " +
                    "m.name as manager_name, " +
                    "COALESCE(SUM(b.total_amount), 0) as total_revenue, " +
                    "COALESCE(SUM(bi.quantity * ci.buying_price), 0) as total_cogs, " +
                    "COALESCE(SUM(e.amount), 0) as total_expenses, " +
                    "COUNT(DISTINCT b.id) as total_bills, " +
                    "COALESCE(SUM(bi.quantity), 0) as total_products_sold " +
                    "FROM users m " +
                    "LEFT JOIN bills b ON m.id = b.manager_id AND b.status = 'COMPLETED' " +
                    "LEFT JOIN bill_items bi ON b.id = bi.bill_id " +
                    "LEFT JOIN manager_inventory mi ON bi.manager_inventory_id = mi.id " +
                    "LEFT JOIN ceo_inventory ci ON mi.ceo_inventory_id = ci.id " +
                    "LEFT JOIN expenses e ON m.id = e.manager_id " +
                    "WHERE m.role = 'MANAGER' AND m.status = 'ACTIVE' " +
                    "GROUP BY m.id, m.name " +
                    "ORDER BY total_revenue DESC";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                ManagerPerformance performance = new ManagerPerformance(
                        rs.getInt("manager_id"),
                        rs.getString("manager_name"),
                        rs.getDouble("total_revenue"),
                        rs.getDouble("total_cogs"),
                        rs.getDouble("total_expenses"),
                        rs.getInt("total_bills"),
                        rs.getInt("total_products_sold")
                );
                performances.add(performance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(rs, ps, conn);
        }
        return performances;
    }

    // Get system-wide totals
    public static SystemTotals getSystemTotals() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();

            String sql = "SELECT " +
                    "COALESCE(SUM(b.total_amount), 0) as total_revenue, " +
                    "COALESCE(SUM(bi.quantity * ci.buying_price), 0) as total_cogs, " +
                    "COALESCE(SUM(e.amount), 0) as total_expenses " +
                    "FROM bills b " +
                    "LEFT JOIN bill_items bi ON b.id = bi.bill_id " +
                    "LEFT JOIN manager_inventory mi ON bi.manager_inventory_id = mi.id " +
                    "LEFT JOIN ceo_inventory ci ON mi.ceo_inventory_id = ci.id " +
                    "LEFT JOIN expenses e ON b.manager_id = e.manager_id " +
                    "WHERE b.status = 'COMPLETED'";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()) {
                double revenue = rs.getDouble("total_revenue");
                double cogs = rs.getDouble("total_cogs");
                double expenses = rs.getDouble("total_expenses");
                return new SystemTotals(revenue, cogs, expenses);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(rs, ps, conn);
        }
        return new SystemTotals(0, 0, 0);
    }

    // System Totals Model
    public static class SystemTotals {
        private double totalRevenue;
        private double totalCOGS;
        private double totalExpenses;
        private double grossProfit;
        private double netProfit;

        public SystemTotals(double totalRevenue, double totalCOGS, double totalExpenses) {
            this.totalRevenue = totalRevenue;
            this.totalCOGS = totalCOGS;
            this.totalExpenses = totalExpenses;
            this.grossProfit = totalRevenue - totalCOGS;
            this.netProfit = grossProfit - totalExpenses;
        }

        public double getTotalRevenue() { return totalRevenue; }
        public double getTotalCOGS() { return totalCOGS; }
        public double getTotalExpenses() { return totalExpenses; }
        public double getGrossProfit() { return grossProfit; }
        public double getNetProfit() { return netProfit; }
    }

    // Get manager performance for specific date range
    public static List<ManagerPerformance> getManagerPerformanceByDateRange(
            java.sql.Date startDate, java.sql.Date endDate) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<ManagerPerformance> performances = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();

            String sql = "SELECT " +
                    "m.id as manager_id, " +
                    "m.name as manager_name, " +
                    "COALESCE(SUM(b.total_amount), 0) as total_revenue, " +
                    "COALESCE(SUM(bi.quantity * ci.buying_price), 0) as total_cogs, " +
                    "COALESCE(SUM(e.amount), 0) as total_expenses, " +
                    "COUNT(DISTINCT b.id) as total_bills, " +
                    "COALESCE(SUM(bi.quantity), 0) as total_products_sold " +
                    "FROM users m " +
                    "LEFT JOIN bills b ON m.id = b.manager_id AND b.status = 'COMPLETED' " +
                    "AND DATE(b.bill_date) BETWEEN ? AND ? " +
                    "LEFT JOIN bill_items bi ON b.id = bi.bill_id " +
                    "LEFT JOIN manager_inventory mi ON bi.manager_inventory_id = mi.id " +
                    "LEFT JOIN ceo_inventory ci ON mi.ceo_inventory_id = ci.id " +
                    "LEFT JOIN expenses e ON m.id = e.manager_id " +
                    "AND DATE(e.expense_date) BETWEEN ? AND ? " +
                    "WHERE m.role = 'MANAGER' AND m.status = 'ACTIVE' " +
                    "GROUP BY m.id, m.name " +
                    "ORDER BY total_revenue DESC";

            ps = conn.prepareStatement(sql);
            ps.setDate(1, startDate);
            ps.setDate(2, endDate);
            ps.setDate(3, startDate);
            ps.setDate(4, endDate);
            rs = ps.executeQuery();

            while (rs.next()) {
                ManagerPerformance performance = new ManagerPerformance(
                        rs.getInt("manager_id"),
                        rs.getString("manager_name"),
                        rs.getDouble("total_revenue"),
                        rs.getDouble("total_cogs"),
                        rs.getDouble("total_expenses"),
                        rs.getInt("total_bills"),
                        rs.getInt("total_products_sold")
                );
                performances.add(performance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(rs, ps, conn);
        }
        return performances;
    }
}

