package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import models.Notification;
import database.NotificationDAO;
import database.BillDAO;
import models.User;

public class NotificationPanel extends JPanel {
    private User currentUser;
    private JPanel notificationListPanel;
    private JLabel unreadCountLabel;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
    private Color roleColor;

    public NotificationPanel(User user) {
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
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("📬 Notifications");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(roleColor);
        
        unreadCountLabel = new JLabel();
        unreadCountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        unreadCountLabel.setForeground(new Color(220, 53, 69));
        
        JButton markAllReadBtn = new JButton("Mark All Read");
        markAllReadBtn.setFont(new Font("Arial", Font.PLAIN, 13));
        markAllReadBtn.setBackground(new Color(34, 139, 34));
        markAllReadBtn.setForeground(Color.WHITE);
        markAllReadBtn.setFocusPainted(false);
        markAllReadBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        markAllReadBtn.addActionListener(e -> markAllAsRead());
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        topPanel.setBackground(Color.WHITE);
        topPanel.add(titleLabel);
        topPanel.add(unreadCountLabel);
        
        headerPanel.add(topPanel, BorderLayout.WEST);
        headerPanel.add(markAllReadBtn, BorderLayout.EAST);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        add(headerPanel, BorderLayout.NORTH);

        // Notifications List
        notificationListPanel = new JPanel();
        notificationListPanel.setLayout(new BoxLayout(notificationListPanel, BoxLayout.Y_AXIS));
        notificationListPanel.setBackground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(notificationListPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        add(scrollPane, BorderLayout.CENTER);

        // Refresh Button
        JButton refreshBtn = new JButton("🔄 Refresh");
        refreshBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        refreshBtn.setBackground(new Color(34, 139, 34));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshBtn.addActionListener(e -> loadNotifications());
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(refreshBtn);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void loadNotifications() {
        notificationListPanel.removeAll();
        
        List<Notification> notifications = NotificationDAO.getAllNotifications(currentUser.getId(), 50);
        int unreadCount = NotificationDAO.getUnreadCount(currentUser.getId());
        
        unreadCountLabel.setText(unreadCount > 0 ? "(" + unreadCount + " unread)" : "");
        
        if (notifications.isEmpty()) {
            JLabel emptyLabel = new JLabel("No notifications yet");
            emptyLabel.setFont(new Font("Arial", Font.ITALIC, 16));
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
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(10, 5));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(notif.isRead() ? new Color(220, 220, 220) : new Color(34, 139, 34), 2),
            new EmptyBorder(10, 15, 10, 15)
        ));
        card.setBackground(notif.isRead() ? Color.WHITE : new Color(240, 255, 240));
        
        // Icon and Title
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topPanel.setOpaque(false);
        
        String icon = getIconForType(notif.getType());
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        
        JLabel titleLabel = new JLabel(notif.getTitle());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 15));
        titleLabel.setForeground(notif.isRead() ? Color.DARK_GRAY : new Color(34, 139, 34));
        
        topPanel.add(iconLabel);
        topPanel.add(titleLabel);
        
        // Message
        JLabel messageLabel = new JLabel("<html>" + notif.getMessage() + "</html>");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        messageLabel.setForeground(Color.DARK_GRAY);
        
        // Time and Actions
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        
        JLabel timeLabel = new JLabel(formatter.format(notif.getCreatedAt()));
        timeLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        timeLabel.setForeground(Color.GRAY);
        
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        actionPanel.setOpaque(false);
        
        if (!notif.isRead()) {
            JButton markReadBtn = new JButton("Mark Read");
            markReadBtn.setFont(new Font("Arial", Font.PLAIN, 11));
            markReadBtn.setBackground(new Color(34, 139, 34));
            markReadBtn.setForeground(Color.WHITE);
            markReadBtn.setFocusPainted(false);
            markReadBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            markReadBtn.addActionListener(e -> {
                NotificationDAO.markAsRead(notif.getId());
                loadNotifications();
            });
            actionPanel.add(markReadBtn);
        }
        
        // View button for specific notification types
        if (notif.getRelatedId() != null && !notif.getRelatedId().isEmpty()) {
            if (notif.getType().equals("BILL_CREATED")) {
                JButton viewBtn = new JButton("View Bill");
                viewBtn.setFont(new Font("Arial", Font.PLAIN, 11));
                viewBtn.setBackground(new Color(0, 123, 255));
                viewBtn.setForeground(Color.WHITE);
                viewBtn.setFocusPainted(false);
                viewBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
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

    private String getIconForType(String type) {
        switch (type) {
            case "CASHIER_REQUEST": return "👤";
            case "CASHIER_APPROVED": return "✅";
            case "CASHIER_REJECTED": return "❌";
            case "BILL_CREATED": return "🧾";
            case "MONTHLY_REPORT": return "📊";
            case "STOCK_LOW": return "⚠️";
            default: return "📬";
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
        // Get bill items
        var billItems = BillDAO.getBillItems(billNumber);
        
        if (billItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Bill details not found", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Bill Details - " + billNumber, true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Header
        JLabel headerLabel = new JLabel("Bill #" + billNumber);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setForeground(new Color(34, 139, 34));
        panel.add(headerLabel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Product", "Quantity", "Unit Price", "Total"};
        Object[][] data = new Object[billItems.size()][4];
        
        double grandTotal = 0;
        for (int i = 0; i < billItems.size(); i++) {
            var item = billItems.get(i);
            double itemTotal = item.getQuantity() * item.getUnitPrice();
            data[i][0] = item.getProductName();
            data[i][1] = item.getQuantity();
            data[i][2] = String.format("Rs. %.2f", item.getUnitPrice());
            data[i][3] = String.format("Rs. %.2f", itemTotal);
            grandTotal += itemTotal;
        }

        JTable table = new JTable(data, columns);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setEnabled(false);
        
        // Configure header with proper visibility
        javax.swing.table.JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBackground(roleColor);
        header.setForeground(Color.WHITE);
        header.setOpaque(true);
        
        table.getTableHeader().setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value,
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
        panel.add(scrollPane, BorderLayout.CENTER);

        // Footer with total
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel totalLabel = new JLabel("Grand Total: Rs. " + String.format("%.2f", grandTotal));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalLabel.setForeground(new Color(34, 139, 34));
        footerPanel.add(totalLabel);
        panel.add(footerPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    // Method to get unread count for badge display
    public static int getUnreadCount(int userId) {
        return NotificationDAO.getUnreadCount(userId);
    }
}
