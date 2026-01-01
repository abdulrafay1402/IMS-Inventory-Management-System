package ui;

import models.CEO;
import utils.ElegantMessageDialog;
import javax.swing.*;
import java.awt.*;

public class CEODashboard extends JFrame {
    private CEO currentUser;
    private JTabbedPane tabbedPane;

    public CEODashboard(CEO user) {
        this.currentUser = user;
        initializeUI();
    }

    private void initializeUI() {
        // Get screen dimensions for full screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = (int) screenSize.getWidth();
        int screenHeight = (int) screenSize.getHeight();
        
        setTitle("CEO Dashboard - " + currentUser.getUsername());
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

        // Tabs - UPDATED WITH ENHANCED REPORTS
        tabbedPane = new JTabbedPane();
        // Responsive tab font size - ensure readable size
        int tabFontSize = Math.max(13, screenWidth / 90);
        tabbedPane.setFont(new Font("Arial", Font.BOLD, tabFontSize));
        
        // Customize tab appearance - ensure proper height for text visibility
        tabbedPane.setTabPlacement(JTabbedPane.TOP);
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        
        // Set UIManager properties for better tab rendering
        int minTabHeight = Math.max(32, screenHeight / 28);
        UIManager.put("TabbedPane.tabHeight", minTabHeight);
        UIManager.put("TabbedPane.selectedTabPadInsets", new Insets(3, 10, 3, 10));
        UIManager.put("TabbedPane.tabInsets", new Insets(5, 14, 5, 14));

        tabbedPane.addTab("Dashboard", new CEODashboardPanel(currentUser));
        tabbedPane.addTab("Add Employee", new AddManagerPanel(currentUser));
        tabbedPane.addTab("Approve Cashiers", new ApproveCashiersPanel());
        tabbedPane.addTab("View Managers", new ViewManagersPanel());
        tabbedPane.addTab("Master Inventory", new MasterInventoryPanel(currentUser));
        tabbedPane.addTab("View Expenses", new ViewExpensesPanel());
        tabbedPane.addTab("Financial Reports", new EnhancedFinancialReportsPanel());

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
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 102, 204));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(headerPadding, hPadding, headerPadding, hPadding));
        headerPanel.setPreferredSize(new Dimension(0, headerHeight));

        // Responsive title font
        int titleFontSize = Math.max(24, screenWidth / 50);
        JLabel titleLabel = new JLabel("CEO Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, titleFontSize));
        titleLabel.setForeground(Color.WHITE);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, Math.max(15, screenWidth / 80), 0));
        rightPanel.setBackground(new Color(0, 102, 204));

        // Responsive user label font
        int userLabelFontSize = Math.max(14, screenWidth / 80);
        JLabel userLabel = new JLabel("Welcome, " + currentUser.getName() + " (CEO)");
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

        rightPanel.add(userLabel);
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

