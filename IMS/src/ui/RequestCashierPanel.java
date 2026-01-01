package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import models.Manager;
import database.UserDAO;
import database.ManagerDAO;
import database.NotificationDAO;
import utils.ValidationUtils;
import utils.ElegantMessageDialog;

// ============= REQUEST CASHIER PANEL =============
class RequestCashierPanel extends JPanel {
    private Manager currentUser;
    private JTextField nameField;
    private JTextField cnicField;
    private JTextField usernameField;
    private JTextField phoneField;
    private JTextField salaryField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton requestButton;
    private JButton clearButton;

    public RequestCashierPanel(Manager user) {
        this.currentUser = user;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header
        JLabel headerLabel = new JLabel("Request New Cashier", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 26));
        headerLabel.setForeground(new Color(34, 139, 34));
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
        addFormField(formPanel, gbc, row++, "Monthly Salary ($)*:", salaryField = new JTextField(20));
        salaryField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                validateSalary();
            }
        });

        // Password
        addFormField(formPanel, gbc, row++, "Password*:", passwordField = new JPasswordField(20));
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                validatePassword();
            }
        });

        // Confirm Password
        addFormField(formPanel, gbc, row++, "Confirm Password*:", confirmPasswordField = new JPasswordField(20));
        confirmPasswordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                validateConfirmPassword();
            }
        });

        // Buttons
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        requestButton = createStyledButton("Submit Request", new Color(34, 139, 34), Color.WHITE);
        requestButton.setPreferredSize(new Dimension(200, 48));
        requestButton.addActionListener(e -> requestCashier());

        clearButton = createStyledButton("Clear Form", new Color(108, 117, 125), Color.WHITE);
        clearButton.setPreferredSize(new Dimension(200, 48));
        clearButton.addActionListener(e -> clearForm());

        buttonPanel.add(requestButton);
        buttonPanel.add(clearButton);
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.CENTER);

// Info Panel
        JPanel infoPanel = createInfoPanel();
        add(infoPanel, BorderLayout.SOUTH);
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

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        panel.setBackground(new Color(240, 248, 255));

        JLabel infoLabel = new JLabel(
                "<html><center><b>Note:</b> Cashier requests require CEO approval.<br>" +
                        "The cashier account will be activated only after CEO approval.</center></html>"
        );
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        infoLabel.setForeground(new Color(70, 70, 70));

        panel.add(infoLabel);
        return panel;
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

    private void validatePassword() {
        String password = new String(passwordField.getPassword());
        if (!ValidationUtils.isValidPassword(password)) {
            passwordField.setBackground(new Color(255, 200, 200));
        } else {
            passwordField.setBackground(Color.WHITE);
        }
        validateConfirmPassword(); // Also update confirm password validation
    }

    private void validateConfirmPassword() {
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (confirmPassword.isEmpty()) {
            confirmPasswordField.setBackground(Color.WHITE);
        } else if (!password.equals(confirmPassword)) {
            confirmPasswordField.setBackground(new Color(255, 200, 200));
        } else {
            confirmPasswordField.setBackground(Color.WHITE);
        }
    }

    private void requestCashier() {
        String name = nameField.getText().trim();
        String cnic = cnicField.getText().trim();
        String username = usernameField.getText().trim();
        String phone = phoneField.getText().trim();
        String salaryStr = salaryField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // Validation
        if (name.isEmpty() || cnic.isEmpty() || username.isEmpty() ||
                password.isEmpty() || confirmPassword.isEmpty() || salaryStr.isEmpty()) {
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

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            return;
        }

        // Submit cashier request
        if (ManagerDAO.requestCashier(currentUser.getId(), name, username, password, phone, cnic, salary)) {
            // Notify CEO about new cashier request
            try {
                // Get CEO user ID (role = 'CEO')
                int ceoUserId = UserDAO.getCEOUserId();
                if (ceoUserId > 0) {
                    NotificationDAO.createNotification(
                        ceoUserId,
                        "CASHIER_REQUEST",
                        "New Cashier Request",
                        "Manager " + currentUser.getName() + " has requested a new cashier: " + name + " (Salary: $" + String.format("%,.2f", salary) + ")",
                        username // Store username as related ID for future reference
                    );
                }
            } catch (Exception e) {
                // Log but don't fail the operation
                System.err.println("Failed to create notification: " + e.getMessage());
            }
            
            ElegantMessageDialog.showMessage(this, 
                "Cashier request submitted successfully!\nSalary: $" + String.format("%,.2f", salary) + "\nAwaiting CEO approval.", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            clearForm();
        } else {
            showError("Failed to submit cashier request. Please try again.");
        }
    }

    private void clearForm() {
        nameField.setText("");
        cnicField.setText("");
        usernameField.setText("");
        phoneField.setText("");
        salaryField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");

        // Reset backgrounds
        nameField.setBackground(Color.WHITE);
        cnicField.setBackground(Color.WHITE);
        usernameField.setBackground(Color.WHITE);
        phoneField.setBackground(Color.WHITE);
        salaryField.setBackground(Color.WHITE);
        passwordField.setBackground(Color.WHITE);
        confirmPasswordField.setBackground(Color.WHITE);
    }

    private void showError(String message) {
        ElegantMessageDialog.showMessage(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}

