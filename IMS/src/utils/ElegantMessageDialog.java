package utils;

import javax.swing.*;
import java.awt.*;

public class ElegantMessageDialog {
    
    public static void showMessage(Component parent, String message, String title, int messageType) {
        Frame parentFrame = parent != null ? (Frame) SwingUtilities.getWindowAncestor(parent) : null;
        JDialog dialog = new JDialog(parentFrame, title, true);
        dialog.setLayout(new BorderLayout());
        
        // Responsive sizing
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int dialogWidth = Math.max(450, Math.min(600, screenSize.width / 3));
        int dialogHeight = Math.max(200, screenSize.height / 5);
        dialog.setSize(dialogWidth, dialogHeight);
        dialog.setLocationRelativeTo(parent);
        dialog.setUndecorated(true);
        
        // Determine colors based on message type
        Color headerColor;
        Color borderColor;
        Color iconColor;
        Icon icon;
        
        switch (messageType) {
            case JOptionPane.ERROR_MESSAGE:
                headerColor = new Color(220, 53, 69);
                borderColor = new Color(200, 35, 51);
                iconColor = new Color(220, 53, 69);
                icon = UIManager.getIcon("OptionPane.errorIcon");
                break;
            case JOptionPane.WARNING_MESSAGE:
                headerColor = new Color(255, 193, 7);
                borderColor = new Color(230, 173, 0);
                iconColor = new Color(255, 193, 7);
                icon = UIManager.getIcon("OptionPane.warningIcon");
                break;
            case JOptionPane.INFORMATION_MESSAGE:
                headerColor = new Color(0, 123, 255);
                borderColor = new Color(0, 86, 179);
                iconColor = new Color(0, 123, 255);
                icon = UIManager.getIcon("OptionPane.informationIcon");
                break;
            case JOptionPane.QUESTION_MESSAGE:
                headerColor = new Color(0, 102, 204);
                borderColor = new Color(0, 80, 160);
                iconColor = new Color(0, 102, 204);
                icon = UIManager.getIcon("OptionPane.questionIcon");
                break;
            default:
                headerColor = new Color(108, 117, 125);
                borderColor = new Color(80, 90, 100);
                iconColor = new Color(108, 117, 125);
                icon = UIManager.getIcon("OptionPane.informationIcon");
        }
        
        // Main panel with rounded corners and border outline
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fill background
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Draw border outline
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 15, 15);
            }
        };
        
        int padding = Math.max(20, Math.min(screenSize.width, screenSize.height) / 40);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(padding, padding, padding, padding));
        mainPanel.setBackground(Color.WHITE);
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        // Responsive font sizes
        int titleFontSize = Math.max(18, screenSize.width / 70);
        int messageFontSize = Math.max(14, screenSize.width / 90);
        
        // Title label
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, titleFontSize));
        titleLabel.setForeground(headerColor);
        
        // Icon label
        if (icon != null) {
            JLabel iconLabel = new JLabel(icon);
            iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
            headerPanel.add(iconLabel, BorderLayout.WEST);
        }
        
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Message label - fixed to prevent text cutoff with proper word wrapping
        int messageWidth = dialogWidth - (padding * 2) - 40; // Account for padding and icon
        JLabel messageLabel = new JLabel("<html><div style='text-align: center; width: " + messageWidth + "px; padding: 5px;'>" + 
                                         message.replace("\n", "<br>") + "</div></html>");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, messageFontSize));
        messageLabel.setForeground(new Color(60, 60, 60));
        messageLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(messageLabel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        int buttonWidth = Math.max(100, screenSize.width / 20);
        int buttonHeight = Math.max(35, screenSize.height / 30);
        JButton okButton = createStyledButton("OK", headerColor, Color.WHITE);
        okButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        okButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(okButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    public static int showConfirm(Component parent, String message, String title) {
        final int[] result = {JOptionPane.NO_OPTION};
        Frame parentFrame = parent != null ? (Frame) SwingUtilities.getWindowAncestor(parent) : null;
        JDialog dialog = new JDialog(parentFrame, title, true);
        dialog.setLayout(new BorderLayout());
        
        // Responsive sizing
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int dialogWidth = Math.max(450, Math.min(600, screenSize.width / 3));
        int dialogHeight = Math.max(220, screenSize.height / 5);
        dialog.setSize(dialogWidth, dialogHeight);
        dialog.setLocationRelativeTo(parent);
        dialog.setUndecorated(true);
        
        Color headerColor = new Color(0, 102, 204);
        Color borderColor = new Color(0, 80, 160);
        
        // Main panel with rounded corners and border outline
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fill background
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Draw border outline
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 15, 15);
            }
        };
        
        int padding = Math.max(20, Math.min(screenSize.width, screenSize.height) / 40);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(padding, padding, padding, padding));
        mainPanel.setBackground(Color.WHITE);
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        // Responsive font sizes
        int titleFontSize = Math.max(18, screenSize.width / 70);
        int messageFontSize = Math.max(14, screenSize.width / 90);
        
        // Title label
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, titleFontSize));
        titleLabel.setForeground(headerColor);
        
        // Icon
        Icon icon = UIManager.getIcon("OptionPane.questionIcon");
        if (icon != null) {
            JLabel iconLabel = new JLabel(icon);
            iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
            headerPanel.add(iconLabel, BorderLayout.WEST);
        }
        
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Message label - fixed to prevent text cutoff
        int messageWidth = dialogWidth - (padding * 2) - 40;
        JLabel messageLabel = new JLabel("<html><div style='text-align: center; width: " + messageWidth + "px; padding: 5px;'>" + 
                                         message.replace("\n", "<br>") + "</div></html>");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, messageFontSize));
        messageLabel.setForeground(new Color(60, 60, 60));
        messageLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(messageLabel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        int buttonWidth = Math.max(100, screenSize.width / 20);
        int buttonHeight = Math.max(35, screenSize.height / 30);
        JButton yesButton = createStyledButton("Yes", new Color(34, 139, 34), Color.WHITE);
        yesButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        yesButton.addActionListener(e -> {
            result[0] = JOptionPane.YES_OPTION;
            dialog.dispose();
        });
        
        JButton noButton = createStyledButton("No", new Color(108, 117, 125), Color.WHITE);
        noButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        noButton.addActionListener(e -> {
            result[0] = JOptionPane.NO_OPTION;
            dialog.dispose();
        });
        
        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
        
        return result[0];
    }
    
    private static JButton createStyledButton(String text, Color bgColor, Color textColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color paintColor = getModel().isRollover() ? bgColor.darker() : bgColor;
                g2.setColor(paintColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(textColor);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
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
}

