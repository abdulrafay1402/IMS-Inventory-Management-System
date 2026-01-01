-- SQLite Schema for IMS (Inventory Management System)
-- Converted from MySQL schema
-- This script creates all necessary tables with SQLite-compatible syntax

PRAGMA foreign_keys = ON;

-- 1. Users Table (Base table for all users)
CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    role TEXT NOT NULL CHECK(role IN ('CEO', 'MANAGER', 'CASHIER')),
    name TEXT NOT NULL,
    phone TEXT,
    cnic TEXT UNIQUE NOT NULL,
    salary DECIMAL(10,2) DEFAULT 0.00,
    status TEXT DEFAULT 'ACTIVE' CHECK(status IN ('ACTIVE', 'PENDING', 'INACTIVE')),
    joining_date DATE,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_updated DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_cnic ON users(cnic);
CREATE INDEX IF NOT EXISTS idx_role ON users(role);

-- 2. CEO-Manager Relationship
CREATE TABLE IF NOT EXISTS manager_ceo (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    ceo_id INTEGER NOT NULL,
    manager_id INTEGER NOT NULL,
    assigned_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ceo_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (manager_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_manager_ceo_ceo_id ON manager_ceo(ceo_id);
CREATE INDEX IF NOT EXISTS idx_manager_ceo_manager_id ON manager_ceo(manager_id);

-- 3. Manager-Cashier Relationship
CREATE TABLE IF NOT EXISTS cashier_manager (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    manager_id INTEGER NOT NULL,
    cashier_id INTEGER NOT NULL,
    join_date DATE DEFAULT CURRENT_DATE,
    status TEXT DEFAULT 'PENDING_APPROVAL' CHECK(status IN ('ACTIVE', 'PENDING_APPROVAL', 'REJECTED')),
    rejection_reason TEXT,
    request_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    approval_date DATETIME,
    FOREIGN KEY (manager_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (cashier_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_cashier_manager_manager_id ON cashier_manager(manager_id);
CREATE INDEX IF NOT EXISTS idx_cashier_manager_cashier_id ON cashier_manager(cashier_id);
CREATE INDEX IF NOT EXISTS idx_cashier_manager_status ON cashier_manager(status);

-- 4. CEO's Master Inventory
CREATE TABLE IF NOT EXISTS ceo_inventory (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    product_name TEXT NOT NULL,
    buying_price DECIMAL(10,2) NOT NULL,
    total_quantity INTEGER NOT NULL DEFAULT 0,
    min_stock_level INTEGER DEFAULT 10,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_updated DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_ceo_inventory_product_name ON ceo_inventory(product_name);

-- 5. Manager's Local Inventory
CREATE TABLE IF NOT EXISTS manager_inventory (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    manager_id INTEGER NOT NULL,
    ceo_inventory_id INTEGER NOT NULL,
    selling_price DECIMAL(10,2) NOT NULL,
    current_quantity INTEGER NOT NULL DEFAULT 0,
    last_updated DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (manager_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (ceo_inventory_id) REFERENCES ceo_inventory(id) ON DELETE CASCADE,
    UNIQUE(manager_id, ceo_inventory_id)
);

CREATE INDEX IF NOT EXISTS idx_manager_inventory_manager_id ON manager_inventory(manager_id);

-- 6. Bills
CREATE TABLE IF NOT EXISTS bills (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    bill_number TEXT UNIQUE NOT NULL,
    cashier_id INTEGER NOT NULL,
    manager_id INTEGER NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    bill_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    status TEXT DEFAULT 'COMPLETED' CHECK(status IN ('COMPLETED', 'CANCELLED')),
    FOREIGN KEY (cashier_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (manager_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_bills_cashier_id ON bills(cashier_id);
CREATE INDEX IF NOT EXISTS idx_bills_manager_id ON bills(manager_id);
CREATE INDEX IF NOT EXISTS idx_bills_bill_date ON bills(bill_date);

-- 7. Bill Items
CREATE TABLE IF NOT EXISTS bill_items (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    bill_id INTEGER NOT NULL,
    manager_inventory_id INTEGER NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (bill_id) REFERENCES bills(id) ON DELETE CASCADE,
    FOREIGN KEY (manager_inventory_id) REFERENCES manager_inventory(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_bill_items_bill_id ON bill_items(bill_id);

-- 8. Expenses
CREATE TABLE IF NOT EXISTS expenses (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    manager_id INTEGER NOT NULL,
    description TEXT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    category TEXT NOT NULL CHECK(category IN ('UTILITIES', 'SALARIES', 'RENT', 'MAINTENANCE', 'OTHER')),
    expense_date DATE DEFAULT CURRENT_DATE,
    recorded_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (manager_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_expenses_manager_id ON expenses(manager_id);
CREATE INDEX IF NOT EXISTS idx_expenses_expense_date ON expenses(expense_date);

-- 9. Stock Transfer table (for tracking stock movements)
CREATE TABLE IF NOT EXISTS stock_transfer (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    from_manager_id INTEGER,
    to_manager_id INTEGER,
    product_name TEXT NOT NULL,
    quantity INTEGER NOT NULL,
    transfer_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    status TEXT DEFAULT 'COMPLETED' CHECK(status IN ('PENDING', 'COMPLETED', 'REJECTED')),
    FOREIGN KEY (from_manager_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (to_manager_id) REFERENCES users(id) ON DELETE SET NULL
);

-- 10. Notifications Table
CREATE TABLE IF NOT EXISTS notifications (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    type TEXT NOT NULL CHECK(type IN ('CASHIER_REQUEST', 'CASHIER_APPROVED', 'CASHIER_REJECTED', 'BILL_CREATED', 'MONTHLY_REPORT', 'STOCK_LOW', 'GENERAL')),
    title TEXT NOT NULL,
    message TEXT NOT NULL,
    related_id TEXT,
    is_read INTEGER DEFAULT 0 CHECK(is_read IN (0, 1)),
    created_at DATETIME DEFAULT (datetime('now', 'localtime')),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_notifications_user_id ON notifications(user_id);
CREATE INDEX IF NOT EXISTS idx_notifications_is_read ON notifications(is_read);
CREATE INDEX IF NOT EXISTS idx_notifications_created_at ON notifications(created_at);

-- 11. Salary Payments Table
CREATE TABLE IF NOT EXISTS salary_payments (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_month TEXT NOT NULL, -- Format: 'YYYY-MM' (e.g., '2026-01')
    payment_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    status TEXT DEFAULT 'PAID' CHECK(status IN ('PAID', 'PENDING', 'CANCELLED')),
    notes TEXT,
    created_by INTEGER, -- Manager who processed the payment
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    UNIQUE(user_id, payment_month) -- Prevent duplicate payments for same month
);

CREATE INDEX IF NOT EXISTS idx_salary_payments_user_id ON salary_payments(user_id);
CREATE INDEX IF NOT EXISTS idx_salary_payments_month ON salary_payments(payment_month);
CREATE INDEX IF NOT EXISTS idx_salary_payments_status ON salary_payments(status);

-- 12. Insert default CEO user (password: ceo123)
INSERT OR IGNORE INTO users (username, password, role, name, phone, cnic, salary, status)
VALUES ('ceo', 'ceo123', 'CEO', 'System Administrator', '03001234567', '1234567890123', 0.00, 'ACTIVE');

-- 13. Sample data for testing
INSERT OR IGNORE INTO ceo_inventory (product_name, buying_price, total_quantity, min_stock_level)
VALUES 
    ('Laptop HP 15', 45000.00, 50, 10),
    ('Mouse Logitech', 500.00, 200, 20),
    ('Keyboard Mechanical', 3000.00, 100, 15),
    ('Monitor Dell 24"', 25000.00, 30, 5),
    ('USB Cable', 150.00, 500, 50);
