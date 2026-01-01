package ui;

import models.User;
import database.UserDAO;
import utils.ValidationUtils;
import utils.ElegantMessageDialog;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ProfileDialog extends JDialog {
    private User user;
    private JTextField nameField;
    private JTextField cnicField;
    private JTextField usernameField;
    private JTextField phoneField;
    private JPasswordField currentPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JCheckBox changePasswordCheckBox;

    public ProfileDialog(JFrame parent, User user) {
        super(parent, "My Profile", true);
        this.user = user;
        // Set window icon
        try {
            java.net.URL imageURL = getClass().getClassLoader().getResource("image/logo.jpg");
            if (imageURL != null) {
                ImageIcon logoIcon = new ImageIcon(imageURL);
                setIconImage(logoIcon.getImage());
            }
        } catch (Exception e) {
            // Icon not found, continue without it
        }
        initializeUI();
    }

    private void initializeUI() {
        // Responsive dialog sizing
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int dialogWidth = Math.max(450, screenSize.width / 3);
        int dialogHeight = Math.max(500, screenSize.height / 2);
        setSize(dialogWidth, dialogHeight);
        setLocationRelativeTo(getParent());
        setResizable(true); // Make resizable for better responsiveness
        setMinimumSize(new Dimension(450, 500)); // Set minimum size

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header with responsive font
        int headerFontSize = Math.max(16, screenSize.width / 80);
        JLabel headerLabel = new JLabel("User Profile", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, headerFontSize));
        headerLabel.setForeground(new Color(0, 102, 204));
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 10, 8, 10);

        int row = 0;

        // Name (Read-only)
        addFormField(formPanel, gbc, row++, "Name:", nameField = new JTextField(user.getName()));
        nameField.setEditable(false);
        nameField.setBackground(new Color(240, 240, 240));

        // CNIC (Read-only)
        addFormField(formPanel, gbc, row++, "CNIC:", cnicField = new JTextField(user.getCnic()));
        cnicField.setEditable(false);
        cnicField.setBackground(new Color(240, 240, 240));

        // Role (Read-only)
        JTextField roleField = new JTextField(user.getRole());
        roleField.setEditable(false);
        roleField.setBackground(new Color(240, 240, 240));
        addFormField(formPanel, gbc, row++, "Role:", roleField);

        // Username (Editable)
        addFormField(formPanel, gbc, row++, "Username:", usernameField = new JTextField(user.getUsername()));
        usernameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                validateUsername();
            }
        });

        // Phone (Editable)
        addFormField(formPanel, gbc, row++, "Phone:", phoneField = new JTextField(user.getPhone()));

        // Separator
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        formPanel.add(new JSeparator(), gbc);
        gbc.gridwidth = 1;

        // Change Password Checkbox
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        changePasswordCheckBox = new JCheckBox("Change Password");
        changePasswordCheckBox.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(changePasswordCheckBox, gbc);
        gbc.gridwidth = 1;

        // Current Password
        addFormField(formPanel, gbc, row++, "Current Password:", currentPasswordField = new JPasswordField());
        currentPasswordField.setEnabled(false);

        // New Password
        addFormField(formPanel, gbc, row++, "New Password:", newPasswordField = new JPasswordField());
        newPasswordField.setEnabled(false);

        // Confirm Password
        addFormField(formPanel, gbc, row++, "Confirm Password:", confirmPasswordField = new JPasswordField());
        confirmPasswordField.setEnabled(false);

        // Change password checkbox listener
        changePasswordCheckBox.addActionListener(e -> {
            boolean selected = changePasswordCheckBox.isSelected();
            currentPasswordField.setEnabled(selected);
            newPasswordField.setEnabled(selected);
            confirmPasswordField.setEnabled(selected);

            if (!selected) {
                currentPasswordField.setText("");
                newPasswordField.setText("");
                confirmPasswordField.setText("");
            }
        });

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Buttons

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        // Responsive button sizes
        int buttonWidth = Math.max(110, screenSize.width / 15);
        int buttonHeight = Math.max(30, screenSize.height / 30);
        
        JButton saveButton = createStyledButton("Save Changes", new Color(0, 153, 51), Color.WHITE);
        saveButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        saveButton.addActionListener(e -> saveProfile());

        JButton cancelButton = createStyledButton("Cancel", new Color(108, 117, 125), Color.WHITE);
        cancelButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
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

        // Responsive button font and padding
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int buttonFontSize = Math.max(11, screenSize.width / 120);
        button.setFont(new Font("Arial", Font.BOLD, buttonFontSize));
        button.setForeground(textColor);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        int btnPadding = Math.max(5, buttonFontSize / 2);
        button.setBorder(BorderFactory.createEmptyBorder(btnPadding, btnPadding * 2, btnPadding, btnPadding * 2));
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
        // Responsive form field fonts
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int formFontSize = Math.max(11, screenSize.width / 120);
        
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, formFontSize));
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        field.setFont(new Font("Arial", Font.PLAIN, formFontSize));
        panel.add(field, gbc);
    }

    private void validateUsername() {
        String username = usernameField.getText().trim();
        if (!username.equals(user.getUsername())) {
            if (!ValidationUtils.isValidUsername(username)) {
                usernameField.setBackground(new Color(255, 200, 200));
            } else if (!UserDAO.isUsernameUnique(username, user.getId())) {
                usernameField.setBackground(new Color(255, 200, 200));
            } else {
                usernameField.setBackground(Color.WHITE);
            }
        } else {
            usernameField.setBackground(Color.WHITE);
        }
    }

    private void saveProfile() {
        String username = usernameField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = user.getPassword(); // Keep current password by default

        // Validate username
        if (username.isEmpty()) {
            showError("Username cannot be empty");
            return;
        }

        if (!ValidationUtils.isValidUsername(username)) {
            showError("Username must be 3-50 alphanumeric characters");
            return;
        }

        if (!username.equals(user.getUsername()) && !UserDAO.isUsernameUnique(username, user.getId())) {
            showError("Username already exists");
            return;
        }

        // Validate phone
        if (!phone.isEmpty() && !ValidationUtils.isValidPhone(phone)) {
            showError("Phone must be 11 digits");
            return;
        }

        // Handle password change
        if (changePasswordCheckBox.isSelected()) {
            String currentPassword = new String(currentPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (!currentPassword.equals(user.getPassword())) {
                showError("Current password is incorrect");
                return;
            }

            if (newPassword.isEmpty()) {
                showError("New password cannot be empty");
                return;
            }

            if (!ValidationUtils.isValidPassword(newPassword)) {
                showError("Password must be at least 6 characters");
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                showError("New passwords do not match");
                return;
            }

            password = newPassword;
        }

        // Update in database
        if (UserDAO.updateUserProfile(user.getId(), username, password, phone)) {
            // Update local user object
            user.setUsername(username);
            user.setPassword(password);
            user.setPhone(phone);

            ElegantMessageDialog.showMessage(this,
                    "Profile updated successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            showError("Failed to update profile. Please try again.");
        }
    }

    private void showError(String message) {
        ElegantMessageDialog.showMessage(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}

