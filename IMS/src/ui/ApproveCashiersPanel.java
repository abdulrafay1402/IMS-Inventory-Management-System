package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import models.CashierRequest;
import database.UserDAO;
import utils.ElegantMessageDialog;

// Approve Cashiers Panel
class ApproveCashiersPanel extends JPanel {
    private JTable requestsTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton;
    private JButton approveButton;
    private JButton rejectButton;

    public ApproveCashiersPanel() {
        initializeUI();
        loadPendingRequests();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        JLabel headerLabel = new JLabel("Pending Cashier Requests", SwingConstants.LEFT);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 26));
        headerLabel.setForeground(new Color(0, 102, 204));

        refreshButton = createStyledButton("Refresh", new Color(23, 162, 184), Color.WHITE);
        refreshButton.setPreferredSize(new Dimension(160, 48));
        refreshButton.addActionListener(e -> loadPendingRequests());

        headerPanel.add(headerLabel, BorderLayout.WEST);
        headerPanel.add(refreshButton, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Request ID", "Cashier Name", "Username", "Phone", "CNIC", "Manager", "Request Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        requestsTable = new JTable(tableModel);
        requestsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        requestsTable.setFont(new Font("Arial", Font.PLAIN, 14));
        requestsTable.setRowHeight(35);
        
        // Configure header with proper visibility
        javax.swing.table.JTableHeader header = requestsTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 16));
        header.setBackground(new Color(0, 102, 204)); // CEO Blue
        header.setForeground(Color.WHITE);
        header.setOpaque(true);
        
        requestsTable.getTableHeader().setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(value.toString());
                label.setFont(new Font("Arial", Font.BOLD, 16));
                label.setBackground(new Color(0, 102, 204));
                label.setForeground(Color.WHITE);
                label.setOpaque(true);
                label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                label.setHorizontalAlignment(CENTER);
                return label;
            }
        });

        JScrollPane scrollPane = new JScrollPane(requestsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        add(scrollPane, BorderLayout.CENTER);

        // Button Panel - Moved up with better spacing
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        approveButton = createStyledButton("Approve", new Color(0, 153, 51), Color.WHITE);
        approveButton.setPreferredSize(new Dimension(180, 48));
        approveButton.addActionListener(e -> approveRequest());

        rejectButton = createStyledButton("Reject", new Color(220, 20, 60), Color.WHITE);
        rejectButton.setPreferredSize(new Dimension(180, 48));
        rejectButton.addActionListener(e -> rejectRequest());

        buttonPanel.add(approveButton);
        buttonPanel.add(rejectButton);
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
    private void loadPendingRequests() {
        tableModel.setRowCount(0);
        List<CashierRequest> requests = UserDAO.getPendingCashierRequests();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (CashierRequest request : requests) {
            tableModel.addRow(new Object[]{
                    request.getId(),
                    request.getCashierName(),
                    request.getUsername(),
                    request.getPhone(),
                    request.getCnic(),
                    request.getManagerName(),
                    sdf.format(request.getRequestDate())
            });
        }
    }

    private void approveRequest() {
        int selectedRow = requestsTable.getSelectedRow();
        if (selectedRow == -1) {
            ElegantMessageDialog.showMessage(this,
                    "Please select a request to approve.",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int requestId = (int) tableModel.getValueAt(selectedRow, 0);
        String cashierName = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = ElegantMessageDialog.showConfirm(this,
                "Are you sure you want to approve cashier: " + cashierName + "?",
                "Confirm Approval");

        if (confirm == JOptionPane.YES_OPTION) {
            if (UserDAO.updateCashierRequestStatus(requestId, true, null)) {
                loadPendingRequests();
            } else {
                ElegantMessageDialog.showMessage(this,
                        "Failed to approve cashier. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void rejectRequest() {
        int selectedRow = requestsTable.getSelectedRow();
        if (selectedRow == -1) {
            ElegantMessageDialog.showMessage(this,
                    "Please select a request to reject.",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int requestId = (int) tableModel.getValueAt(selectedRow, 0);
        String cashierName = (String) tableModel.getValueAt(selectedRow, 1);

        String reason = JOptionPane.showInputDialog(this,
                "Enter rejection reason for " + cashierName + ":",
                "Rejection Reason",
                JOptionPane.QUESTION_MESSAGE);

        if (reason != null && !reason.trim().isEmpty()) {
            if (UserDAO.updateCashierRequestStatus(requestId, false, reason.trim())) {
                ElegantMessageDialog.showMessage(this,
                        "Cashier request rejected!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loadPendingRequests();
            } else {
                ElegantMessageDialog.showMessage(this,
                        "Failed to reject request. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

