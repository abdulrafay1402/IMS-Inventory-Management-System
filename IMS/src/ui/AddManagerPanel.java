package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import models.CEO;
import database.UserDAO;
import utils.ValidationUtils;

// Add Manager Panel
class AddManagerPanel extends JPanel {
    private CEO currentUser;
    private JTextField nameField;
    private JTextField cnicField;
    private JTextField usernameField;
    private JTextField phoneField;
    private JTextField salaryField;
    private JTextField bonusField;
    private JComboBox<String> adjustmentTypeCombo;
    private JTextField adjustmentAmountField;
    private JPasswordField passwordField;
    private JButton addButton;

    public AddManagerPanel(CEO user) {
        this.currentUser = user;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header
        JLabel headerLabel = new JLabel("Add New Manager", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 26));
        headerLabel.setForeground(new Color(0, 102, 204));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));
        add(headerLabel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 10, 8, 10);

        int row = 0;

        // Name
        addFormField(formPanel, gbc, row++, "Full Name*:", nameField = new JTextField(20));

        // CNIC
        addFormField(formPanel, gbc, row++, "CNIC* (13 digits):", cnicField = new JTextField(20));
        cnicField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                validateCnic();
            }
        });

        // Username
        addFormField(formPanel, gbc, row++, "Username*:", usernameField = new JTextField(20));
        usernameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                validateUsername();
            }
        });

        // Phone
        addFormField(formPanel, gbc, row++, "Phone (11 digits):", phoneField = new JTextField(20));

        // Salary
        addFormField(formPanel, gbc, row++, "Base Salary (Rs.)*:", salaryField = new JTextField(20));
        salaryField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                validateSalary();
            }
        });

        // Bonus
        addFormField(formPanel, gbc, row++, "Bonus (Rs.):", bonusField = new JTextField(20));
        bonusField.setToolTipText("Optional: Additional bonus amount");

        // Salary Adjustment
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(8, 10, 8, 5);
        JLabel adjustLabel = new JLabel("Salary Adjustment:");
        adjustLabel.setFont(new Font("Arial", Font.BOLD, 15));
        formPanel.add(adjustLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        gbc.insets = new Insets(8, 5, 8, 10);
        JPanel adjustmentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        adjustmentTypeCombo = new JComboBox<>(new String[]{"None", "Increment", "Decrement"});
        adjustmentTypeCombo.setFont(new Font("Arial", Font.PLAIN, 15));
        adjustmentTypeCombo.setPreferredSize(new Dimension(120, 30));
        adjustmentAmountField = new JTextField(10);
        adjustmentAmountField.setFont(new Font("Arial", Font.PLAIN, 15));
        adjustmentAmountField.setToolTipText("Adjustment amount in Rs.");
        adjustmentAmountField.setEnabled(false);
        adjustmentTypeCombo.addActionListener(e -> {
            adjustmentAmountField.setEnabled(!adjustmentTypeCombo.getSelectedItem().equals("None"));
            if (adjustmentTypeCombo.getSelectedItem().equals("None")) {
                adjustmentAmountField.setText("");
            }
        });
        adjustmentPanel.add(adjustmentTypeCombo);
        adjustmentPanel.add(adjustmentAmountField);
        formPanel.add(adjustmentPanel, gbc);
        row++;

        // Password
        addFormField(formPanel, gbc, row++, "Password*:", passwordField = new JPasswordField(20));

        // Buttons
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        addButton = createStyledButton("Add Manager", new Color(0, 102, 204), Color.WHITE);
        addButton.setPreferredSize(new Dimension(180, 48));
        addButton.addActionListener(e -> addManager());

        JButton clearButton = createStyledButton("Clear Form", new Color(108, 117, 125), Color.WHITE);
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
        panel.add(field, gbc);
    }

    private void validateCnic() {
        String cnic = cnicField.getText().trim();
        if (!ValidationUtils.isValidCnic(cnic)) {
            cnicField.setBackground(new Color(255, 200, 200));
        } else if (!UserDAO.isCnicUnique(cnic, null)) {
            cnicField.setBackground(new Color(255, 200, 200));
        } else {
            cnicField.setBackground(Color.WHITE);
        }
    }

    private void validateSalary() {
        String salary = salaryField.getText().trim();
        if (!salary.isEmpty()) {
            try {
                double sal = Double.parseDouble(salary);
                if (sal < 0) {
                    salaryField.setBackground(new Color(255, 200, 200));
                } else {
                    salaryField.setBackground(Color.WHITE);
                }
            } catch (NumberFormatException e) {
                salaryField.setBackground(new Color(255, 200, 200));
            }
        } else {
            salaryField.setBackground(Color.WHITE);
        }
    }

    private void validateUsername() {
        String username = usernameField.getText().trim();
        if (!ValidationUtils.isValidUsername(username)) {
            usernameField.setBackground(new Color(255, 200, 200));
        } else if (!UserDAO.isUsernameUnique(username, null)) {
            usernameField.setBackground(new Color(255, 200, 200));
        } else {
            usernameField.setBackground(Color.WHITE);
        }
    }

    private void addManager() {
        String name = nameField.getText().trim();
        String cnic = cnicField.getText().trim();
        String username = usernameField.getText().trim();
        String phone = phoneField.getText().trim();
        String salaryStr = salaryField.getText().trim();
        String bonusStr = bonusField.getText().trim();
        String adjustmentType = (String) adjustmentTypeCombo.getSelectedItem();
        String adjustmentAmountStr = adjustmentAmountField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Validation
        if (name.isEmpty() || cnic.isEmpty() || username.isEmpty() || password.isEmpty() || salaryStr.isEmpty()) {
            showError("Please fill all required fields (*)");
            return;
        }

        double salary;
        try {
            salary = Double.parseDouble(salaryStr);
            if (salary < 0) {
                showError("Salary cannot be negative");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid salary amount");
            return;
        }

        // Handle bonus
        double bonus = 0;
        if (!bonusStr.isEmpty()) {
            try {
                bonus = Double.parseDouble(bonusStr);
                if (bonus < 0) {
                    showError("Bonus cannot be negative");
                    return;
                }
            } catch (NumberFormatException e) {
                showError("Please enter a valid bonus amount");
                return;
            }
        }

        // Handle adjustment
        double adjustment = 0;
        if (!"None".equals(adjustmentType) && !adjustmentAmountStr.isEmpty()) {
            try {
                adjustment = Double.parseDouble(adjustmentAmountStr);
                if (adjustment < 0) {
                    showError("Adjustment amount cannot be negative");
                    return;
                }
                if ("Decrement".equals(adjustmentType)) {
                    adjustment = -adjustment;
                }
            } catch (NumberFormatException e) {
                showError("Please enter a valid adjustment amount");
                return;
            }
        }

        // Calculate final salary
        double finalSalary = salary + bonus + adjustment;
        if (finalSalary < 0) {
            showError("Final salary cannot be negative. Please adjust your values.");
            return;
        }

        if (!ValidationUtils.isValidCnic(cnic)) {
            showError("Please enter a valid 13-digit CNIC");
            return;
        }

        if (!UserDAO.isCnicUnique(cnic, null)) {
            showError("CNIC already exists in system");
            return;
        }

        if (!ValidationUtils.isValidUsername(username)) {
            showError("Username must be 3-50 alphanumeric characters");
            return;
        }

        if (!UserDAO.isUsernameUnique(username, null)) {
            showError("Username already exists");
            return;
        }

        if (!phone.isEmpty() && !ValidationUtils.isValidPhone(phone)) {
            showError("Phone must be 11 digits");
            return;
        }

        if (!ValidationUtils.isValidPassword(password)) {
            showError("Password must be at least 6 characters");
            return;
        }

        // Add manager to database with final calculated salary
        if (UserDAO.addManager(username, password, name, phone, cnic, finalSalary, currentUser.getId())) {
            String salaryBreakdown = "Base: Rs. " + String.format("%,.2f", salary);
            if (bonus > 0) salaryBreakdown += " + Bonus: Rs. " + String.format("%,.2f", bonus);
            if (adjustment != 0) {
                salaryBreakdown += (adjustment > 0 ? " + Increment: Rs. " : " - Decrement: Rs. ") + 
                                  String.format("%,.2f", Math.abs(adjustment));
            }
            salaryBreakdown += " = Final: Rs. " + String.format("%,.2f", finalSalary);
            
            utils.ElegantMessageDialog.showMessage(this,
                    "Manager added successfully!\n" + salaryBreakdown,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            clearForm();
        } else {
            showError("Failed to add manager. Please try again.");
        }
    }

    private void clearForm() {
        nameField.setText("");
        cnicField.setText("");
        usernameField.setText("");
        phoneField.setText("");
        salaryField.setText("");
        bonusField.setText("");
        adjustmentTypeCombo.setSelectedIndex(0);
        adjustmentAmountField.setText("");
        passwordField.setText("");
    }

    private void showError(String message) {
        utils.ElegantMessageDialog.showMessage(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}

