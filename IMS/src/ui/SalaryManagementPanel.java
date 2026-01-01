package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import database.SalaryDAO;
import database.NotificationDAO;
import models.SalaryPayment;
import models.User;

public class SalaryManagementPanel extends JPanel {
    private User currentUser;
    private JTable salaryTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JComboBox<String> monthComboBox;
    private JButton processSalariesButton;
    private static final Color CEO_COLOR = new Color(0, 102, 204);

    public SalaryManagementPanel(User user) {
        this.currentUser = user;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Salary Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 102, 204));

        statusLabel = new JLabel("");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusLabel.setForeground(new Color(100, 100, 100));

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(statusLabel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Control Panel
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.SOUTH);

        // Table Panel
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);

        // Load current month data
        loadSalaryData();
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(15, 0, 0, 0)
        ));

        // Month selector
        JLabel monthLabel = new JLabel("Select Month:");
        monthLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Generate months (current and previous 11 months)
        String[] months = new String[12];
        YearMonth currentYearMonth = YearMonth.now();
        for (int i = 0; i < 12; i++) {
            YearMonth ym = currentYearMonth.minusMonths(i);
            months[i] = ym.format(DateTimeFormatter.ofPattern("yyyy-MM")) + " (" + 
                       ym.format(DateTimeFormatter.ofPattern("MMMM yyyy")) + ")";
        }
        
        monthComboBox = new JComboBox<>(months);
        monthComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        monthComboBox.setPreferredSize(new Dimension(250, 35));
        monthComboBox.addActionListener(e -> loadSalaryData());

        // Process Salaries Button
        processSalariesButton = new JButton("Process Monthly Salaries");
        processSalariesButton.setFont(new Font("Arial", Font.BOLD, 14));
        processSalariesButton.setBackground(CEO_COLOR);
        processSalariesButton.setForeground(Color.WHITE);
        processSalariesButton.setFocusPainted(false);
        processSalariesButton.setBorderPainted(false);
        processSalariesButton.setPreferredSize(new Dimension(250, 35));
        processSalariesButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        processSalariesButton.addActionListener(e -> processSalaries());

        // Pay Selected Button
        JButton paySelectedButton = new JButton("Pay Selected");
        paySelectedButton.setFont(new Font("Arial", Font.BOLD, 14));
        paySelectedButton.setBackground(new Color(40, 167, 69));
        paySelectedButton.setForeground(Color.WHITE);
        paySelectedButton.setFocusPainted(false);
        paySelectedButton.setBorderPainted(false);
        paySelectedButton.setPreferredSize(new Dimension(150, 35));
        paySelectedButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        paySelectedButton.addActionListener(e -> paySelectedEmployee());

        // Refresh Button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14));
        refreshButton.setBackground(new Color(23, 162, 184));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorderPainted(false);
        refreshButton.setPreferredSize(new Dimension(120, 35));
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> loadSalaryData());

        panel.add(monthLabel);
        panel.add(monthComboBox);
        panel.add(processSalariesButton);
        panel.add(paySelectedButton);
        panel.add(refreshButton);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        String[] columns = {"Employee ID", "Name", "Role", "Joining Date", "Salary", "Payment Date", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        salaryTable = new JTable(tableModel);
        salaryTable.setFont(new Font("Arial", Font.PLAIN, 14));
        salaryTable.setRowHeight(35);
        salaryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        salaryTable.setGridColor(new Color(220, 220, 220));

        // Configure header
        javax.swing.table.JTableHeader header = salaryTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 16));
        header.setBackground(CEO_COLOR);
        header.setForeground(Color.WHITE);
        header.setOpaque(true);

        salaryTable.getTableHeader().setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(value.toString());
                label.setFont(new Font("Arial", Font.BOLD, 16));
                label.setBackground(CEO_COLOR);
                label.setForeground(Color.WHITE);
                label.setOpaque(true);
                label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                label.setHorizontalAlignment(CENTER);
                return label;
            }
        });

        JScrollPane scrollPane = new JScrollPane(salaryTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadSalaryData() {
        tableModel.setRowCount(0);
        
        String selectedMonth = getSelectedMonth();
        if (selectedMonth == null) return;

        // Check if salaries are already paid for this month
        boolean isPaid = SalaryDAO.areSalariesPaidForMonth(selectedMonth);
        
        if (isPaid) {
            // Load existing payment records
            java.util.List<SalaryPayment> payments = SalaryDAO.getSalaryPaymentsByMonth(selectedMonth);
            
            for (SalaryPayment payment : payments) {
                // Get joining date for the user
                String joiningDate = database.UserDAO.getJoiningDate(payment.getUserId());
                tableModel.addRow(new Object[]{
                    payment.getUserId(),
                    payment.getUserName(),
                    payment.getUserRole(),
                    joiningDate != null ? joiningDate : "N/A",
                    String.format("Rs. %.2f", payment.getAmount()),
                    payment.getPaymentDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")),
                    payment.getStatus()
                });
            }
            
            statusLabel.setText("Salaries processed (" + payments.size() + " employees)");
            statusLabel.setForeground(new Color(0, 150, 0));
            processSalariesButton.setEnabled(false);
            processSalariesButton.setText("Already Processed");
        } else {
            // Load pending employees
            java.util.List<SalaryDAO.UserSalaryInfo> pendingEmployees = 
                SalaryDAO.getPendingEmployeesForMonth(selectedMonth);
            
            for (SalaryDAO.UserSalaryInfo emp : pendingEmployees) {
                tableModel.addRow(new Object[]{
                    emp.getId(),
                    emp.getName(),
                    emp.getRole(),
                    emp.getJoiningDate() != null ? emp.getJoiningDate() : "N/A",
                    String.format("Rs. %.2f", emp.getSalary()),
                    "Pending",
                    "NOT PAID"
                });
            }
            
            if (pendingEmployees.isEmpty()) {
                statusLabel.setText("No employees with salary configured");
                statusLabel.setForeground(new Color(150, 150, 150));
                processSalariesButton.setEnabled(false);
            } else {
                statusLabel.setText(pendingEmployees.size() + " employees pending payment");
                statusLabel.setForeground(new Color(255, 140, 0));
                processSalariesButton.setEnabled(true);
                processSalariesButton.setText("Process Monthly Salaries");
            }
        }
    }

    private String getSelectedMonth() {
        String selected = (String) monthComboBox.getSelectedItem();
        if (selected == null) return null;
        // Extract YYYY-MM from "YYYY-MM (Month Year)"
        return selected.substring(0, 7);
    }

    private void processSalaries() {
        String selectedMonth = getSelectedMonth();
        if (selectedMonth == null) return;

        // Confirm with user
        int confirm = JOptionPane.showConfirmDialog(this,
            "Process salary payments for all employees for " + selectedMonth + "?\n" +
            "This will create expense entries and cannot be undone.",
            "Confirm Salary Processing",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Process salaries
        boolean success = SalaryDAO.processMonthlySalaries(currentUser.getId(), selectedMonth);

        if (success) {
            JOptionPane.showMessageDialog(this,
                "Monthly salaries processed successfully!\n" +
                "Expense entries have been created.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

            // Create notification for CEO about salary expenses
            int ceoId = database.UserDAO.getCEOUserId();
            if (ceoId > 0) {
                NotificationDAO.createNotification(
                    ceoId,
                    "SALARY_PROCESSED",
                    "Monthly salaries processed for " + selectedMonth,
                    "Manager " + currentUser.getName() + " has processed monthly salary payments. Check expenses for details.",
                    String.valueOf(currentUser.getId())
                );
            }

            loadSalaryData();
        } else {
            JOptionPane.showMessageDialog(this,
                "Failed to process salaries. Please check the logs.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void paySelectedEmployee() {
        int selectedRow = salaryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select an employee to pay.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String selectedMonth = getSelectedMonth();
        if (selectedMonth == null) return;

        // Get employee details from table
        int employeeId = (int) tableModel.getValueAt(selectedRow, 0);
        String employeeName = (String) tableModel.getValueAt(selectedRow, 1);
        String employeeRole = (String) tableModel.getValueAt(selectedRow, 2);
        String salaryStr = (String) tableModel.getValueAt(selectedRow, 4);
        String status = (String) tableModel.getValueAt(selectedRow, 6);

        // Check if already paid
        if (!"NOT PAID".equals(status)) {
            JOptionPane.showMessageDialog(this,
                "This employee has already been paid for " + selectedMonth + ".",
                "Already Paid",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Extract base salary amount
        String baseSalaryStr = salaryStr.replace("Rs. ", "").replace(",", "");
        double baseSalary = Double.parseDouble(baseSalaryStr);

        // Show payment dialog with bonus and adjustment options
        IndividualPaymentDialog dialog = new IndividualPaymentDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this),
            employeeName,
            employeeRole,
            baseSalary,
            selectedMonth
        );
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            double bonus = dialog.getBonus();
            double adjustment = dialog.getAdjustment();
            double finalAmount = baseSalary + bonus + adjustment;

            // Process individual salary payment with adjustments
            boolean success = SalaryDAO.processSingleSalaryWithAdjustments(
                currentUser.getId(), employeeId, selectedMonth, baseSalary, bonus, adjustment, finalAmount
            );

            if (success) {
                String breakdown = "Base: Rs. " + String.format("%,.2f", baseSalary);
                if (bonus > 0) breakdown += "\nBonus: Rs. " + String.format("%,.2f", bonus);
                if (adjustment != 0) {
                    breakdown += (adjustment > 0 ? "\nIncrement: Rs. " : "\nDecrement: Rs. ") + 
                                String.format("%,.2f", Math.abs(adjustment));
                }
                breakdown += "\nFinal Amount: Rs. " + String.format("%,.2f", finalAmount);

                JOptionPane.showMessageDialog(this,
                    "Salary paid successfully to " + employeeName + "!\n\n" + breakdown + "\n\nMonth: " + selectedMonth,
                    "Payment Successful",
                    JOptionPane.INFORMATION_MESSAGE);

                // Create notification for CEO
                int ceoId = database.UserDAO.getCEOUserId();
                if (ceoId > 0) {
                    String notifBreakdown = "Rs. " + String.format("%,.2f", finalAmount);
                    if (bonus > 0 || adjustment != 0) {
                        notifBreakdown += " (with adjustments)";
                    }
                    NotificationDAO.createNotification(
                        ceoId,
                        "SALARY_PAID",
                        "Individual salary paid for " + selectedMonth,
                        "Salary paid to " + employeeName + " (" + employeeRole + ") - " + notifBreakdown + " by " + currentUser.getName(),
                        String.valueOf(employeeId)
                    );
                }

                loadSalaryData();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to process salary payment.\n" +
                    "Possible reasons:\n" +
                    "- Employee not eligible for this month (check joining date)\n" +
                    "- Payment already exists\n" +
                    "- Database error",
                    "Payment Failed",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
