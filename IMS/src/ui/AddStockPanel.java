package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import models.Manager;
import models.CEOInventory;
import database.InventoryDAO;
import database.StockTransferDAO;
import utils.ElegantMessageDialog;

// ============= ADD STOCK PANEL =============
class AddStockPanel extends JPanel {
    private Manager currentUser;
    private JTable availableProductsTable;
    private DefaultTableModel availableTableModel;
    private JTable selectedProductsTable;
    private DefaultTableModel selectedTableModel;
    private JButton addButton, removeButton, requestStockButton, refreshButton;
    private JSpinner quantitySpinner;
    private JTextField sellingPriceField;
    private java.util.Map<Integer, CEOInventory> availableProductsMap;
    private java.util.Map<Integer, SelectedProduct> selectedProductsMap;
    private double currentMinSellingPrice = 0.0;

    public AddStockPanel(Manager user) {
        this.currentUser = user;
        this.availableProductsMap = new java.util.HashMap<>();
        this.selectedProductsMap = new java.util.HashMap<>();
        initializeUI();
        loadAvailableProducts();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header
        JLabel headerLabel = new JLabel("Add Stock from CEO Inventory", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 26));
        headerLabel.setForeground(new Color(34, 139, 34));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));
        add(headerLabel, BorderLayout.NORTH);

        // Main content panel
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 15, 0));

        // Left panel - Available products
        mainPanel.add(createAvailableProductsPanel());

        // Right panel - Selected products and controls
        mainPanel.add(createSelectionPanel());

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createAvailableProductsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Available Products from CEO"));

        // Table
        String[] columns = {"ID", "Product Name", "Available Qty", "Buying Price", "Min Selling Price"};
        availableTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        availableProductsTable = new JTable(availableTableModel);
        availableProductsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        availableProductsTable.setFont(new Font("Arial", Font.PLAIN, 14));
        availableProductsTable.setRowHeight(35);
        
        // Configure header with proper visibility
        javax.swing.table.JTableHeader availableHeader = availableProductsTable.getTableHeader();
        availableHeader.setFont(new Font("Arial", Font.BOLD, 16));
        availableHeader.setBackground(new Color(34, 139, 34)); // Manager Green
        availableHeader.setForeground(Color.WHITE);
        availableHeader.setOpaque(true);
        
        availableProductsTable.getTableHeader().setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(value.toString());
                label.setFont(new Font("Arial", Font.BOLD, 16));
                label.setBackground(new Color(34, 139, 34));
                label.setForeground(Color.WHITE);
                label.setOpaque(true);
                label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                label.setHorizontalAlignment(CENTER);
                return label;
            }
        });

        // Add mouse listener to update selling price when product is selected
        availableProductsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                updateSellingPriceForSelectedProduct();
            }
        });

        JScrollPane scrollPane = new JScrollPane(availableProductsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Refresh button
        refreshButton = createStyledButton("Refresh", new Color(23, 162, 184), Color.WHITE);
        refreshButton.setPreferredSize(new Dimension(160, 48));
        refreshButton.addActionListener(e -> loadAvailableProducts());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Selected Products for Request"));

        // Selected products table
        String[] columns = {"Product", "Request Qty", "Buying Price", "Selling Price", "Subtotal"};
        selectedTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        selectedProductsTable = new JTable(selectedTableModel);
        selectedProductsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectedProductsTable.setFont(new Font("Arial", Font.PLAIN, 14));
        selectedProductsTable.setRowHeight(35);
        
        // Configure header with proper visibility
        javax.swing.table.JTableHeader selectedHeader = selectedProductsTable.getTableHeader();
        selectedHeader.setFont(new Font("Arial", Font.BOLD, 16));
        selectedHeader.setBackground(new Color(34, 139, 34)); // Manager Green
        selectedHeader.setForeground(Color.WHITE);
        selectedHeader.setOpaque(true);
        
        selectedProductsTable.getTableHeader().setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(value.toString());
                label.setFont(new Font("Arial", Font.BOLD, 16));
                label.setBackground(new Color(34, 139, 34));
                label.setForeground(Color.WHITE);
                label.setOpaque(true);
                label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                label.setHorizontalAlignment(CENTER);
                return label;
            }
        });

        JScrollPane scrollPane = new JScrollPane(selectedProductsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Control panel
        JPanel controlPanel = new JPanel(new GridLayout(3, 1, 5, 5));

        // Quantity and price controls
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        JLabel quantityLabel = new JLabel("Quantity:");
        quantityLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        inputPanel.add(quantityLabel);
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        quantitySpinner.setFont(new Font("Arial", Font.PLAIN, 16));
        quantitySpinner.setPreferredSize(new Dimension(100, 40));
        inputPanel.add(quantitySpinner);

        JLabel priceLabel = new JLabel("Selling Price ($):");
        priceLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        inputPanel.add(priceLabel);
        sellingPriceField = new JTextField(10);
        sellingPriceField.setFont(new Font("Arial", Font.PLAIN, 16));
        sellingPriceField.setPreferredSize(new Dimension(150, 40));
        sellingPriceField.setToolTipText("Minimum selling price: $0.00");
        inputPanel.add(sellingPriceField);

        controlPanel.add(inputPanel);

        // Action buttons

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        addButton = createStyledButton("Add", new Color(0, 153, 51), Color.WHITE);
        addButton.setPreferredSize(new Dimension(180, 48));
        addButton.addActionListener(e -> addToRequest());

        removeButton = createStyledButton("Remove", new Color(220, 20, 60), Color.WHITE);
        removeButton.setPreferredSize(new Dimension(180, 48));
        removeButton.setFont(new Font("Arial", Font.BOLD, 15)); // Slightly smaller font to fit text
        removeButton.addActionListener(e -> removeFromRequest());

        actionPanel.add(addButton);
        actionPanel.add(removeButton);
        controlPanel.add(actionPanel);

// Request button
        JPanel requestPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        requestStockButton = createStyledButton("Submit Request", new Color(34, 139, 34), Color.WHITE);
        requestStockButton.setPreferredSize(new Dimension(220, 48));
        requestStockButton.addActionListener(e -> submitStockRequest());
        requestPanel.add(requestStockButton);

        controlPanel.add(requestPanel);

        panel.add(controlPanel, BorderLayout.SOUTH);

        return panel;
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
    private void loadAvailableProducts() {
        availableTableModel.setRowCount(0);
        availableProductsMap.clear();

        java.util.List<CEOInventory> products = InventoryDAO.getAllCEOInventory();

        for (CEOInventory product : products) {
            if (product.getTotalQuantity() > 0) { // Only show products with available stock
                double buyingPrice = product.getBuyingPrice();
                double minSellingPrice = calculateMinSellingPrice(buyingPrice);

                availableTableModel.addRow(new Object[]{
                        product.getId(),
                        product.getProductName(),
                        product.getTotalQuantity(),
                        String.format("$%.2f", buyingPrice),
                        String.format("$%.2f", minSellingPrice)
                });
                availableProductsMap.put(product.getId(), product);
            }
        }

        // Removed message box - user can see empty table
    }

    private void updateSellingPriceForSelectedProduct() {
        int selectedRow = availableProductsTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        int productId = (int) availableTableModel.getValueAt(selectedRow, 0);
        CEOInventory product = availableProductsMap.get(productId);

        if (product != null) {
            double buyingPrice = product.getBuyingPrice();
            double minSellingPrice = calculateMinSellingPrice(buyingPrice);
            currentMinSellingPrice = minSellingPrice;

            // Set the selling price field with the minimum selling price
            sellingPriceField.setText(String.format("%.2f", minSellingPrice));
            sellingPriceField.setToolTipText(String.format("Minimum selling price: $%.2f",
                    minSellingPrice));

            // Select all text for easy editing
            sellingPriceField.selectAll();
            sellingPriceField.requestFocus();
        }
    }

    private double calculateMinSellingPrice(double buyingPrice) {
        // Calculate 20% markup
        return buyingPrice * 1.20;
    }

    private void addToRequest() {
        int selectedRow = availableProductsTable.getSelectedRow();
        if (selectedRow == -1) {
            ElegantMessageDialog.showMessage(this, "Please select a product from available products.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int productId = (int) availableTableModel.getValueAt(selectedRow, 0);
        String productName = (String) availableTableModel.getValueAt(selectedRow, 1);
        int availableQty = (int) availableTableModel.getValueAt(selectedRow, 2);
        int requestQty = (int) quantitySpinner.getValue();

        CEOInventory product = availableProductsMap.get(productId);
        double buyingPrice = product.getBuyingPrice();
        double minSellingPrice = calculateMinSellingPrice(buyingPrice);

        double sellingPrice;
        try {
            sellingPrice = Double.parseDouble(sellingPriceField.getText());
            if (sellingPrice < minSellingPrice) {
                ElegantMessageDialog.showMessage(this,
                        String.format("Selling price must be at least $%.2f",
                                minSellingPrice),
                        "Invalid Price",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            ElegantMessageDialog.showMessage(this, "Please enter a valid selling price.", "Invalid Price", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (requestQty <= 0) {
            ElegantMessageDialog.showMessage(this, "Quantity must be greater than 0.", "Invalid Quantity", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (requestQty > availableQty) {
            ElegantMessageDialog.showMessage(this,
                    String.format("Requested quantity (%d) exceeds available quantity (%d).", requestQty, availableQty),
                    "Insufficient Stock",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check if product already in request
        if (selectedProductsMap.containsKey(productId)) {
            ElegantMessageDialog.showMessage(this, "Product already in request list. Remove it first to change quantity.", "Duplicate Product", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Add to selected products
        SelectedProduct selectedProduct = new SelectedProduct(productId, productName, requestQty, buyingPrice, sellingPrice);
        selectedProductsMap.put(productId, selectedProduct);

        selectedTableModel.addRow(new Object[]{
                productName,
                requestQty,
                String.format("$%.2f", buyingPrice),
                String.format("$%.2f", sellingPrice),
                String.format("$%.2f", requestQty * sellingPrice)
        });

        // Reset inputs
        quantitySpinner.setValue(1);
        sellingPriceField.setText("0.00");
        currentMinSellingPrice = 0.0;
        sellingPriceField.setToolTipText("Minimum selling price: $0.00");
    }

    private void removeFromRequest() {
        int selectedRow = selectedProductsTable.getSelectedRow();
        if (selectedRow == -1) {
            ElegantMessageDialog.showMessage(this, "Please select a product to remove.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String productName = (String) selectedTableModel.getValueAt(selectedRow, 0);

        // Find product ID by name (since we don't store ID in the table)
        int productIdToRemove = -1;
        for (java.util.Map.Entry<Integer, SelectedProduct> entry : selectedProductsMap.entrySet()) {
            if (entry.getValue().getProductName().equals(productName)) {
                productIdToRemove = entry.getKey();
                break;
            }
        }

        if (productIdToRemove != -1) {
            selectedProductsMap.remove(productIdToRemove);
            selectedTableModel.removeRow(selectedRow);
        }
    }

    private void submitStockRequest() {
        if (selectedProductsMap.isEmpty()) {
            ElegantMessageDialog.showMessage(this, "Please add products to the request list.", "Empty Request", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = ElegantMessageDialog.showConfirm(this,
                "Are you sure you want to submit this stock request?<br>This will transfer stock from CEO inventory to your inventory.",
                "Confirm Stock Request");

        if (confirm == JOptionPane.YES_OPTION) {
            // Process each selected product
            boolean success = true;
            for (SelectedProduct product : selectedProductsMap.values()) {
                if (!StockTransferDAO.transferStockFromCEO(currentUser.getId(), product.getProductId(),
                        product.getQuantity(), product.getSellingPrice())) {
                    success = false;
                    break;
                }
            }

            if (success) {
                // Clear selections
                selectedProductsMap.clear();
                selectedTableModel.setRowCount(0);
                loadAvailableProducts(); // Refresh available products
            } else {
                ElegantMessageDialog.showMessage(this,
                        "Failed to process some stock requests. Please try again.",
                        "Request Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Helper class for selected products
    private class SelectedProduct {
        private int productId;
        private String productName;
        private int quantity;
        private double buyingPrice;
        private double sellingPrice;

        public SelectedProduct(int productId, String productName, int quantity, double buyingPrice, double sellingPrice) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.buyingPrice = buyingPrice;
            this.sellingPrice = sellingPrice;
        }

        public int getProductId() { return productId; }
        public String getProductName() { return productName; }
        public int getQuantity() { return quantity; }
        public double getBuyingPrice() { return buyingPrice; }
        public double getSellingPrice() { return sellingPrice; }
    }
}

