package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import models.Manager;
import models.ManagerInventory;
import database.ManagerInventoryDAO;

// ============= MANAGER INVENTORY PANEL =============
class ManagerInventoryPanel extends JPanel {
    private Manager currentUser;
    private JTable inventoryTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton;

    public ManagerInventoryPanel(Manager user) {
        this.currentUser = user;
        initializeUI();
        loadInventory();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        JLabel headerLabel = new JLabel("My Inventory", SwingConstants.LEFT);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 26));
        headerLabel.setForeground(new Color(34, 139, 34));
        refreshButton = createStyledButton("Refresh", new Color(23, 162, 184), Color.WHITE);
        refreshButton.setPreferredSize(new Dimension(160, 48));
        refreshButton.addActionListener(e -> loadInventory());

        headerPanel.add(headerLabel, BorderLayout.WEST);
        headerPanel.add(refreshButton, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Product Name", "Buying Price", "Selling Price", "Current Quantity", "Min Stock", "Status", "Last Updated"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        inventoryTable = new JTable(tableModel);
        inventoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        inventoryTable.setFont(new Font("Arial", Font.PLAIN, 14));
        inventoryTable.setRowHeight(35);
        
        // Configure header with proper visibility
        javax.swing.table.JTableHeader header = inventoryTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 16));
        header.setBackground(new Color(34, 139, 34)); // Manager Green
        header.setForeground(Color.WHITE);
        header.setOpaque(true);
        
        inventoryTable.getTableHeader().setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(value.toString());
                label.setFont(new Font("Arial", Font.BOLD, 16));
                label.setBackground(new Color(34, 139, 34));
                label.setForeground(Color.WHITE);
                label.setOpaque(true);
                label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                label.setHorizontalAlignment(CENTER);
                return label;
            }
        });

        // Color code rows based on stock status
        inventoryTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                String status = (String) table.getValueAt(row, 5);
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

        JScrollPane scrollPane = new JScrollPane(inventoryTable);
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

        JLabel lowStockLabel = new JLabel("Low Stock Items: 0");
        lowStockLabel.setFont(new Font("Arial", Font.BOLD, 16));
        lowStockLabel.setForeground(Color.RED);

        panel.add(totalLabel);
        panel.add(new JLabel(" | "));
        panel.add(lowStockLabel);

        return panel;
    }

    private void loadInventory() {
        tableModel.setRowCount(0);
        java.util.List<ManagerInventory> inventory = ManagerInventoryDAO.getManagerInventory(currentUser.getId());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        int totalProducts = 0;
        int lowStockCount = 0;

        for (ManagerInventory item : inventory) {
            String status = item.getStockStatus();
            if (item.isLowStock()) {
                lowStockCount++;
            }
            totalProducts++;

            tableModel.addRow(new Object[]{
                    item.getProductName(),
                    String.format("$%.2f", item.getBuyingPrice()),
                    String.format("$%.2f", item.getSellingPrice()),
                    item.getCurrentQuantity(),
                    item.getMinStockLevel(),
                    status,
                    sdf.format(item.getLastUpdated())
            });
        }

        // Update summary panel
        updateSummaryPanel(totalProducts, lowStockCount);
    }

    private void updateSummaryPanel(int totalProducts, int lowStockCount) {
        Component[] components = ((JPanel)getComponent(2)).getComponents();
        if (components.length >= 3) {
            JLabel totalLabel = (JLabel) components[0];
            JLabel lowStockLabel = (JLabel) components[2];

            totalLabel.setText("Total Products: " + totalProducts);
            lowStockLabel.setText("Low Stock Items: " + lowStockCount);

            if (lowStockCount > 0) {
                lowStockLabel.setForeground(Color.RED);
            } else {
                lowStockLabel.setForeground(Color.BLACK);
            }
        }
    }
}

