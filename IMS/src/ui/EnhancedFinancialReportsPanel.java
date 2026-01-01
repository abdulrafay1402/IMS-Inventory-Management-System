package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import database.EnhancedProfitDAO;
import utils.ElegantMessageDialog;

// ============= ENHANCED FINANCIAL REPORTS PANEL =============
class EnhancedFinancialReportsPanel extends JPanel {
    private JLabel totalRevenueLabel, totalCOGSLabel, grossProfitLabel;
    private JLabel totalExpensesLabel, netProfitLabel, profitMarginLabel;
    private JTable reportsTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton, exportButton;
    private JComboBox<String> dateFilterCombo;

    public EnhancedFinancialReportsPanel() {
        initializeUI();
        loadFinancialData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int padding = Math.max(20, Math.min(screenSize.width, screenSize.height) / 30);
        setBorder(BorderFactory.createEmptyBorder(padding, padding, padding, padding));

        // Header - Responsive styling
        int headerFontSize = Math.max(22, screenSize.width / 60);
        JLabel headerLabel = new JLabel("Financial Reports Dashboard", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, headerFontSize));
        headerLabel.setForeground(new Color(0, 102, 204)); // Standard blue color
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, Math.max(20, screenSize.height / 40), 0));
        add(headerLabel, BorderLayout.NORTH);

        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Financial summary panel
        JPanel summaryPanel = createFinancialSummaryPanel();
        mainPanel.add(summaryPanel, BorderLayout.NORTH);

        // Reports table
        JPanel tablePanel = createReportsTablePanel();
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // Control panel
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.SOUTH);
    }

    private JPanel createFinancialSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 50, 20, 50));

        // Row 1: Revenue, COGS, Gross Profit
        panel.add(createFinancialCard("Total Revenue", "$0.00", new Color(0, 128, 0),
                totalRevenueLabel = new JLabel("$0.00")));
        panel.add(createFinancialCard("Cost of Goods Sold", "$0.00", new Color(255, 140, 0),
                totalCOGSLabel = new JLabel("$0.00")));
        panel.add(createFinancialCard("Gross Profit", "$0.00", new Color(0, 102, 204),
                grossProfitLabel = new JLabel("$0.00")));

        // Row 2: Expenses, Net Profit, Profit Margin
        panel.add(createFinancialCard("Total Expenses", "$0.00", new Color(220, 20, 60),
                totalExpensesLabel = new JLabel("$0.00")));
        panel.add(createFinancialCard("Net Profit", "$0.00", new Color(34, 139, 34),
                netProfitLabel = new JLabel("$0.00")));
        panel.add(createFinancialCard("Profit Margin", "0%", new Color(128, 0, 128),
                profitMarginLabel = new JLabel("0%")));

        return panel;
    }

    private JPanel createFinancialCard(String title, String value, Color color, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(color);

        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createReportsTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel tableLabel = new JLabel("Manager Performance Report (Revenue - COGS - Expenses = Net Profit)", SwingConstants.LEFT);
        tableLabel.setFont(new Font("Arial", Font.BOLD, 18));
        tableLabel.setForeground(new Color(0, 102, 204));
        tableLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(tableLabel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Manager", "Revenue", "COGS", "Gross Profit", "Expenses", "Net Profit", "Margin %", "Bills", "Items Sold"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reportsTable = new JTable(tableModel);
        reportsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reportsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        reportsTable.setFont(new Font("Arial", Font.PLAIN, 14));
        reportsTable.setRowHeight(35);

        // Color code profit/loss
        reportsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (column == 5) { // Net Profit column
                    try {
                        String profitStr = value.toString();
                        if (profitStr.startsWith("$")) {
                            profitStr = profitStr.substring(1);
                        }
                        double profit = Double.parseDouble(profitStr);

                        if (profit >= 0) {
                            c.setBackground(new Color(200, 255, 200)); // Light green
                        } else {
                            c.setBackground(new Color(255, 200, 200)); // Light red
                        }
                    } catch (NumberFormatException e) {
                        c.setBackground(Color.WHITE);
                    }
                } else {
                    c.setBackground(Color.WHITE);
                }

                if (isSelected) {
                    c.setBackground(new Color(200, 200, 255)); // Light blue
                }

                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(reportsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Date filter
        JLabel filterLabel = new JLabel("Filter by:");
        filterLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        panel.add(filterLabel);
        String[] filters = {"All Time", "This Month", "Last Month", "Last 3 Months", "This Year"};
        dateFilterCombo = new JComboBox<>(filters);
        dateFilterCombo.setFont(new Font("Arial", Font.PLAIN, 16));
        dateFilterCombo.setPreferredSize(new Dimension(180, 40));
        dateFilterCombo.addActionListener(e -> loadFinancialData());
        panel.add(dateFilterCombo);

        refreshButton = createStyledButton("Refresh Reports", new Color(0, 102, 204), Color.WHITE);
        refreshButton.setPreferredSize(new Dimension(200, 48));
        refreshButton.addActionListener(e -> loadFinancialData());
        panel.add(refreshButton);

        exportButton = createStyledButton("Export to CSV", new Color(34, 139, 34), Color.WHITE);
        exportButton.setPreferredSize(new Dimension(200, 48));
        exportButton.addActionListener(e -> exportToCSV());
        panel.add(exportButton);

        return panel;
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
    private void loadFinancialData() {
        // Load system totals
        EnhancedProfitDAO.SystemTotals totals = EnhancedProfitDAO.getSystemTotals();

        totalRevenueLabel.setText(String.format("$%.2f", totals.getTotalRevenue()));
        totalCOGSLabel.setText(String.format("$%.2f", totals.getTotalCOGS()));
        grossProfitLabel.setText(String.format("$%.2f", totals.getGrossProfit()));
        totalExpensesLabel.setText(String.format("$%.2f", totals.getTotalExpenses()));

        double netProfit = totals.getNetProfit();
        netProfitLabel.setText(String.format("$%.2f", netProfit));
        if (netProfit >= 0) {
            netProfitLabel.setForeground(new Color(0, 128, 0));
        } else {
            netProfitLabel.setForeground(new Color(220, 20, 60));
        }

        double profitMargin = totals.getTotalRevenue() > 0 ?
                (netProfit / totals.getTotalRevenue()) * 100 : 0;
        profitMarginLabel.setText(String.format("%.2f%%", profitMargin));

        // Load manager performance data
        loadManagerPerformanceReport();
    }

    private void loadManagerPerformanceReport() {
        tableModel.setRowCount(0);

        java.util.List<EnhancedProfitDAO.ManagerPerformance> performances;

        String selectedFilter = (String) dateFilterCombo.getSelectedItem();

        if ("All Time".equals(selectedFilter)) {
            performances = EnhancedProfitDAO.getManagerPerformanceReport();
        } else {
            // Calculate date range based on filter
            LocalDate endDate = LocalDate.now();
            LocalDate startDate;

            switch (selectedFilter) {
                case "This Month":
                    startDate = endDate.withDayOfMonth(1);
                    break;
                case "Last Month":
                    startDate = endDate.minusMonths(1).withDayOfMonth(1);
                    endDate = endDate.withDayOfMonth(1).minusDays(1);
                    break;
                case "Last 3 Months":
                    startDate = endDate.minusMonths(3);
                    break;
                case "This Year":
                    startDate = endDate.withDayOfYear(1);
                    break;
                default:
                    startDate = endDate.minusYears(10); // Default to all time
            }

            performances = EnhancedProfitDAO.getManagerPerformanceByDateRange(
                    Date.valueOf(startDate),
                    Date.valueOf(endDate)
            );
        }

        for (EnhancedProfitDAO.ManagerPerformance perf : performances) {
            tableModel.addRow(new Object[]{
                    perf.getManagerName(),
                    String.format("$%.2f", perf.getTotalRevenue()),
                    String.format("$%.2f", perf.getTotalCostOfGoodsSold()),
                    String.format("$%.2f", perf.getGrossProfit()),
                    String.format("$%.2f", perf.getTotalExpenses()),
                    String.format("$%.2f", perf.getNetProfit()),
                    String.format("%.2f%%", perf.getProfitMargin()),
                    perf.getTotalBills(),
                    perf.getTotalProductsSold()
            });
        }

        if (performances.isEmpty()) {
            ElegantMessageDialog.showMessage(this,
                    "No manager performance data available for the selected period.",
                    "No Data",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Financial Report");
        fileChooser.setSelectedFile(new java.io.File("financial_report_" +
                LocalDate.now().toString() + ".csv"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();

            try (java.io.PrintWriter writer = new java.io.PrintWriter(fileToSave)) {
                // Write system totals
                writer.println("FINANCIAL SUMMARY");
                writer.println("Total Revenue," + totalRevenueLabel.getText());
                writer.println("Cost of Goods Sold," + totalCOGSLabel.getText());
                writer.println("Gross Profit," + grossProfitLabel.getText());
                writer.println("Total Expenses," + totalExpensesLabel.getText());
                writer.println("Net Profit," + netProfitLabel.getText());
                writer.println("Profit Margin," + profitMarginLabel.getText());
                writer.println();

                // Write table headers
                writer.println("MANAGER PERFORMANCE REPORT");
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    writer.print(tableModel.getColumnName(i));
                    if (i < tableModel.getColumnCount() - 1) writer.print(",");
                }
                writer.println();

                // Write table data
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    for (int j = 0; j < tableModel.getColumnCount(); j++) {
                        writer.print(tableModel.getValueAt(i, j));
                        if (j < tableModel.getColumnCount() - 1) writer.print(",");
                    }
                    writer.println();
                }

                ElegantMessageDialog.showMessage(this,
                        "Report exported successfully to:<br>" + fileToSave.getAbsolutePath(),
                        "Export Successful",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (java.io.IOException e) {
                ElegantMessageDialog.showMessage(this,
                        "Error exporting report: " + e.getMessage(),
                        "Export Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

