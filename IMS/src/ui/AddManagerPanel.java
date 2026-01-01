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
    private JPasswordField passwordField;
    private JButton addButton;

    public AddManagerPanel(CEO user) {
        this.currentUser = user;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header - Enhanced styling
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int headerFontSize = Math.max(18, screenSize.width / 70);
        JLabel headerLabel = new JLabel("Add New Employee (Manager)", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, headerFontSize));
        headerLabel.setForeground(new Color(0, 102, 204));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        add(headerLabel, BorderLayout.NORTH);

        // Form Panel - Centered
        JPanel formPanel = new JPanel(new GridBagLayout());
        Dimension screenSize2 = Toolkit.getDefaultToolkit().getScreenSize();
        int formPadding = Math.max(30, Math.min(screenSize2.width, screenSize2.height) / 25);
        formPanel.setBorder(BorderFactory.createEmptyBorder(formPadding, formPadding, formPadding, formPadding));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.anchor = GridBagConstraints.CENTER; // Center alignment

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

        // Password
        addFormField(formPanel, gbc, row++, "Password*:", passwordField = new JPasswordField(20));

        // Add Button
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        addButton = createStyledButton("Add Manager", new Color(0, 153, 51), Color.WHITE);
        addButton.setPreferredSize(new Dimension(150, 35));
        addButton.addActionListener(e -> addManager());
        formPanel.add(addButton, gbc);

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

        // Use a more compact font and reduce padding
        button.setFont(new Font("Arial", Font.BOLD, 12)); // Smaller font size
        button.setForeground(textColor);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12)); // Reduced padding
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
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        field.setFont(new Font("Arial", Font.PLAIN, 12));
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
        String password = new String(passwordField.getPassword());

        // Validation
        if (name.isEmpty() || cnic.isEmpty() || username.isEmpty() || password.isEmpty()) {
            showError("Please fill all required fields (*)");
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

        // Add manager to database
        if (UserDAO.addManager(username, password, name, phone, cnic, currentUser.getId())) {
            JOptionPane.showMessageDialog(this,
                    "Manager added successfully!",
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
        passwordField.setText("");
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}

