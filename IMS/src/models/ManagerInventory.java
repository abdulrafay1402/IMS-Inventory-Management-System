package models;

import java.util.Date;

// ============= MANAGER INVENTORY MODEL =============
public class ManagerInventory {
    private int id;
    private int managerId;
    private int ceoInventoryId;
    private String productName;
    private double buyingPrice;
    private double sellingPrice;
    private int currentQuantity;
    private int minStockLevel;
    private Date lastUpdated;

    public ManagerInventory(int id, int managerId, int ceoInventoryId, String productName,
                            double buyingPrice, double sellingPrice, int currentQuantity,
                            int minStockLevel, Date lastUpdated) {
        this.id = id;
        this.managerId = managerId;
        this.ceoInventoryId = ceoInventoryId;
        this.productName = productName;
        this.buyingPrice = buyingPrice;
        this.sellingPrice = sellingPrice;
        this.currentQuantity = currentQuantity;
        this.minStockLevel = minStockLevel;
        this.lastUpdated = lastUpdated;
    }

    // Getters
    public int getId() { return id; }
    public int getManagerId() { return managerId; }
    public int getCeoInventoryId() { return ceoInventoryId; }
    public String getProductName() { return productName; }
    public double getBuyingPrice() { return buyingPrice; }
    public double getSellingPrice() { return sellingPrice; }
    public int getCurrentQuantity() { return currentQuantity; }
    public int getMinStockLevel() { return minStockLevel; }
    public Date getLastUpdated() { return lastUpdated; }

    // Business logic methods
    public boolean isLowStock() {
        return currentQuantity <= minStockLevel;
    }

    public String getStockStatus() {
        if (currentQuantity == 0) return "OUT OF STOCK";
        if (currentQuantity <= minStockLevel) return "LOW STOCK";
        return "IN STOCK";
    }

    public double getProfitMargin() {
        if (buyingPrice == 0) return 0;
        return ((sellingPrice - buyingPrice) / buyingPrice) * 100;
    }

    public double getTotalValue() {
        return currentQuantity * sellingPrice;
    }

    public boolean canSell(int quantity) {
        return currentQuantity >= quantity;
    }
}

