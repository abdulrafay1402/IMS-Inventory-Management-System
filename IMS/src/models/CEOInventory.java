package models;

import java.util.Date;

// ============= CEO INVENTORY MODEL =============
public class CEOInventory {
    private int id;
    private String productName;
    private double buyingPrice;
    private int totalQuantity;
    private int minStockLevel;
    private Date createdDate;

    public CEOInventory(int id, String productName, double buyingPrice, int totalQuantity,
                        int minStockLevel, Date createdDate) {
        this.id = id;
        this.productName = productName;
        this.buyingPrice = buyingPrice;
        this.totalQuantity = totalQuantity;
        this.minStockLevel = minStockLevel;
        this.createdDate = createdDate;
    }

    // Getters
    public int getId() { return id; }
    public String getProductName() { return productName; }
    public double getBuyingPrice() { return buyingPrice; }
    public int getTotalQuantity() { return totalQuantity; }
    public int getMinStockLevel() { return minStockLevel; }
    public Date getCreatedDate() { return createdDate; }

    // Check if product is low stock
    public boolean isLowStock() {
        return totalQuantity <= minStockLevel;
    }

    // Get stock status
    public String getStockStatus() {
        if (totalQuantity == 0) return "OUT OF STOCK";
        if (totalQuantity <= minStockLevel) return "LOW STOCK";
        return "IN STOCK";
    }
}

