package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import models.Manager;
import models.Cashier;
import models.CashierRequest;
import database.ManagerDAO;

// ============= VIEW CASHIERS PANEL =============
class ViewCashiersPanel extends JPanel {
    private Manager currentUser;
    private JTable cashiersTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton;
    private JComboBox<String> statusFilterCombo;

    public ViewCashiersPanel(Manager user) {
        this.currentUser = user;
        initializeUI();
        loadCashiers();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        JLabel headerLabel = new JLabel("My Cashiers", SwingConstants.LEFT);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 26));
        headerLabel.setForeground(new Color(34, 139, 34));

        // Filter and buttons panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));

        // Status filter
        JLabel filterLabel = new JLabel("Filter by Status:");
        filterLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        controlPanel.add(filterLabel);
        String[] statusFilters = {"All", "ACTIVE", "PENDING_APPROVAL", "REJECTED"};
        statusFilterCombo = new JComboBox<>(statusFilters);
        statusFilterCombo.setFont(new Font("Arial", Font.PLAIN, 16));
        statusFilterCombo.setPreferredSize(new Dimension(180, 40));
        statusFilterCombo.addActionListener(e -> loadCashiers());
        controlPanel.add(statusFilterCombo);

        refreshButton = createStyledButton("Refresh", new Color(23, 162, 184), Color.WHITE);
        refreshButton.setPreferredSize(new Dimension(160, 48));
        refreshButton.addActionListener(e -> loadCashiers());
        controlPanel.add(refreshButton);

        headerPanel.add(headerLabel, BorderLayout.WEST);
        headerPanel.add(controlPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Name", "Username", "Phone", "CNIC", "Salary (Rs.)", "Status", "Join Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        cashiersTable = new JTable(tableModel);
        cashiersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cashiersTable.setFont(new Font("Arial", Font.PLAIN, 14));
        cashiersTable.setRowHeight(35);
        
        // Configure header with proper visibility
        javax.swing.table.JTableHeader header = cashiersTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 16));
        header.setBackground(new Color(0, 102, 204)); // CEO Blue
        header.setForeground(Color.WHITE);
        header.setOpaque(true);
        
        cashiersTable.getTableHeader().setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(value.toString());
                label.setFont(new Font("Arial", Font.BOLD, 16));
                label.setBackground(new Color(0, 102, 204)); // CEO Blue
                label.setForeground(Color.WHITE);
                label.setOpaque(true);
                label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                label.setHorizontalAlignment(CENTER);
                return label;
            }
        });

        // Color code rows based on status
        cashiersTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                String status = (String) table.getValueAt(row, 4);
                switch (status) {
                    case "ACTIVE":
                        c.setBackground(new Color(200, 255, 200)); // Light green
                        break;
                    case "PENDING_APPROVAL":
                        c.setBackground(new Color(255, 255, 200)); // Light yellow
                        break;
                    case "REJECTED":
                        c.setBackground(new Color(255, 200, 200)); // Light red
                        break;
                    default:
                        c.setBackground(Color.WHITE);
                }

                if (isSelected) {
                    c.setBackground(new Color(200, 200, 255)); // Light blue for selection
                }

                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(cashiersTable);
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

        JLabel totalLabel = new JLabel("Total Cashiers: 0");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel activeLabel = new JLabel("Active: 0");
        activeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        activeLabel.setForeground(new Color(0, 128, 0));

        JLabel pendingLabel = new JLabel("Pending: 0");
        pendingLabel.setFont(new Font("Arial", Font.BOLD, 16));
        pendingLabel.setForeground(Color.ORANGE);

        panel.add(totalLabel);
        panel.add(new JLabel(" | "));
        panel.add(activeLabel);
        panel.add(new JLabel(" | "));
        panel.add(pendingLabel);

        return panel;
    }

    private void loadCashiers() {
        tableModel.setRowCount(0);

        String selectedStatus = (String) statusFilterCombo.getSelectedItem();
        java.util.List<Cashier> allCashiers = ManagerDAO.getApprovedCashiers(currentUser.getId());
        java.util.List<CashierRequest> pendingRequests = ManagerDAO.getPendingCashierRequests(currentUser.getId());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        int totalCount = 0;
        int activeCount = 0;
        int pendingCount = 0;

        // Add active cashiers
        for (Cashier cashier : allCashiers) {
            if (!"All".equals(selectedStatus) && !"ACTIVE".equals(selectedStatus)) {
                continue;
            }

            tableModel.addRow(new Object[]{
                    cashier.getName(),
                    cashier.getUsername(),
                    cashier.getPhone(),
                    cashier.getCnic(),
                    String.format("%,.2f", cashier.getSalary()),
                    "ACTIVE",
                    "Assigned" // You might want to store actual join date
            });
            totalCount++;
            activeCount++;
        }

        // Add pending requests
        for (CashierRequest request : pendingRequests) {
            if (!"All".equals(selectedStatus) && !"PENDING_APPROVAL".equals(selectedStatus)) {
                continue;
            }

            tableModel.addRow(new Object[]{
                    request.getCashierName(),
                    request.getUsername(),
                    request.getPhone(),
                    request.getCnic(),
                    "N/A", // Salary not available for pending requests
                    "PENDING_APPROVAL",
                    sdf.format(request.getRequestDate())
            });
            totalCount++;
            pendingCount++;
        }

        // Update summary panel
        updateSummaryPanel(totalCount, activeCount, pendingCount);
    }

    private void updateSummaryPanel(int total, int active, int pending) {
        Component[] components = ((JPanel)getComponent(2)).getComponents();
        if (components.length >= 5) {
            JLabel totalLabel = (JLabel) components[0];
            JLabel activeLabel = (JLabel) components[2];
            JLabel pendingLabel = (JLabel) components[4];

            totalLabel.setText("Total Cashiers: " + total);
            activeLabel.setText("Active: " + active);
            pendingLabel.setText("Pending: " + pending);
        }
    }
}

