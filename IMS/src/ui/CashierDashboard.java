package ui;

import models.Cashier;
import database.NotificationDAO;
import utils.ElegantMessageDialog;
import javax.swing.*;
import java.awt.*;

public class CashierDashboard extends JFrame {
    private Cashier currentUser;
    private JTabbedPane tabbedPane;
    
    // Teal color for cashier - complements CEO blue and Manager green
    private static final Color CASHIER_COLOR = new Color(0, 150, 136);

    public CashierDashboard(Cashier user) {
        this.currentUser = user;
        initializeUI();
    }

    private void initializeUI() {
        // Get screen dimensions for responsive sizing
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = (int) screenSize.getWidth();
        int screenHeight = (int) screenSize.getHeight();
        
        setTitle("Cashier Dashboard - " + currentUser.getUsername());
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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(screenWidth, screenHeight);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header
        add(createHeader(), BorderLayout.NORTH);

        // Tabs - COMPLETE IMPLEMENTATION
        tabbedPane = new JTabbedPane();
        // Responsive tab font size
        int tabFontSize = Math.max(14, screenWidth / 100);
        tabbedPane.setFont(new Font("Arial", Font.BOLD, tabFontSize));
        tabbedPane.addTab("Create Bill", new CreateBillPanel(currentUser));
        tabbedPane.addTab("View Stock", new CashierViewStockPanel(currentUser));
        tabbedPane.addTab("Past Bills", new CashierPastBillsPanel(currentUser));

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = (int) screenSize.getWidth();
        int screenHeight = (int) screenSize.getHeight();
        
        // Responsive header sizing
        int headerPadding = Math.max(20, screenHeight / 40);
        int headerHeight = Math.max(80, screenHeight / 12);
        int hPadding = Math.max(30, screenWidth / 40);
        
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(255, 140, 0), getWidth(), 0, new Color(255, 180, 50));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(headerPadding, hPadding, headerPadding, hPadding));
        headerPanel.setPreferredSize(new Dimension(0, headerHeight));

        // Responsive title font
        int titleFontSize = Math.max(24, screenWidth / 50);
        JLabel titleLabel = new JLabel("Cashier Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, titleFontSize));
        titleLabel.setForeground(Color.WHITE);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, Math.max(15, screenWidth / 80), 0));
        rightPanel.setOpaque(false);

        // Responsive user label font
        int userLabelFontSize = Math.max(14, screenWidth / 80);
        JLabel userLabel = new JLabel("Welcome, " + currentUser.getName() + " (Cashier)");
        userLabel.setFont(new Font("Arial", Font.PLAIN, userLabelFontSize));
        userLabel.setForeground(Color.WHITE);

        // Responsive button sizes
        int buttonWidth = Math.max(140, screenWidth / 12);
        int buttonHeight = Math.max(40, screenHeight / 25);
        
        JButton profileButton = createStyledButton("My Profile", new Color(23, 162, 184), Color.WHITE);
        profileButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        profileButton.setFocusPainted(false);
        profileButton.addActionListener(e -> new ProfileDialog(this, currentUser).setVisible(true));

        JButton logoutButton = createStyledButton("Logout", new Color(220, 20, 60), Color.WHITE);
        logoutButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> logout());
        
        // Notification button with badge
        final JButton notificationButton = createNotificationButton();
        notificationButton.setPreferredSize(new Dimension(Math.max(160, buttonWidth), buttonHeight));
        notificationButton.setFocusPainted(false);
        notificationButton.addActionListener(e -> {
            NotificationDialog dialog = new NotificationDialog(this, currentUser);
            dialog.setVisible(true);
            // Update badge after dialog closes (setVisible blocks until closed)
            int unreadCount = NotificationDAO.getUnreadCount(currentUser.getId());
            if (unreadCount > 0) {
                notificationButton.setText("NOTIFICATIONS (" + unreadCount + ")");
            } else {
                notificationButton.setText("NOTIFICATIONS");
            }
            notificationButton.repaint();
        });

        rightPanel.add(userLabel);
        rightPanel.add(notificationButton);
        rightPanel.add(profileButton);
        rightPanel.add(logoutButton);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);

        return headerPanel;
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

        // Responsive button styling
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int buttonFontSize = Math.max(12, screenSize.width / 100);
        button.setFont(new Font("Arial", Font.BOLD, buttonFontSize));
        button.setForeground(textColor);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        int btnPadding = Math.max(8, buttonFontSize / 2);
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
    
    private JButton createNotificationButton() {
        int unreadCount = NotificationDAO.getUnreadCount(currentUser.getId());
        String bellText = unreadCount > 0 ? "NOTIFICATIONS (" + unreadCount + ")" : "NOTIFICATIONS";
        
        JButton button = new JButton(bellText) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color paintColor = getModel().isRollover() ? new Color(255, 193, 7).darker() : new Color(255, 193, 7);
                g2.setColor(paintColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                super.paintComponent(g);
            }
        };
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int buttonFontSize = Math.max(11, screenSize.width / 120);
        button.setFont(new Font("Arial", Font.BOLD, buttonFontSize));
        button.setForeground(Color.BLACK);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.repaint();
            }
        });

        return button;
    }
    
    private void logout() {
        int confirm = ElegantMessageDialog.showConfirm(this,
                "Are you sure you want to logout?",
                "Confirm Logout");

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}

