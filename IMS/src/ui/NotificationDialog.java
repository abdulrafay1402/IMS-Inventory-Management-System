package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import models.Notification;
import models.Bill;
import database.NotificationDAO;
import database.BillDAO;
import models.User;

public class NotificationDialog extends JDialog {
    private User currentUser;
    private JPanel notificationListPanel;
    private JLabel unreadCountLabel;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
    private Color roleColor;

    public NotificationDialog(Frame parent, User user) {
        super(parent, "Notifications", true);
        this.currentUser = user;
        // Set color based on role
        switch(user.getRole()) {
            case "CEO":
                roleColor = new Color(0, 102, 204); // Blue
                break;
            case "MANAGER":
                roleColor = new Color(34, 139, 34); // Green
                break;
            case "CASHIER":
                roleColor = new Color(0, 150, 136); // Teal
                break;
            default:
                roleColor = new Color(34, 139, 34); // Default green
        }
        initializeUI();
        loadNotifications();
    }

    private void initializeUI() {
        setSize(550, 600);
        setLocationRelativeTo(getOwner());
        setResizable(true);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(Color.WHITE);

        // Header Panel with gradient background
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(roleColor);
        headerPanel.setBorder(new EmptyBorder(20, 25, 20, 25));
        
        JLabel titleLabel = new JLabel("Notifications");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        
        unreadCountLabel = new JLabel();
        unreadCountLabel.setFont(new Font("Arial", Font.BOLD, 13));
        unreadCountLabel.setForeground(new Color(255, 235, 59));
        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel);
        titlePanel.add(unreadCountLabel);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content Panel
        JPanel contentPanel = new JPanel(new BorderLayout(0, 0));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Notifications List
        notificationListPanel = new JPanel();
        notificationListPanel.setLayout(new BoxLayout(notificationListPanel, BoxLayout.Y_AXIS));
        notificationListPanel.setBackground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(notificationListPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(Color.WHITE);
        
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Button Panel - Same style as ProfileDialog
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JButton markAllReadBtn = createStyledButton("Mark All Read", new Color(34, 139, 34));
        markAllReadBtn.addActionListener(e -> markAllAsRead());
        
        JButton refreshBtn = createStyledButton("Refresh", new Color(0, 123, 255));
        refreshBtn.addActionListener(e -> loadNotifications());
        
        JButton closeBtn = createStyledButton("Close", new Color(108, 117, 125));
        closeBtn.addActionListener(e -> dispose());
        
        buttonPanel.add(markAllReadBtn);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(closeBtn);
        
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        add(mainPanel);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(140, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            Color originalColor = bgColor;
            
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor.darker());
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor);
            }
        });
        
        return button;
    }

    public void loadNotifications() {
        notificationListPanel.removeAll();
        
        List<Notification> notifications = NotificationDAO.getAllNotifications(currentUser.getId(), 50);
        int unreadCount = NotificationDAO.getUnreadCount(currentUser.getId());
        
        unreadCountLabel.setText(unreadCount > 0 ? "(" + unreadCount + " unread)" : "");
        
        if (notifications.isEmpty()) {
            JLabel emptyLabel = new JLabel("No notifications yet");
            emptyLabel.setFont(new Font("Arial", Font.ITALIC, 15));
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            emptyLabel.setBorder(new EmptyBorder(50, 0, 0, 0));
            notificationListPanel.add(emptyLabel);
        } else {
            for (Notification notif : notifications) {
                notificationListPanel.add(createNotificationCard(notif));
                notificationListPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }
        
        notificationListPanel.revalidate();
        notificationListPanel.repaint();
    }

    private JPanel createNotificationCard(Notification notif) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.setColor(notif.isRead() ? new Color(220, 220, 220) : new Color(34, 139, 34));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 15, 15);
            }
        };
        card.setLayout(new BorderLayout(8, 5));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        card.setBorder(new EmptyBorder(12, 15, 12, 15));
        card.setBackground(notif.isRead() ? Color.WHITE : new Color(240, 255, 240));
        card.setOpaque(false);
        
        // Icon and Title
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topPanel.setOpaque(false);
        
        String icon = getIconForType(notif.getType());
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        
        JLabel titleLabel = new JLabel(notif.getTitle());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 15));
        titleLabel.setForeground(notif.isRead() ? Color.DARK_GRAY : new Color(34, 139, 34));
        
        topPanel.add(iconLabel);
        topPanel.add(titleLabel);
        
        // Message
        JLabel messageLabel = new JLabel("<html>" + notif.getMessage() + "</html>");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        messageLabel.setForeground(new Color(70, 70, 70));
        
        // Time and Actions
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        
        JLabel timeLabel = new JLabel(formatter.format(notif.getCreatedAt()));
        timeLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        timeLabel.setForeground(Color.GRAY);
        
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        actionPanel.setOpaque(false);
        
        if (!notif.isRead()) {
            JButton markReadBtn = createSmallButton("Mark Read", new Color(34, 139, 34));
            markReadBtn.addActionListener(e -> {
                NotificationDAO.markAsRead(notif.getId());
                loadNotifications();
            });
            actionPanel.add(markReadBtn);
        }
        
        // View button for specific notification types
        if (notif.getRelatedId() != null && !notif.getRelatedId().isEmpty()) {
            if (notif.getType().equals("BILL_CREATED")) {
                JButton viewBtn = createSmallButton("View Bill", new Color(0, 123, 255));
                viewBtn.addActionListener(e -> showBillDetails(notif.getRelatedId()));
                actionPanel.add(viewBtn);
            }
        }
        
        bottomPanel.add(timeLabel, BorderLayout.WEST);
        bottomPanel.add(actionPanel, BorderLayout.EAST);
        
        // Assemble card
        JPanel contentPanel = new JPanel(new BorderLayout(5, 5));
        contentPanel.setOpaque(false);
        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(messageLabel, BorderLayout.CENTER);
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        card.add(contentPanel, BorderLayout.CENTER);
        
        return card;
    }

    private JButton createSmallButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color paintColor = getModel().isRollover() ? bgColor.darker() : bgColor;
                g2.setColor(paintColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                super.paintComponent(g);
            }
        };
        button.setFont(new Font("Arial", Font.BOLD, 11));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setPreferredSize(new Dimension(90, 28));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
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

    private String getIconForType(String type) {
        switch (type) {
            case "CASHIER_REQUEST": return "[REQUEST]";
            case "CASHIER_APPROVED": return "[APPROVED]";
            case "CASHIER_REJECTED": return "[REJECTED]";
            case "BILL_CREATED": return "[BILL]";
            case "MONTHLY_REPORT": return "[REPORT]";
            case "STOCK_LOW": return "[ALERT]";
            default: return "[NOTIFY]";
        }
    }

    private void markAllAsRead() {
        if (NotificationDAO.markAllAsRead(currentUser.getId())) {
            loadNotifications();
            JOptionPane.showMessageDialog(this, 
                "All notifications marked as read", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showBillDetails(String billNumber) {
        // Get bill info
        Bill bill = BillDAO.getBillByNumber(billNumber);
        if (bill == null) {
            JOptionPane.showMessageDialog(this, 
                "Bill not found", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Get bill items
        var billItems = BillDAO.getBillItems(billNumber);
        
        if (billItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Bill details not found", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create dialog with same style as this dialog
        JDialog dialog = new JDialog(this, "Bill Details - " + billNumber, true);
        dialog.setSize(650, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(true);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(34, 139, 34));
        headerPanel.setBorder(new EmptyBorder(18, 20, 18, 20));
        
        JLabel headerLabel = new JLabel("Bill #" + billNumber);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setForeground(Color.WHITE);
        
        JLabel cashierLabel = new JLabel("Cashier: " + bill.getCashierName());
        cashierLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        cashierLabel.setForeground(Color.WHITE);
        
        JPanel headerTextPanel = new JPanel();
        headerTextPanel.setLayout(new BoxLayout(headerTextPanel, BoxLayout.Y_AXIS));
        headerTextPanel.setOpaque(false);
        headerTextPanel.add(headerLabel);
        headerTextPanel.add(Box.createVerticalStrut(5));
        headerTextPanel.add(cashierLabel);
        
        headerPanel.add(headerTextPanel, BorderLayout.WEST);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Table
        String[] columns = {"Product", "Quantity", "Unit Price", "Total"};
        Object[][] data = new Object[billItems.size()][4];
        
        double grandTotal = 0;
        for (int i = 0; i < billItems.size(); i++) {
            var item = billItems.get(i);
            double itemTotal = item.getQuantity() * item.getUnitPrice();
            data[i][0] = item.getProductName();
            data[i][1] = item.getQuantity();
            data[i][2] = String.format("$%.2f", item.getUnitPrice());
            data[i][3] = String.format("$%.2f", itemTotal);
            grandTotal += itemTotal;
        }

        JTable table = new JTable(data, columns);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(32);
        table.setEnabled(false);
        table.setGridColor(new Color(220, 220, 220));
        
        // Configure table header with proper visibility and no hover effects
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBackground(new Color(34, 139, 34));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 35));
        header.setOpaque(true);
        
        // Ensure white text is always visible
        table.getTableHeader().setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(value.toString());
                label.setFont(new Font("Arial", Font.BOLD, 14));
                label.setBackground(roleColor);
                label.setForeground(Color.WHITE);
                label.setOpaque(true);
                label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                label.setHorizontalAlignment(CENTER);
                return label;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Footer with total
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JLabel totalLabel = new JLabel("Grand Total: $" + String.format("%.2f", grandTotal));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalLabel.setForeground(new Color(34, 139, 34));
        footerPanel.add(totalLabel, BorderLayout.EAST);
        
        JButton closeBtn = createStyledButton("Close", new Color(108, 117, 125));
        closeBtn.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(closeBtn);
        
        footerPanel.add(buttonPanel, BorderLayout.SOUTH);
        contentPanel.add(footerPanel, BorderLayout.SOUTH);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
}
