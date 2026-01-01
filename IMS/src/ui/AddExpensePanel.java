package ui;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import models.Manager;
import database.ExpenseDAO;
import utils.ElegantMessageDialog;


// ============= ADD EXPENSE PANEL =============
class AddExpensePanel extends JPanel {
    private Manager currentUser;
    private JTextField descriptionField;
    private JTextField amountField;
    private JComboBox<String> categoryCombo;
    private JSpinner dateSpinner;
    private JButton addButton, clearButton;

    public AddExpensePanel(Manager user) {
        this.currentUser = user;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header
        JLabel headerLabel = new JLabel("Add New Expense", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 26));
        headerLabel.setForeground(new Color(34, 139, 34));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));
        add(headerLabel, BorderLayout.NORTH);

        // Form Panel - Centered
        JPanel formPanel = new JPanel(new GridBagLayout());
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int formPadding = Math.max(30, Math.min(screenSize.width, screenSize.height) / 25);
        formPanel.setBorder(BorderFactory.createEmptyBorder(formPadding, formPadding, formPadding, formPadding));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 10, 8, 10);

        int row = 0;

        // Description
        addFormField(formPanel, gbc, row++, "Description*:", descriptionField = new JTextField(20));

        // Amount
        addFormField(formPanel, gbc, row++, "Amount* ($):", amountField = new JTextField(20));

        // Category
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        gbc.gridwidth = 1;
        JLabel categoryLabel = new JLabel("Category*:");
        categoryLabel.setFont(new Font("Arial", Font.BOLD, 16));
        formPanel.add(categoryLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        String[] categories = {"UTILITIES", "SALARIES", "RENT", "MAINTENANCE", "OTHER"};
        categoryCombo = new JComboBox<>(categories);
        categoryCombo.setFont(new Font("Arial", Font.PLAIN, 16));
        categoryCombo.setPreferredSize(new Dimension(200, 40));
        formPanel.add(categoryCombo, gbc);

        row++;

        // Date
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        gbc.gridwidth = 1;
        JLabel dateLabel = new JLabel("Expense Date:");
        dateLabel.setFont(new Font("Arial", Font.BOLD, 16));
        formPanel.add(dateLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;

        // Create date spinner with current date
        SpinnerDateModel dateModel = new SpinnerDateModel();
        dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "MM/dd/yyyy");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setValue(new Date()); // Set to current date
        dateSpinner.setFont(new Font("Arial", Font.PLAIN, 16));
        dateSpinner.setPreferredSize(new Dimension(200, 40));
        formPanel.add(dateSpinner, gbc);

        row++;

        // Buttons
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));

// Create styled buttons using the method from your reference code
        addButton = createStyledButton("Add Expense", new Color(34, 139, 34), Color.WHITE);
        addButton.setPreferredSize(new Dimension(180, 48));
        addButton.addActionListener(e -> addExpense());

        clearButton = createStyledButton("Clear Form", new Color(108, 117, 125), Color.WHITE);
        clearButton.setPreferredSize(new Dimension(180, 48));
        clearButton.addActionListener(e -> clearForm());

        buttonPanel.add(addButton);
        buttonPanel.add(clearButton);
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.CENTER);
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

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row,
                              String labelText, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(8, 10, 8, 5);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 15));
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        gbc.insets = new Insets(8, 5, 8, 10);
        field.setFont(new Font("Arial", Font.PLAIN, 15));
        if (field instanceof JTextField) {
            ((JTextField) field).setPreferredSize(new Dimension(200, 40));
        }
        panel.add(field, gbc);
    }

    private void addExpense() {
        String description = descriptionField.getText().trim();
        String amountStr = amountField.getText().trim();
        String category = (String) categoryCombo.getSelectedItem();
        Date expenseDate = (Date) dateSpinner.getValue();

        // Validation
        if (description.isEmpty() || amountStr.isEmpty()) {
            showError("Please fill all required fields (*)");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                showError("Amount must be greater than 0");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid amount");
            return;
        }

        // Add expense to database
        if (ExpenseDAO.addExpense(currentUser.getId(), description, amount, category, expenseDate)) {
            clearForm();
        } else {
            showError("Failed to add expense. Please try again.");
        }
    }

    private void clearForm() {
        descriptionField.setText("");
        amountField.setText("");
        categoryCombo.setSelectedIndex(0);
        dateSpinner.setValue(new Date()); // Reset to current date
    }

    private void showError(String message) {
        ElegantMessageDialog.showMessage(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}

