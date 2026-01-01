package models;

// ============= BILL ITEM MODEL (for creating bills) =============
public class BillItem {
    private int managerInventoryId;
    private int quantity;
    private double unitPrice;
    private double subtotal;

    public BillItem(int managerInventoryId, int quantity, double unitPrice) {
        this.managerInventoryId = managerInventoryId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = quantity * unitPrice;
    }

    public int getManagerInventoryId() { return managerInventoryId; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public double getSubtotal() { return subtotal; }
}

