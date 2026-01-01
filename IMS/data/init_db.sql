-- SQLite initialization script for IMS
-- Converted from MySQL TRUNCATE/DELETE statements to SQLite-compatible commands.
-- This will clear most tables (keeping the CEO user) and reset AUTOINCREMENT sequences.

PRAGMA foreign_keys = OFF;
BEGIN TRANSACTION;

-- Remove all data from tables
DELETE FROM bill_items;
DELETE FROM bills;
DELETE FROM expenses;
DELETE FROM manager_inventory;
DELETE FROM cashier_manager;
DELETE FROM manager_ceo;
DELETE FROM ceo_inventory;

-- Keep the CEO account only
DELETE FROM users WHERE role != 'CEO';

-- Reset sqlite autoincrement counters for these tables (if they use AUTOINCREMENT)
DELETE FROM sqlite_sequence WHERE name IN (
  'bill_items', 'bills', 'expenses', 'manager_inventory', 'cashier_manager', 'manager_ceo', 'ceo_inventory'
);

-- Optional: ensure CEO user has known credentials (update only the CEO user)
UPDATE users SET 
  username = 'ceo',
  password = 'ceo123',
  name = 'Chief Executive Officer',
  phone = '03001234567',
  cnic = '1234567890123',
  status = 'ACTIVE'
WHERE role = 'CEO';

-- If there is no CEO user present, insert a default one (safe for fresh DBs)
INSERT INTO users (username, password, role, name, phone, cnic, status)
SELECT 'ceo', 'ceo123', 'CEO', 'Chief Executive Officer', '03001234567', '1234567890123', 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE role = 'CEO');

COMMIT;
PRAGMA foreign_keys = ON;
