package models;

// ============= BILL ITEM DETAIL MODEL (for viewing bills) =============
public class BillItemDetail {
    private int id;
    private int billId;
    private String productName;
    private int quantity;
    private double unitPrice;
    private double subtotal;

    public BillItemDetail(int id, int billId, String productName, int quantity,
                          double unitPrice, double subtotal) {
        this.id = id;
        this.billId = billId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = subtotal;
    }

    public int getId() {
        return id;
    }

    public int getBillId() {
        return billId;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public double getSubtotal() {
        return subtotal;
    }
}

