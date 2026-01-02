package ui;

import javax.swing.*;
import java.awt.*;
import utils.ElegantMessageDialog;

public class IndividualPaymentDialog extends JDialog {
    private JTextField bonusField;
    private JComboBox<String> adjustmentTypeCombo;
    private JTextField adjustmentAmountField;
    private JLabel finalAmountLabel;
    private boolean confirmed = false;
    
    private double baseSalary;
    private double bonus = 0;
    private double adjustment = 0;
    
    public IndividualPaymentDialog(JFrame parent, String employeeName, String role, double baseSalary, String month) {
        super(parent, "Pay Salary - " + employeeName, true);
        this.baseSalary = baseSalary;
        
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
        
        setLayout(new BorderLayout(0, 0));
        setSize(650, 580);
        setLocationRelativeTo(parent);
        setResizable(false);
        
        // Header Panel with gradient effect
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(41, 128, 185), getWidth(), 0, new Color(52, 152, 219));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setPreferredSize(new Dimension(650, 80));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 35, 20, 35));
        headerPanel.setLayout(new BorderLayout());
        
        JLabel headerLabel = new JLabel("Individual Salary Payment");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setForeground(Color.WHITE);
        
        JLabel subHeaderLabel = new JLabel(employeeName + " (" + role + ") - " + month);
        subHeaderLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subHeaderLabel.setForeground(new Color(236, 240, 241));
        
        JPanel headerTextPanel = new JPanel(new BorderLayout(0, 6));
        headerTextPanel.setOpaque(false);
        headerTextPanel.add(headerLabel, BorderLayout.NORTH);
        headerTextPanel.add(subHeaderLabel, BorderLayout.CENTER);
        
        headerPanel.add(headerTextPanel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);
        
        // Form Panel with better styling
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(250, 251, 252));
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 5, 10, 5);
        
        int row = 0;
        
        // Base Salary Display
        JPanel salaryCard = new JPanel(new BorderLayout(10, 5));
        salaryCard.setBackground(Color.WHITE);
        salaryCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(18, 22, 18, 22)
        ));
        
        JLabel salaryLabel = new JLabel("Base Salary");
        salaryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        salaryLabel.setForeground(new Color(127, 140, 141));
        
        JLabel salaryValue = new JLabel("$" + String.format("%,.2f", baseSalary));
        salaryValue.setFont(new Font("Segoe UI", Font.BOLD, 20));
        salaryValue.setForeground(new Color(44, 62, 80));
        
        salaryCard.add(salaryLabel, BorderLayout.NORTH);
        salaryCard.add(salaryValue, BorderLayout.CENTER);
        
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 25, 0);
        formPanel.add(salaryCard, gbc);
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 5, 10, 5);
        
        // Bonus
        addFormField(formPanel, gbc, row++, "Bonus Amount (Rs.):");
        bonusField = new JTextField(15);
        bonusField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        bonusField.setText("0");
        bonusField.setBackground(Color.WHITE);
        bonusField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        bonusField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                updateFinalAmount();
            }
        });
        gbc.gridx = 1;
        gbc.gridy = row - 1;
        gbc.weightx = 0.6;
        formPanel.add(bonusField, gbc);
        
        // Adjustment Type
        addFormField(formPanel, gbc, row++, "Adjustment Type:");
        adjustmentTypeCombo = new JComboBox<>(new String[]{"None", "Increment", "Decrement"});
        adjustmentTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        adjustmentTypeCombo.setBackground(Color.WHITE);
        adjustmentTypeCombo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        adjustmentTypeCombo.addActionListener(e -> {
            adjustmentAmountField.setEnabled(!adjustmentTypeCombo.getSelectedItem().equals("None"));
            if (adjustmentTypeCombo.getSelectedItem().equals("None")) {
                adjustmentAmountField.setText("0");
            }
            updateFinalAmount();
        });
        gbc.gridx = 1;
        gbc.gridy = row - 1;
        gbc.weightx = 0.6;
        formPanel.add(adjustmentTypeCombo, gbc);
        
        // Adjustment Amount
        addFormField(formPanel, gbc, row++, "Adjustment Amount (Rs.):");
        adjustmentAmountField = new JTextField(15);
        adjustmentAmountField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        adjustmentAmountField.setText("0");
        adjustmentAmountField.setEnabled(false);
        adjustmentAmountField.setBackground(new Color(236, 240, 241));
        adjustmentAmountField.setDisabledTextColor(new Color(127, 140, 141));
        adjustmentAmountField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        adjustmentAmountField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                updateFinalAmount();
            }
        });
        gbc.gridx = 1;
        gbc.gridy = row - 1;
        gbc.weightx = 0.6;
        formPanel.add(adjustmentAmountField, gbc);
        
        // Final Amount Card
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 0, 0, 0);
        
        JPanel finalCard = new JPanel(new BorderLayout(10, 8));
        finalCard.setBackground(new Color(231, 242, 250));
        finalCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
            BorderFactory.createEmptyBorder(16, 25, 16, 25)
        ));
        
        JLabel finalLabel = new JLabel("Total Payable Amount");
        finalLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        finalLabel.setForeground(new Color(52, 73, 94));
        
        finalAmountLabel = new JLabel("$" + String.format("%,.2f", baseSalary));
        finalAmountLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        finalAmountLabel.setForeground(new Color(41, 128, 185));
        
        finalCard.add(finalLabel, BorderLayout.NORTH);
        finalCard.add(finalAmountLabel, BorderLayout.CENTER);
        formPanel.add(finalCard, gbc);
        
        add(formPanel, BorderLayout.CENTER);
        
        // Button Panel with modern styling
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 18));
        buttonPanel.setBackground(new Color(250, 251, 252));
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 223, 225)));
        
        JButton confirmButton = createStyledButton("Confirm Payment", new Color(39, 174, 96), Color.WHITE);
        confirmButton.setPreferredSize(new Dimension(220, 48));
        confirmButton.addActionListener(e -> confirmPayment());
        
        JButton cancelButton = createStyledButton("Cancel", new Color(149, 165, 166), Color.WHITE);
        cancelButton.setPreferredSize(new Dimension(140, 48));
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JButton createStyledButton(String text, Color bgColor, Color textColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color paintColor = getModel().isRollover() ? bgColor.brighter() : bgColor;
                g2.setColor(paintColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(textColor);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String labelText) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.4;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(new Color(52, 73, 94));
        panel.add(label, gbc);
    }
    
    private void addInfoField(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.45;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(100, 100, 100));
        panel.add(lbl, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.55;
        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        val.setForeground(new Color(40, 40, 40));
        panel.add(val, gbc);
    }
    
    private void updateFinalAmount() {
        try {
            double bonusValue = bonusField.getText().trim().isEmpty() ? 0 : Double.parseDouble(bonusField.getText().trim());
            double adjustValue = adjustmentAmountField.getText().trim().isEmpty() ? 0 : Double.parseDouble(adjustmentAmountField.getText().trim());
            
            if (bonusValue < 0 || adjustValue < 0) {
                finalAmountLabel.setForeground(Color.RED);
                finalAmountLabel.setText("Invalid input");
                return;
            }
            
            String adjType = (String) adjustmentTypeCombo.getSelectedItem();
            if ("Decrement".equals(adjType)) {
                adjustValue = -adjustValue;
            } else if ("None".equals(adjType)) {
                adjustValue = 0;
            }
            
            double finalAmount = baseSalary + bonusValue + adjustValue;
            
            if (finalAmount < 0) {
                finalAmountLabel.setForeground(new Color(231, 76, 60));
                finalAmountLabel.setText("Cannot be negative");
            } else {
                finalAmountLabel.setForeground(new Color(41, 128, 185));
                finalAmountLabel.setText("$" + String.format("%,.2f", finalAmount));
            }
        } catch (NumberFormatException e) {
            finalAmountLabel.setForeground(new Color(231, 76, 60));
            finalAmountLabel.setText("Invalid input");
        }
    }
    
    private void confirmPayment() {
        try {
            bonus = bonusField.getText().trim().isEmpty() ? 0 : Double.parseDouble(bonusField.getText().trim());
            double adjustValue = adjustmentAmountField.getText().trim().isEmpty() ? 0 : Double.parseDouble(adjustmentAmountField.getText().trim());
            
            if (bonus < 0 || adjustValue < 0) {
                ElegantMessageDialog.showMessage(this,
                    "Bonus and adjustment amounts cannot be negative.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String adjType = (String) adjustmentTypeCombo.getSelectedItem();
            if ("Decrement".equals(adjType)) {
                adjustment = -adjustValue;
            } else if ("Increment".equals(adjType)) {
                adjustment = adjustValue;
            } else {
                adjustment = 0;
            }
            
            double finalAmount = baseSalary + bonus + adjustment;
            
            if (finalAmount < 0) {
                ElegantMessageDialog.showMessage(this,
                    "Final amount cannot be negative.\nPlease adjust your values.",
                    "Invalid Amount",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            confirmed = true;
            dispose();
            
        } catch (NumberFormatException e) {
            ElegantMessageDialog.showMessage(this,
                "Please enter valid numeric values.",
                "Invalid Input",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public double getBonus() {
        return bonus;
    }
    
    public double getAdjustment() {
        return adjustment;
    }
}
