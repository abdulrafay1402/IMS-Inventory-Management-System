package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import models.Expense;
import database.ExpenseDAO;

// ============= VIEW EXPENSES PANEL =============
class ViewExpensesPanel extends JPanel {
    private JTable expensesTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton;
    private JComboBox<String> filterCombo;

    public ViewExpensesPanel() {
        initializeUI();
        loadExpenses();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        JLabel headerLabel = new JLabel("All Expenses", SwingConstants.LEFT);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 26));
        headerLabel.setForeground(new Color(0, 102, 204));

        // Filter and buttons panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));

        // Filter combo
        JLabel filterLabel = new JLabel("Filter by:");
        filterLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        controlPanel.add(filterLabel);
        String[] filters = {"All", "UTILITIES", "SALARIES", "RENT", "MAINTENANCE", "OTHER"};
        filterCombo = new JComboBox<>(filters);
        filterCombo.setFont(new Font("Arial", Font.PLAIN, 16));
        filterCombo.setPreferredSize(new Dimension(150, 40));
        filterCombo.addActionListener(e -> loadExpenses());
        controlPanel.add(filterCombo);

        refreshButton = createStyledButton("Refresh", new Color(23, 162, 184), Color.WHITE);
        refreshButton.setPreferredSize(new Dimension(160, 48));
        refreshButton.addActionListener(e -> loadExpenses());
        controlPanel.add(refreshButton);

        headerPanel.add(headerLabel, BorderLayout.WEST);
        headerPanel.add(controlPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Description", "Amount", "Category", "Manager", "Expense Date", "Recorded Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        expensesTable = new JTable(tableModel);
        expensesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        expensesTable.setFont(new Font("Arial", Font.PLAIN, 14));
        expensesTable.setRowHeight(35);
        
        // Configure header with proper visibility
        javax.swing.table.JTableHeader header = expensesTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 16));
        header.setBackground(new Color(0, 102, 204)); // CEO Blue
        header.setForeground(Color.WHITE);
        header.setOpaque(true);
        
        expensesTable.getTableHeader().setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
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

        // Color code rows based on category
        expensesTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                String category = (String) table.getValueAt(row, 3);
                Color bgColor = getCategoryColor(category);
                c.setBackground(bgColor);

                if (isSelected) {
                    c.setBackground(new Color(200, 200, 255)); // Light blue for selection
                }

                return c;
            }

            private Color getCategoryColor(String category) {
                switch (category) {
                    case "UTILITIES": return new Color(255, 255, 200); // Light yellow
                    case "SALARIES": return new Color(255, 200, 200); // Light red
                    case "RENT": return new Color(200, 255, 200); // Light green
                    case "MAINTENANCE": return new Color(200, 200, 255); // Light blue
                    case "OTHER": return new Color(255, 200, 255); // Light purple
                    default: return Color.WHITE;
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(expensesTable);
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

        JLabel totalLabel = new JLabel("Total Expenses: $0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalLabel.setForeground(new Color(220, 20, 60));

        panel.add(totalLabel);

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
    private void loadExpenses() {
        tableModel.setRowCount(0);
        java.util.List<Expense> expenses = ExpenseDAO.getAllExpenses();

        String selectedFilter = (String) filterCombo.getSelectedItem();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        double totalAmount = 0;

        for (Expense expense : expenses) {
            // Apply filter
            if (!"All".equals(selectedFilter) && !selectedFilter.equals(expense.getCategory())) {
                continue;
            }

            tableModel.addRow(new Object[]{
                    expense.getId(),
                    expense.getDescription(),
                    String.format("$%.2f", expense.getAmount()),
                    expense.getCategory(),
                    expense.getManagerName(),
                    sdf.format(expense.getExpenseDate()),
                    sdf.format(expense.getRecordedDate())
            });

            totalAmount += expense.getAmount();
        }

        // Update summary
        updateSummaryPanel(totalAmount);

//        if (expenses.isEmpty()) {
//            JOptionPane.showMessageDialog(this,
//                    "No expenses found in the system.",
//                    "Information",
//                    JOptionPane.INFORMATION_MESSAGE);
//        }
    }

    private void updateSummaryPanel(double totalAmount) {
        Component[] components = ((JPanel)getComponent(2)).getComponents();
        if (components.length > 0 && components[0] instanceof JLabel) {
            JLabel totalLabel = (JLabel) components[0];
            totalLabel.setText(String.format("Total Expenses: $%.2f", totalAmount));
        }
    }
}

