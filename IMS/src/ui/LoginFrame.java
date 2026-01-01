package ui;

import models.*;
import database.UserDAO;
import utils.ElegantMessageDialog;
import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {
        initializeUI();
    }

    private void initializeUI() {
        // Load logo image from classpath (works in both IDE and JAR/EXE)
        ImageIcon originalImg = null;
        try {
            java.net.URL imageURL = getClass().getClassLoader().getResource("image/logo.jpg");
            if (imageURL != null) {
                originalImg = new ImageIcon(imageURL);
            } else {
                // Fallback to file path for development
                originalImg = new ImageIcon("image/logo.jpg");
            }
        } catch (Exception e) {
            // Create a default placeholder if image not found
            originalImg = new ImageIcon();
            System.err.println("Warning: Could not load logo.jpg - " + e.getMessage());
        }
        
        // Get screen dimensions for responsive sizing
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = (int) screenSize.getWidth();
        int screenHeight = (int) screenSize.getHeight();
        
        // Calculate responsive font sizes based on screen size
        int baseFontSize = Math.max(24, screenWidth / 40); // Minimum 24, scales with screen
        int titleFontSize = (int)(baseFontSize * 2.0); // 2x base
        int subtitleFontSize = (int)(baseFontSize * 0.83); // ~83% of base
        int inputFontSize = (int)(baseFontSize * 0.75); // 75% of base
        
        // Calculate responsive padding (percentage-based)
        int paddingPercent = 5; // 5% of screen dimension
        int hPadding = Math.max(40, screenWidth * paddingPercent / 100);
        int vPadding = Math.max(30, screenHeight * paddingPercent / 100);
        
        setTitle("Inventory Management System - Login");
        // Set window icon
        if (originalImg.getImage() != null) {
            setIconImage(originalImg.getImage());
        }
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(screenWidth, screenHeight);
        setLocationRelativeTo(null);
        setResizable(true);
        setLayout(new BorderLayout());

        // Left Panel - Login Form (White Background) - Takes 50% of screen width
        JPanel p1 = new JPanel();
        p1.setPreferredSize(new Dimension(screenWidth / 2, screenHeight));
        p1.setBackground(Color.WHITE);
        p1.setLayout(new BorderLayout());
        p1.setBorder(BorderFactory.createEmptyBorder(vPadding, hPadding, vPadding, hPadding));

        // Login Title
        JLabel login = new JLabel("Welcome Back", SwingConstants.CENTER);
        login.setFont(new Font("Arial", Font.BOLD, titleFontSize));
        login.setForeground(new Color(50, 50, 50));
        login.setBorder(BorderFactory.createEmptyBorder(0, 0, (int)(vPadding * 0.25), 0));
        
        JLabel subtitle = new JLabel("Sign in to continue", SwingConstants.CENTER);
        subtitle.setFont(new Font("Arial", Font.PLAIN, subtitleFontSize));
        subtitle.setForeground(new Color(120, 120, 120));
        subtitle.setBorder(BorderFactory.createEmptyBorder(0, 0, (int)(vPadding * 1.33), 0));
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(login, BorderLayout.CENTER);
        titlePanel.add(subtitle, BorderLayout.SOUTH);
        p1.add(titlePanel, BorderLayout.NORTH);

        // Input Panel
        JPanel inputPanel = new JPanel();
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 0, 20, 0);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username Label
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, inputFontSize));
        usernameLabel.setForeground(new Color(60, 60, 60));

        // Username Field - Clean minimal design with better visibility
        usernameField = new JTextField(20);
        usernameField.setForeground(new Color(50, 50, 50));
        usernameField.setBackground(Color.WHITE);
        usernameField.setCaretColor(new Color(34, 139, 34));
        usernameField.setFont(new Font("Arial", Font.PLAIN, inputFontSize));
        int fieldPadding = Math.max(8, inputFontSize / 2);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 3, 0, new Color(180, 180, 180)),
            BorderFactory.createEmptyBorder(fieldPadding, 0, fieldPadding, 0)
        ));

        // Password Label
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, inputFontSize));
        passwordLabel.setForeground(new Color(60, 60, 60));

        // Password Field - Clean minimal design with better visibility
        passwordField = new JPasswordField(20);
        passwordField.setForeground(new Color(50, 50, 50));
        passwordField.setBackground(Color.WHITE);
        passwordField.setCaretColor(new Color(34, 139, 34));
        passwordField.setFont(new Font("Arial", Font.PLAIN, inputFontSize));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 3, 0, new Color(180, 180, 180)),
            BorderFactory.createEmptyBorder(fieldPadding, 0, fieldPadding, 0)
        ));

        // Add components to input panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        inputPanel.add(usernameLabel, gbc);
        
        gbc.gridy = 1;
        inputPanel.add(usernameField, gbc);

        gbc.gridy = 2;
        inputPanel.add(passwordLabel, gbc);
        
        gbc.gridy = 3;
        inputPanel.add(passwordField, gbc);

        // Button Panel - Ensure both buttons are visible
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        int buttonGap = Math.max(15, screenWidth / 60);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, buttonGap, (int)(vPadding * 0.5)));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder((int)(vPadding * 0.5), 0, 0, 0));
        buttonPanel.setOpaque(true); // Ensure panel is visible

        // Calculate responsive button sizes
        int buttonWidth = Math.max(180, screenWidth / 8);
        int buttonHeight = Math.max(45, screenHeight / 20);

        // Login Button with greenish color (matching other pages) - with hover effect
        JButton loginButton = createStyledButton("Login", new Color(34, 139, 34), Color.WHITE);
        loginButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        loginButton.setMinimumSize(new Dimension(buttonWidth, buttonHeight));
        loginButton.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
        loginButton.addActionListener(e -> performLogin());

        // Cancel Button - ensure it's visible
        JButton cancelButton = createStyledButton("Cancel", new Color(108, 117, 125), Color.WHITE);
        cancelButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        cancelButton.setMinimumSize(new Dimension(buttonWidth, buttonHeight));
        cancelButton.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
        cancelButton.addActionListener(e -> System.exit(0));

        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);

        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        inputPanel.add(buttonPanel, gbc);

        p1.add(inputPanel, BorderLayout.CENTER);

        // Right Panel - Logo (White Background) - Takes 50% of screen width
        JPanel p2 = new JPanel();
        p2.setPreferredSize(new Dimension(screenWidth / 2, screenHeight));
        p2.setBackground(Color.WHITE);
        p2.setLayout(new BorderLayout());

        // Create a panel with the image and app name overlay - responsive image scaling
        final ImageIcon finalImg = originalImg;
        JPanel imagePanel = new JPanel(new BorderLayout()) {
            private Image originalImage = finalImg != null ? finalImg.getImage() : null;
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw the image scaled to current panel size (maintains aspect ratio)
                if (originalImage != null) {
                    int panelWidth = getWidth();
                    int panelHeight = getHeight();
                    g2.drawImage(originalImage, 0, 0, panelWidth, panelHeight, this);
                }
            }
        };
        imagePanel.setBackground(Color.WHITE);
        imagePanel.setOpaque(true);

        // App Name Label on top of image - responsive font
        int appNameFontSize = Math.max(28, screenWidth / 30);
        JLabel appNameLabel = new JLabel("IMS STOCKMASTER", SwingConstants.CENTER);
        appNameLabel.setFont(new Font("Arial", Font.BOLD, appNameFontSize));
        appNameLabel.setForeground(new Color(50, 50, 50));
        int appNamePadding = Math.max(30, screenHeight / 25);
        appNameLabel.setBorder(BorderFactory.createEmptyBorder(appNamePadding, hPadding / 2, (int)(appNamePadding * 0.6), hPadding / 2));
        appNameLabel.setOpaque(false);

        imagePanel.add(appNameLabel, BorderLayout.NORTH);
        p2.add(imagePanel, BorderLayout.CENTER);

        // Add panels to frame
        add(p1, BorderLayout.CENTER);
        add(p2, BorderLayout.EAST);

        getContentPane().setBackground(Color.WHITE);

        // Enter key listener
        passwordField.addActionListener(e -> performLogin());
    }

    private JButton createStyledButton(String text, Color bgColor, Color textColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                // Determine color based on mouse presence - enhanced hover effect
                Color paintColor;
                if (getModel().isPressed()) {
                    paintColor = bgColor.darker().darker(); // Darker when pressed
                } else if (getModel().isRollover()) {
                    paintColor = bgColor.darker(); // Darker on hover
                } else {
                    paintColor = bgColor; // Normal state
                }
                
                g2.setColor(paintColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                // Paint text
                super.paintComponent(g);
            }
        };

        // Responsive button font size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int buttonFontSize = Math.max(12, screenSize.width / 100);
        button.setFont(new Font("Arial", Font.BOLD, buttonFontSize));
        button.setForeground(textColor);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        int btnPadding = Math.max(6, buttonFontSize / 2);
        button.setBorder(BorderFactory.createEmptyBorder(btnPadding, btnPadding * 2, btnPadding, btnPadding * 2));
        button.setOpaque(false);

        // Enhanced mouse listener for better hover effects
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
                button.repaint();
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                button.repaint();
            }
            
            public void mousePressed(java.awt.event.MouseEvent evt) {
                button.repaint();
            }
            
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                button.repaint();
            }
        });

        return button;
    }
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            ElegantMessageDialog.showMessage(this,
                    "Please enter both username and password",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Authenticate user
        User user = UserDAO.authenticate(username, password);

        if (user != null) {
            dispose();
            openDashboard(user);
        } else {
            ElegantMessageDialog.showMessage(this,
                    "Invalid username or password",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }

    private void openDashboard(User user) {
        if (user instanceof CEO) {
            new CEODashboard((CEO) user).setVisible(true);
        } else if (user instanceof Manager) {
            new ManagerDashboard((Manager) user).setVisible(true);
        } else if (user instanceof Cashier) {
            new CashierDashboard((Cashier) user).setVisible(true);
        }
    }
}

