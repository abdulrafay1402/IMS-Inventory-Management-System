package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import models.Manager;
import database.UserDAO;

// View Managers Panel
class ViewManagersPanel extends JPanel {
    private JTable managersTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton;

    public ViewManagersPanel() {
        initializeUI();
        loadManagers();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        JLabel headerLabel = new JLabel("All Managers", SwingConstants.LEFT);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 26));
        headerLabel.setForeground(new Color(0, 102, 204));

        refreshButton = createStyledButton("Refresh", new Color(23, 162, 184), Color.WHITE);
        refreshButton.setPreferredSize(new Dimension(160, 48));
        refreshButton.addActionListener(e -> loadManagers());

        headerPanel.add(headerLabel, BorderLayout.WEST);
        headerPanel.add(refreshButton, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Name", "Username", "Phone", "CNIC", "Salary (Rs.)", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        managersTable = new JTable(tableModel);
        managersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        managersTable.setFont(new Font("Arial", Font.PLAIN, 14));
        managersTable.setRowHeight(35);
        
        // Configure header with proper visibility
        javax.swing.table.JTableHeader header = managersTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 16));
        header.setBackground(new Color(0, 102, 204)); // CEO Blue
        header.setForeground(Color.WHITE);
        header.setOpaque(true);
        
        managersTable.getTableHeader().setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(value.toString());
                label.setFont(new Font("Arial", Font.BOLD, 16));
                label.setBackground(new Color(0, 102, 204)); // CEO Blue
                label.setForeground(Color.WHITE);
                label.setOpaque(true);
                label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                label.setHorizontalAlignment(CENTER);
                return label;
            }
        });

        JScrollPane scrollPane = new JScrollPane(managersTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        add(scrollPane, BorderLayout.CENTER);
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
    private void loadManagers() {
        tableModel.setRowCount(0);
        List<Manager> managers = UserDAO.getAllManagers();

        for (Manager manager : managers) {
            tableModel.addRow(new Object[]{
                    manager.getId(),
                    manager.getName(),
                    manager.getUsername(),
                    manager.getPhone(),
                    manager.getCnic(),
                    String.format("%,.2f", manager.getSalary()),
                    manager.getStatus()
            });
        }
    }
}

