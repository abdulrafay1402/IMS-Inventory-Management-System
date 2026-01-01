package ui;

import javax.swing.*;
import java.awt.*;
import models.CEO;
import models.Manager;
import models.CEOInventory;
import models.CashierRequest;
import database.UserDAO;
import database.InventoryDAO;
import database.BillDAO;
import database.ExpenseDAO;

// ============= UPDATED CEO DASHBOARD OVERVIEW PANEL =============
class CEODashboardPanel extends JPanel {
    private CEO currentUser;
    private JLabel totalManagersLabel, totalProductsLabel, lowStockLabel, pendingRequestsLabel;
    private JLabel totalRevenueLabel, totalExpensesLabel, netProfitLabel;

    public CEODashboardPanel(CEO user) {
        this.currentUser = user;
        initializeUI();
        loadDashboardData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int padding = Math.max(25, Math.min(screenSize.width, screenSize.height) / 30);
        setBorder(BorderFactory.createEmptyBorder(padding, padding, padding, padding));

        // Header - Responsive
        int headerFontSize = Math.max(24, screenSize.width / 55);
        JLabel headerLabel = new JLabel("CEO Dashboard Overview", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, headerFontSize));
        headerLabel.setForeground(new Color(0, 102, 204));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, Math.max(20, screenSize.height / 40), 0));
        add(headerLabel, BorderLayout.NORTH);

        // Stats Panel - Centered and responsive
        JPanel statsPanel = createStatsPanel();
        add(statsPanel, BorderLayout.CENTER);

        // Button Panel - Centered refresh button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(Math.max(15, screenSize.height / 50), 0, 0, 0));
        
        int buttonWidth = Math.max(160, screenSize.width / 15);
        int buttonHeight = Math.max(42, screenSize.height / 25);
        JButton refreshButton = createStyledButton("Refresh", new Color(23, 162, 184), Color.WHITE);
        refreshButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
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
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int gap = Math.max(12, Math.min(screenSize.width, screenSize.height) / 60);
        int panelPadding = Math.max(20, Math.min(screenSize.width, screenSize.height) / 35);
        
        JPanel panel = new JPanel(new GridLayout(4, 2, gap, gap));
        panel.setBorder(BorderFactory.createEmptyBorder(panelPadding, panelPadding, panelPadding, panelPadding));

        // Manager Stats Card
        panel.add(createStatCard("Total Managers", "0", new Color(0, 102, 204), totalManagersLabel = new JLabel("0")));

        // Inventory Stats Card
        panel.add(createStatCard("Total Products", "0", new Color(34, 139, 34), totalProductsLabel = new JLabel("0")));

        // Revenue Card
        panel.add(createStatCard("Total Revenue", "$0.00", new Color(0, 128, 0), totalRevenueLabel = new JLabel("$0.00")));

        // Expenses Card
        panel.add(createStatCard("Total Expenses", "$0.00", new Color(220, 20, 60), totalExpensesLabel = new JLabel("$0.00")));

        // Low Stock Alert Card
        panel.add(createStatCard("Low Stock Items", "0", new Color(255, 140, 0), lowStockLabel = new JLabel("0")));

        // Pending Requests Card
        panel.add(createStatCard("Pending Cashier Requests", "0", new Color(128, 0, 128), pendingRequestsLabel = new JLabel("0")));

        // Net Profit Card
        panel.add(createStatCard("Net Profit", "$0.00", new Color(0, 102, 204), netProfitLabel = new JLabel("$0.00")));

        return panel;
    }

    private JPanel createStatCard(String title, String value, Color color, JLabel valueLabel) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int cardPadding = Math.max(12, Math.min(screenSize.width, screenSize.height) / 50);
        int titleFontSize = Math.max(14, screenSize.width / 80);
        int valueFontSize = Math.max(22, screenSize.width / 50);
        
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(cardPadding, cardPadding, cardPadding, cardPadding)
        ));
        card.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, titleFontSize));
        titleLabel.setForeground(color);

        valueLabel.setFont(new Font("Arial", Font.BOLD, valueFontSize));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private void loadDashboardData() {
        // Load managers count
        java.util.List<Manager> managers = UserDAO.getAllManagers();
        totalManagersLabel.setText(String.valueOf(managers.size()));

        // Load inventory stats
        java.util.List<CEOInventory> inventory = InventoryDAO.getAllCEOInventory();
        totalProductsLabel.setText(String.valueOf(inventory.size()));

        // Load financial data
        double totalRevenue = BillDAO.getTotalRevenue();
        double totalExpenses = ExpenseDAO.getTotalExpenses();
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
        java.util.List<CEOInventory> lowStock = InventoryDAO.getLowStockCEOInventory();
        lowStockLabel.setText(String.valueOf(lowStock.size()));

        // Load pending requests count
        java.util.List<CashierRequest> pendingRequests = UserDAO.getPendingCashierRequests();
        pendingRequestsLabel.setText(String.valueOf(pendingRequests.size()));

        // Removed automatic warning messages - user can check dashboard stats instead
    }

}

