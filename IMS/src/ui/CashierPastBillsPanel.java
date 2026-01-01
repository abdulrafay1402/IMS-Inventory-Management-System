package ui;

import models.*;
import database.CashierInventoryDAO;
import utils.ElegantMessageDialog;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;

// ============= CASHIER PAST BILLS PANEL =============
class CashierPastBillsPanel extends JPanel {
    private Cashier currentUser;
    private JTable billsTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton, viewDetailsButton;
    
    // Teal color for cashier
    private static final Color CASHIER_COLOR = new Color(0, 150, 136);

    public CashierPastBillsPanel(Cashier user) {
        this.currentUser = user;
        initializeUI();
        loadBills();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        JLabel headerLabel = new JLabel("My Past Bills", SwingConstants.LEFT);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 26));
        headerLabel.setForeground(CASHIER_COLOR);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));

        viewDetailsButton = createStyledButton("View Details", new Color(0, 102, 204), Color.WHITE);
        viewDetailsButton.setPreferredSize(new Dimension(180, 48));
        viewDetailsButton.addActionListener(e -> viewBillDetails());
        buttonPanel.add(viewDetailsButton);

        refreshButton = createStyledButton("Refresh", new Color(23, 162, 184), Color.WHITE);
        refreshButton.setPreferredSize(new Dimension(160, 48));
        refreshButton.addActionListener(e -> loadBills());
        buttonPanel.add(refreshButton);

        headerPanel.add(headerLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Bill Number", "Date", "Total Amount", "Status", "Manager"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        billsTable = new JTable(tableModel);
        billsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        billsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        billsTable.setFont(new Font("Arial", Font.PLAIN, 14));
        billsTable.setRowHeight(35);

        JScrollPane scrollPane = new JScrollPane(billsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        add(scrollPane, BorderLayout.CENTER);

        // Summary panel
        JPanel summaryPanel = createSummaryPanel();
        add(summaryPanel, BorderLayout.SOUTH);
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));
        panel.setBackground(new Color(240, 240, 240));

        JLabel totalBillsLabel = new JLabel("Total Bills: 0");
        totalBillsLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel totalSalesLabel = new JLabel("Total Sales: $0.00");
        totalSalesLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalSalesLabel.setForeground(new Color(0, 128, 0));

        panel.add(totalBillsLabel);
        panel.add(new JLabel(" | "));
        panel.add(totalSalesLabel);

        return panel;
    }

    private void loadBills() {
        tableModel.setRowCount(0);
        java.util.List<Bill> bills = CashierInventoryDAO.getCashierBills(currentUser.getId());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        double totalSales = 0;

        for (Bill bill : bills) {
            tableModel.addRow(new Object[]{
                    bill.getBillNumber(),
                    sdf.format(bill.getBillDate()),
                    String.format("$%.2f", bill.getTotalAmount()),
                    bill.getStatus(),
                    bill.getManagerName()
            });
            totalSales += bill.getTotalAmount();
        }

        // Update summary
        updateSummaryPanel(bills.size(), totalSales);
    }

    private void updateSummaryPanel(int totalBills, double totalSales) {
        Component[] components = ((JPanel)getComponent(2)).getComponents();
        if (components.length >= 3) {
            JLabel totalBillsLabel = (JLabel) components[0];
            JLabel totalSalesLabel = (JLabel) components[2];

            totalBillsLabel.setText("Total Bills: " + totalBills);
            totalSalesLabel.setText(String.format("Total Sales: $%.2f", totalSales));
        }
    }

    private void viewBillDetails() {
        int selectedRow = billsTable.getSelectedRow();
        if (selectedRow == -1) {
            ElegantMessageDialog.showMessage(this,
                    "Please select a bill to view details.",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String billNumber = (String) tableModel.getValueAt(selectedRow, 0);
        String date = (String) tableModel.getValueAt(selectedRow, 1);
        String totalAmount = (String) tableModel.getValueAt(selectedRow, 2);
        String manager = (String) tableModel.getValueAt(selectedRow, 4);

        // Get bill ID (we need to store it or fetch from database)
        // For now, show basic details
        showBillDetailsDialog(billNumber, date, totalAmount, manager);
    }

    private void showBillDetailsDialog(String billNumber, String date, String totalAmount, String manager) {
        JDialog dialog = new JDialog((Frame)null, "Bill Details - " + billNumber, true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        // Header panel
        JPanel headerPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        headerPanel.setBackground(Color.WHITE);

        headerPanel.add(createDetailLabel("Bill Number:"));
        headerPanel.add(createDetailValue(billNumber));

        headerPanel.add(createDetailLabel("Date:"));
        headerPanel.add(createDetailValue(date));

        headerPanel.add(createDetailLabel("Manager:"));
        headerPanel.add(createDetailValue(manager));

        headerPanel.add(createDetailLabel("Total Amount:"));
        JLabel totalLabel = createDetailValue(totalAmount);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalLabel.setForeground(new Color(0, 128, 0));
        headerPanel.add(totalLabel);

        dialog.add(headerPanel, BorderLayout.NORTH);

        // Items table (placeholder - you can fetch actual items from database)
        JPanel itemsPanel = new JPanel(new BorderLayout());
        itemsPanel.setBorder(BorderFactory.createTitledBorder("Bill Items"));

        String[] columns = {"Product", "Quantity", "Unit Price", "Subtotal"};
        DefaultTableModel itemsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable itemsTable = new JTable(itemsModel);
        itemsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        itemsTable.setRowHeight(25);

        // Note: You would fetch actual items here using bill ID
        // For now, showing a message
        JLabel noteLabel = new JLabel("<html><center>Bill items details<br>would be displayed here</center></html>", SwingConstants.CENTER);
        noteLabel.setForeground(Color.GRAY);

        JScrollPane scrollPane = new JScrollPane(itemsTable);
        itemsPanel.add(scrollPane, BorderLayout.CENTER);

        dialog.add(itemsPanel, BorderLayout.CENTER);

        // Close button
        JButton closeButton = createStyledButton("Close", new Color(108, 117, 125), Color.WHITE);
        closeButton.addActionListener(e -> dialog.dispose());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
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
    private JLabel createDetailLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        return label;
    }

    private JLabel createDetailValue(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        return label;
    }
}

