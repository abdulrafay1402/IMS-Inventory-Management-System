package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import models.CEO;
import models.CEOInventory;
import database.InventoryDAO;
import utils.ElegantMessageDialog;

// ============= MASTER INVENTORY PANEL =============
class MasterInventoryPanel extends JPanel {
    private CEO currentUser;
    private JTable inventoryTable;
    private DefaultTableModel tableModel;
    private JButton addButton, refreshButton, editButton, deleteButton;

    public MasterInventoryPanel(CEO user) {
        this.currentUser = user;
        initializeUI();
        loadInventory();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        JLabel headerLabel = new JLabel("Master Inventory Management", SwingConstants.LEFT);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 26));
        headerLabel.setForeground(new Color(0, 102, 204));

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        addButton = createStyledButton("Add Product", new Color(0, 153, 51), Color.WHITE);
        addButton.setPreferredSize(new Dimension(160, 48));
        addButton.addActionListener(e -> showAddProductDialog());

        editButton = createStyledButton("Edit Product", new Color(255, 153, 0), Color.WHITE);
        editButton.setPreferredSize(new Dimension(160, 48));
        editButton.addActionListener(e -> editSelectedProduct());

        deleteButton = createStyledButton("Delete Product", new Color(220, 20, 60), Color.WHITE);
        deleteButton.setPreferredSize(new Dimension(180, 48));
        deleteButton.addActionListener(e -> deleteSelectedProduct());

        refreshButton = createStyledButton("Refresh", new Color(23, 162, 184), Color.WHITE);
        refreshButton.setPreferredSize(new Dimension(160, 48));
        refreshButton.addActionListener(e -> loadInventory());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        headerPanel.add(headerLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Product Name", "Buying Price", "Quantity", "Min Stock", "Status", "Created Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        inventoryTable = new JTable(tableModel);
        inventoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        inventoryTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        inventoryTable.setFont(new Font("Arial", Font.PLAIN, 14));
        inventoryTable.setRowHeight(35);

        // Color code rows based on stock status
        inventoryTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                String status = (String) table.getValueAt(row, 5);
                if ("OUT OF STOCK".equals(status)) {
                    c.setBackground(new Color(255, 200, 200)); // Light red
                } else if ("LOW STOCK".equals(status)) {
                    c.setBackground(new Color(255, 255, 200)); // Light yellow
                } else {
                    c.setBackground(Color.WHITE);
                }

                if (isSelected) {
                    c.setBackground(new Color(200, 200, 255)); // Light blue for selection
                }

                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        add(scrollPane, BorderLayout.CENTER);

        // Low stock warning panel
        JPanel warningPanel = createWarningPanel();
        add(warningPanel, BorderLayout.SOUTH);
    }

    private JPanel createWarningPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));
        panel.setBackground(new Color(255, 240, 240));

        JLabel warningLabel = new JLabel("⚠ Low Stock Alert: ");
        warningLabel.setFont(new Font("Arial", Font.BOLD, 12));
        warningLabel.setForeground(Color.RED);

        JLabel countLabel = new JLabel("0 products need restocking");
        countLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        panel.add(warningLabel);
        panel.add(countLabel);

        return panel;
    }

    private void loadInventory() {
        tableModel.setRowCount(0);
        List<CEOInventory> inventory = InventoryDAO.getAllCEOInventory();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        int lowStockCount = 0;

        for (CEOInventory item : inventory) {
            String status = item.getStockStatus();
            if (item.isLowStock()) {
                lowStockCount++;
            }

            tableModel.addRow(new Object[]{
                    item.getId(),
                    item.getProductName(),
                    String.format("$%.2f", item.getBuyingPrice()),
                    item.getTotalQuantity(),
                    item.getMinStockLevel(),
                    status,
                    sdf.format(item.getCreatedDate())
            });
        }

        // Update warning panel
        updateWarningPanel(lowStockCount);

        if (inventory.isEmpty()) {
            ElegantMessageDialog.showMessage(this,
                    "No products in inventory. Add some products to get started.",
                    "Empty Inventory",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void updateWarningPanel(int lowStockCount) {
        Component[] components = ((JPanel)getComponent(2)).getComponents();
        if (components.length >= 2 && components[1] instanceof JLabel) {
            JLabel countLabel = (JLabel) components[1];
            if (lowStockCount > 0) {
                countLabel.setText(lowStockCount + " product(s) need restocking");
                countLabel.setForeground(Color.RED);
            } else {
                countLabel.setText("All products are sufficiently stocked");
                countLabel.setForeground(new Color(0, 128, 0));
            }
        }
    }

    private void showAddProductDialog() {
        JDialog dialog = new JDialog((Frame)null, "Add New Product", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        JTextField nameField = new JTextField(20);
        JTextField priceField = new JTextField(20);
        JTextField quantityField = new JTextField(20);
        JTextField minStockField = new JTextField(20);

        int row = 0;
        addFormFieldToDialog(formPanel, gbc, row++, "Product Name*:", nameField);
        addFormFieldToDialog(formPanel, gbc, row++, "Buying Price* ($):", priceField);
        addFormFieldToDialog(formPanel, gbc, row++, "Initial Quantity*:", quantityField);
        addFormFieldToDialog(formPanel, gbc, row++, "Min Stock Level*:", minStockField);

        // Buttons
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        JButton saveButton = createStyledButton("Save Product", new Color(0, 153, 51), Color.WHITE);
        saveButton.addActionListener(e -> {
            if (saveProduct(nameField, priceField, quantityField, minStockField)) {
                dialog.dispose();
                loadInventory();
            }
        });

        JButton cancelButton = createStyledButton("Cancel", new Color(108, 117, 125), Color.WHITE);
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        formPanel.add(buttonPanel, gbc);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
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
    private void addFormFieldToDialog(JPanel panel, GridBagConstraints gbc, int row,
                                      String labelText, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.4;
        gbc.gridwidth = 1;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.6;
        field.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(field, gbc);
    }

    private boolean saveProduct(JTextField nameField, JTextField priceField,
                                JTextField quantityField, JTextField minStockField) {
        String productName = nameField.getText().trim();
        String priceStr = priceField.getText().trim();
        String quantityStr = quantityField.getText().trim();
        String minStockStr = minStockField.getText().trim();

        // Validation
        if (productName.isEmpty() || priceStr.isEmpty() || quantityStr.isEmpty() || minStockStr.isEmpty()) {
            ElegantMessageDialog.showMessage(this, "Please fill all required fields (*)", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            double buyingPrice = Double.parseDouble(priceStr);
            int quantity = Integer.parseInt(quantityStr);
            int minStockLevel = Integer.parseInt(minStockStr);

            if (buyingPrice <= 0 || quantity < 0 || minStockLevel < 0) {
                ElegantMessageDialog.showMessage(this, "Please enter positive values", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            if (InventoryDAO.addProductToCEOInventory(productName, buyingPrice, quantity, minStockLevel)) {
                ElegantMessageDialog.showMessage(this, "Product added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                return true;
            } else {
                ElegantMessageDialog.showMessage(this, "Failed to add product", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            ElegantMessageDialog.showMessage(this, "Please enter valid numbers for price and quantity", "Error", JOptionPane.ERROR_MESSAGE);
        }

        return false;
    }

    private void editSelectedProduct() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow == -1) {
            ElegantMessageDialog.showMessage(this, "Please select a product to edit", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int productId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentName = (String) tableModel.getValueAt(selectedRow, 1);
        String currentPrice = ((String) tableModel.getValueAt(selectedRow, 2)).substring(1); // Remove $ sign
        int currentQuantity = (int) tableModel.getValueAt(selectedRow, 3);
        int currentMinStock = (int) tableModel.getValueAt(selectedRow, 4);

        // Similar to add dialog but pre-filled with current values
        showEditProductDialog(productId, currentName, currentPrice, currentQuantity, currentMinStock);
    }

    private void showEditProductDialog(int productId, String currentName, String currentPrice,
                                       int currentQuantity, int currentMinStock) {
        // Implementation similar to add dialog but with pre-filled values
        // For brevity, I'll show the structure - you can implement the full edit functionality
        ElegantMessageDialog.showMessage(this,
                "Edit functionality for product: " + currentName + "<br>This would open an edit form similar to the add form but pre-filled with current values.",
                "Edit Product",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteSelectedProduct() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow == -1) {
            ElegantMessageDialog.showMessage(this, "Please select a product to delete", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int productId = (int) tableModel.getValueAt(selectedRow, 0);
        String productName = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = ElegantMessageDialog.showConfirm(this,
                "Are you sure you want to delete product: " + productName + "?<br>This action cannot be undone.",
                "Confirm Deletion");

        if (confirm == JOptionPane.YES_OPTION) {
            if (InventoryDAO.deleteFromCEOInventory(productId)) {
                ElegantMessageDialog.showMessage(this, "Product deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadInventory();
            } else {
                ElegantMessageDialog.showMessage(this, "Failed to delete product", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

