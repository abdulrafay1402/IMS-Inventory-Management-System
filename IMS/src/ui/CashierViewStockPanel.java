package ui;

import models.*;
import database.CashierInventoryDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

// ============= CASHIER VIEW STOCK PANEL =============
class CashierViewStockPanel extends JPanel {
    private Cashier currentUser;
    private JTable stockTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton;
    
    // Teal color for cashier
    private static final Color CASHIER_COLOR = new Color(0, 150, 136);

    public CashierViewStockPanel(Cashier user) {
        this.currentUser = user;
        initializeUI();
        loadStock();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        JLabel headerLabel = new JLabel("Available Stock", SwingConstants.LEFT);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 26));
        headerLabel.setForeground(CASHIER_COLOR);

        refreshButton = createStyledButton("Refresh", new Color(23, 162, 184), Color.WHITE);
        refreshButton.setPreferredSize(new Dimension(160, 48));
        refreshButton.addActionListener(e -> loadStock());

        headerPanel.add(headerLabel, BorderLayout.WEST);
        headerPanel.add(refreshButton, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Product Name", "Selling Price", "Available Quantity", "Stock Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        stockTable = new JTable(tableModel);
        stockTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        stockTable.setFont(new Font("Arial", Font.PLAIN, 14));
        stockTable.setRowHeight(35);
        
        // Configure header with proper visibility
        javax.swing.table.JTableHeader header = stockTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 16));
        header.setBackground(new Color(0, 150, 136)); // Cashier Teal
        header.setForeground(Color.WHITE);
        header.setOpaque(true);
        
        stockTable.getTableHeader().setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(value.toString());
                label.setFont(new Font("Arial", Font.BOLD, 16));
                label.setBackground(new Color(0, 150, 136)); // Cashier Teal
                label.setForeground(Color.WHITE);
                label.setOpaque(true);
                label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                label.setHorizontalAlignment(CENTER);
                return label;
            }
        });

        // Color code rows based on stock status
        stockTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                String status = (String) table.getValueAt(row, 3);
                if ("OUT OF STOCK".equals(status)) {
                    c.setBackground(new Color(255, 200, 200)); // Light red
                } else if ("LOW STOCK".equals(status)) {
                    c.setBackground(new Color(255, 255, 200)); // Light yellow
                } else {
                    c.setBackground(Color.WHITE);
                }

                if (isSelected) {
                    c.setBackground(new Color(200, 200, 255)); // Light blue for selection
                }

                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(stockTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        add(scrollPane, BorderLayout.CENTER);

        // Summary panel
        JPanel summaryPanel = createSummaryPanel();
        add(summaryPanel, BorderLayout.SOUTH);
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
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));
        panel.setBackground(new Color(240, 240, 240));

        JLabel totalLabel = new JLabel("Total Products: 0");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel availableLabel = new JLabel("Available: 0");
        availableLabel.setFont(new Font("Arial", Font.BOLD, 16));
        availableLabel.setForeground(new Color(0, 128, 0));

        JLabel lowStockLabel = new JLabel("Low Stock: 0");
        lowStockLabel.setFont(new Font("Arial", Font.BOLD, 16));
        lowStockLabel.setForeground(Color.ORANGE);

        panel.add(totalLabel);
        panel.add(new JLabel(" | "));
        panel.add(availableLabel);
        panel.add(new JLabel(" | "));
        panel.add(lowStockLabel);

        return panel;
    }

    private void loadStock() {
        tableModel.setRowCount(0);

        java.util.List<ManagerInventory> inventory = CashierInventoryDAO.getAvailableProductsForCashier(currentUser.getId());

        int totalProducts = 0;
        int availableProducts = 0;
        int lowStockProducts = 0;

        for (ManagerInventory product : inventory) {
            String status = product.getStockStatus();

            tableModel.addRow(new Object[]{
                    product.getProductName(),
                    String.format("$%.2f", product.getSellingPrice()),
                    product.getCurrentQuantity(),
                    status
            });

            totalProducts++;
            if (product.getCurrentQuantity() > 0) {
                availableProducts++;
            }
            if (product.isLowStock()) {
                lowStockProducts++;
            }
        }

        // Update summary
        updateSummaryPanel(totalProducts, availableProducts, lowStockProducts);
    }

    private void updateSummaryPanel(int total, int available, int lowStock) {
        Component[] components = ((JPanel)getComponent(2)).getComponents();
        if (components.length >= 5) {
            JLabel totalLabel = (JLabel) components[0];
            JLabel availableLabel = (JLabel) components[2];
            JLabel lowStockLabel = (JLabel) components[4];

            totalLabel.setText("Total Products: " + total);
            availableLabel.setText("Available: " + available);
            lowStockLabel.setText("Low Stock: " + lowStock);
        }
    }
}

