package ui;

import models.*;
import database.CashierInventoryDAO;
import utils.ElegantMessageDialog;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

// ============= CREATE BILL PANEL =============
class CreateBillPanel extends JPanel {
    private Cashier currentUser;
    private JTable productsTable;
    private DefaultTableModel productsTableModel;
    private JTable cartTable;
    private DefaultTableModel cartTableModel;
    private JButton addToCartButton, removeFromCartButton, createBillButton, clearCartButton;
    private JSpinner quantitySpinner;
    private JLabel totalLabel;
    private List<CartItem> cartItems;
    private int managerId;
    
    // Teal color for cashier
    private static final Color CASHIER_COLOR = new Color(0, 150, 136);

    public CreateBillPanel(Cashier user) {
        this.currentUser = user;
        this.cartItems = new ArrayList<>();
        this.managerId = CashierInventoryDAO.getManagerIdForCashier(currentUser.getId());
        initializeUI();
        loadProducts();
    }

    private void initializeUI() {
        // Get screen dimensions for responsive sizing
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = (int) screenSize.getWidth();
        int screenHeight = (int) screenSize.getHeight();
        
        // Responsive padding
        int padding = Math.max(20, Math.min(screenWidth, screenHeight) / 30);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(padding, padding, padding, padding));

        // Header with responsive font
        int headerFontSize = Math.max(20, screenWidth / 60);
        JLabel headerLabel = new JLabel("Create New Bill", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, headerFontSize));
        headerLabel.setForeground(CASHIER_COLOR);
        int headerBottomPadding = Math.max(15, screenHeight / 50);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, headerBottomPadding, 0));
        add(headerLabel, BorderLayout.NORTH);

        // Main split panel with percentage-based divider
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        // Set divider to 50% of available width (responsive)
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerLocation(0.5); // 50% split

        // Left panel - Available products
        JPanel leftPanel = createProductsPanel();
        splitPane.setLeftComponent(leftPanel);

        // Right panel - Cart
        JPanel rightPanel = createCartPanel();
        splitPane.setRightComponent(rightPanel);

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createProductsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Available Products"));

        // Products table
        String[] columns = {"ID", "Product Name", "Price", "Available Qty"};
        productsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productsTable = new JTable(productsTableModel);
        productsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Responsive table fonts
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int tableHeaderFontSize = Math.max(12, screenSize.width / 100);
        int tableFontSize = Math.max(11, screenSize.width / 110);
        int tableRowHeight = Math.max(25, screenSize.height / 35);
        productsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, tableHeaderFontSize));
        productsTable.setFont(new Font("Arial", Font.PLAIN, tableFontSize));
        productsTable.setRowHeight(tableRowHeight);

        JScrollPane scrollPane = new JScrollPane(productsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Control panel with responsive sizing
        int controlGap = Math.max(10, screenSize.width / 100);
        int controlVGap = Math.max(8, screenSize.height / 100);
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, controlGap, controlVGap));
        int labelFontSize = Math.max(12, screenSize.width / 100);
        JLabel quantityLabel = new JLabel("Quantity:");
        quantityLabel.setFont(new Font("Arial", Font.PLAIN, labelFontSize));
        controlPanel.add(quantityLabel);
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        quantitySpinner.setFont(new Font("Arial", Font.PLAIN, labelFontSize));
        int spinnerWidth = Math.max(80, screenSize.width / 20);
        int spinnerHeight = Math.max(30, screenSize.height / 30);
        quantitySpinner.setPreferredSize(new Dimension(spinnerWidth, spinnerHeight));
        controlPanel.add(quantitySpinner);

        int buttonWidth = Math.max(140, screenSize.width / 12);
        int buttonHeight = Math.max(35, screenSize.height / 25);
        addToCartButton = createStyledButton("Add to Cart", new Color(0, 153, 51), Color.WHITE);
        addToCartButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        addToCartButton.addActionListener(e -> addToCart());
        controlPanel.add(addToCartButton);

        JButton refreshButton = createStyledButton("Refresh", new Color(23, 162, 184), Color.WHITE);
        refreshButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        refreshButton.addActionListener(e -> loadProducts());
        controlPanel.add(refreshButton);

        panel.add(controlPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Shopping Cart"));

        // Cart table
        String[] columns = {"Product", "Price", "Qty", "Subtotal"};
        cartTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        cartTable = new JTable(cartTableModel);
        cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Responsive table fonts
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int tableHeaderFontSize = Math.max(12, screenSize.width / 100);
        int tableFontSize = Math.max(11, screenSize.width / 110);
        int tableRowHeight = Math.max(25, screenSize.height / 35);
        cartTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, tableHeaderFontSize));
        cartTable.setFont(new Font("Arial", Font.PLAIN, tableFontSize));
        cartTable.setRowHeight(tableRowHeight);

        JScrollPane scrollPane = new JScrollPane(cartTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with total and buttons
        JPanel bottomPanel = new JPanel(new BorderLayout());

        // Total panel with responsive sizing
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.setBackground(new Color(240, 240, 240));
        int totalPadding = Math.max(8, screenSize.height / 100);
        totalPanel.setBorder(BorderFactory.createEmptyBorder(totalPadding, totalPadding, totalPadding, totalPadding));
        totalLabel = new JLabel("Total: $0.00");
        int totalFontSize = Math.max(16, screenSize.width / 80);
        totalLabel.setFont(new Font("Arial", Font.BOLD, totalFontSize));
        totalLabel.setForeground(CASHIER_COLOR);
        totalPanel.add(totalLabel);
        bottomPanel.add(totalPanel, BorderLayout.NORTH);

        // Button panel with responsive sizing
        int buttonGap = Math.max(8, screenSize.width / 120);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, buttonGap, buttonGap));
        int buttonWidth = Math.max(140, screenSize.width / 12);
        int buttonHeight = Math.max(35, screenSize.height / 25);

        removeFromCartButton = createStyledButton("Remove Item", new Color(255, 153, 0), Color.WHITE);
        removeFromCartButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        removeFromCartButton.addActionListener(e -> removeFromCart());
        buttonPanel.add(removeFromCartButton);

        clearCartButton = createStyledButton("Clear Cart", new Color(108, 117, 125), Color.WHITE);
        clearCartButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        clearCartButton.addActionListener(e -> clearCart());
        buttonPanel.add(clearCartButton);

        createBillButton = createStyledButton("Create Bill", CASHIER_COLOR, Color.WHITE);
        createBillButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        createBillButton.addActionListener(e -> createBill());
        buttonPanel.add(createBillButton);

        bottomPanel.add(buttonPanel, BorderLayout.CENTER);

        panel.add(bottomPanel, BorderLayout.SOUTH);

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
    private void loadProducts() {
        productsTableModel.setRowCount(0);

        if (managerId == -1) {
            ElegantMessageDialog.showMessage(this,
                    "You are not assigned to any manager yet.<br>Please contact the administrator.",
                    "No Manager Assigned",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<ManagerInventory> products = CashierInventoryDAO.getAvailableProductsForCashier(currentUser.getId());

        for (ManagerInventory product : products) {
            productsTableModel.addRow(new Object[]{
                    product.getId(),
                    product.getProductName(),
                    String.format("$%.2f", product.getSellingPrice()),
                    product.getCurrentQuantity()
            });
        }

        // Removed message box - user can see empty table
    }

    private void addToCart() {
        int selectedRow = productsTable.getSelectedRow();
        if (selectedRow == -1) {
            ElegantMessageDialog.showMessage(this,
                    "Please select a product to add to cart.",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int productId = (int) productsTableModel.getValueAt(selectedRow, 0);
        String productName = (String) productsTableModel.getValueAt(selectedRow, 1);
        String priceStr = (String) productsTableModel.getValueAt(selectedRow, 2);
        double price = Double.parseDouble(priceStr.substring(1)); // Remove $ sign
        int availableQty = (int) productsTableModel.getValueAt(selectedRow, 3);
        int quantity = (int) quantitySpinner.getValue();

        // Check if already in cart
        for (CartItem item : cartItems) {
            if (item.getProductId() == productId) {
                ElegantMessageDialog.showMessage(this,
                        "Product already in cart. Remove it first to change quantity.",
                        "Duplicate Item",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        // Validate quantity
        if (quantity > availableQty) {
            ElegantMessageDialog.showMessage(this,
                    String.format("Only %d units available!", availableQty),
                    "Insufficient Stock",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Add to cart
        CartItem item = new CartItem(productId, productName, price, quantity);
        cartItems.add(item);

        cartTableModel.addRow(new Object[]{
                productName,
                String.format("$%.2f", price),
                quantity,
                String.format("$%.2f", price * quantity)
        });

        updateTotal();
        quantitySpinner.setValue(1); // Reset quantity
    }

    private void removeFromCart() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow == -1) {
            ElegantMessageDialog.showMessage(this,
                    "Please select an item to remove from cart.",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        cartItems.remove(selectedRow);
        cartTableModel.removeRow(selectedRow);
        updateTotal();
    }

    private void clearCart() {
        if (cartItems.isEmpty()) {
            return;
        }

        int confirm = ElegantMessageDialog.showConfirm(this,
                "Are you sure you want to clear the cart?",
                "Confirm Clear");

        if (confirm == JOptionPane.YES_OPTION) {
            cartItems.clear();
            cartTableModel.setRowCount(0);
            updateTotal();
        }
    }

    private void createBill() {
        if (cartItems.isEmpty()) {
            ElegantMessageDialog.showMessage(this,
                    "Cart is empty. Please add products to create a bill.",
                    "Empty Cart",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = ElegantMessageDialog.showConfirm(this,
                String.format("Create bill for total amount: $%.2f?", calculateTotal()),
                "Confirm Bill");

        if (confirm == JOptionPane.YES_OPTION) {
            // Convert cart items to bill items
            List<BillItem> billItems = new ArrayList<>();
            for (CartItem item : cartItems) {
                billItems.add(new BillItem(item.getProductId(), item.getQuantity(), item.getPrice()));
            }

            double totalAmount = calculateTotal();

            if (CashierInventoryDAO.createBill(currentUser.getId(), managerId, billItems, totalAmount)) {
                // Clear cart and refresh products
                clearCart();
                loadProducts();
            } else {
                ElegantMessageDialog.showMessage(this,
                        "Failed to create bill. Please check stock availability and try again.",
                        "Bill Creation Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateTotal() {
        double total = calculateTotal();
        totalLabel.setText(String.format("Total: $%.2f", total));
    }

    private double calculateTotal() {
        return cartItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    // Helper class for cart items
    private class CartItem {
        private int productId;
        private String productName;
        private double price;
        private int quantity;

        public CartItem(int productId, String productName, double price, int quantity) {
            this.productId = productId;
            this.productName = productName;
            this.price = price;
            this.quantity = quantity;
        }

        public int getProductId() { return productId; }
        public String getProductName() { return productName; }
        public double getPrice() { return price; }
        public int getQuantity() { return quantity; }
    }
}

