package ui;

import javax.swing.*;
import java.awt.*;
import models.Manager;
import database.ManagerDAO;

// ============= MANAGER DASHBOARD OVERVIEW PANEL =============
class ManagerDashboardPanel extends JPanel {
    private Manager currentUser;
    private JLabel totalCashiersLabel, totalProductsLabel, lowStockLabel, pendingRequestsLabel;
    private JLabel totalRevenueLabel, totalExpensesLabel, netProfitLabel;

    public ManagerDashboardPanel(Manager user) {
        this.currentUser = user;
        initializeUI();
        loadDashboardData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header
        JLabel headerLabel = new JLabel("Manager Dashboard Overview", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 26));
        headerLabel.setForeground(new Color(34, 139, 34));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));
        add(headerLabel, BorderLayout.NORTH);

        // Stats Panel
        JPanel statsPanel = createStatsPanel();
        add(statsPanel, BorderLayout.CENTER);

        // Button Panel - Moved up with better spacing
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        JButton refreshButton = createStyledButton("Refresh", new Color(23, 162, 184), Color.WHITE);
        refreshButton.setPreferredSize(new Dimension(180, 48));
        refreshButton.addActionListener(e -> loadDashboardData());

        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    private JButton createStyledButton(String text, Color bgColor, Color textColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Determine color based on mouse presence
                Color paintColor = getModel().isRollover() ? bgColor.darker() : bgColor;
                g2.setColor(paintColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                // Paint text
                super.paintComponent(g);
            }
        };

        // Professional button styling
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(textColor);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        button.setOpaque(false);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
                button.repaint();
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                button.repaint();
            }
        });

        return button;
    }
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // Cashiers Card
        panel.add(createStatCard("Total Cashiers", "0", new Color(34, 139, 34), totalCashiersLabel = new JLabel("0")));

        // Inventory Card
        panel.add(createStatCard("Products in Stock", "0", new Color(0, 102, 204), totalProductsLabel = new JLabel("0")));

        // Revenue Card
        panel.add(createStatCard("Total Revenue", "$0.00", new Color(0, 128, 0), totalRevenueLabel = new JLabel("$0.00")));

        // Expenses Card
        panel.add(createStatCard("Total Expenses", "$0.00", new Color(220, 20, 60), totalExpensesLabel = new JLabel("$0.00")));

        // Low Stock Alert Card
        panel.add(createStatCard("Low Stock Items", "0", new Color(255, 140, 0), lowStockLabel = new JLabel("0")));

        // Net Profit Card
        panel.add(createStatCard("Net Profit", "$0.00", new Color(128, 0, 128), netProfitLabel = new JLabel("$0.00")));

        return panel;
    }

    private JPanel createStatCard(String title, String value, Color color, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(color);

        valueLabel.setFont(new Font("Arial", Font.BOLD, 26));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private void loadDashboardData() {
        // Load cashiers count
        int cashiersCount = ManagerDAO.getCashiersCount(currentUser.getId());
        totalCashiersLabel.setText(String.valueOf(cashiersCount));

        // Load inventory stats
        int productsCount = ManagerDAO.getProductsCount(currentUser.getId());
        totalProductsLabel.setText(String.valueOf(productsCount));

        // Load financial data
        double totalRevenue = ManagerDAO.getManagerRevenue(currentUser.getId());
        double totalExpenses = ManagerDAO.getManagerExpenses(currentUser.getId());
        double netProfit = totalRevenue - totalExpenses;

        totalRevenueLabel.setText(String.format("$%.2f", totalRevenue));
        totalExpensesLabel.setText(String.format("$%.2f", totalExpenses));

        // Color code profit (green for profit, red for loss)
        if (netProfit >= 0) {
            netProfitLabel.setForeground(new Color(0, 128, 0)); // Green
        } else {
            netProfitLabel.setForeground(new Color(220, 20, 60)); // Red
        }
        netProfitLabel.setText(String.format("$%.2f", netProfit));

        // Load low stock count
        int lowStockCount = ManagerDAO.getLowStockCount(currentUser.getId());
        lowStockLabel.setText(String.valueOf(lowStockCount));

        // Removed automatic warning messages - user can check dashboard stats instead
    }

}

