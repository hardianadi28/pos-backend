# DATABASE_SCHEMA.md - Technical Data Structure

## 1. General Rules
* **Primary Keys:** Gunakan `UUID` untuk mendukung sinkronisasi offline-first.
* **Soft Deletes:** Gunakan kolom `deleted_at` untuk data master.
* **Naming Convention:** `snake_case`.

## 2. Table Definitions

### User & Security
* `roles`: (id, name, permissions[jsonb])
* `users`: (id, role_id, username, pin_hash, name, is_active)

### Master Data & Inventory
* `categories`: (id, name)
* `products`: (id, category_id, sku, barcode, name, uom, base_price)
* `stock_batches`: (id, product_id, batch_number, qty, cost_price, expiry_date)
* `stock_logs`: (id, product_id, batch_id, type [SALE/IN/VOID/ADJ], qty_change, balance, ref_id)

### Promotion System
* `promotions`: (id, name, type, is_stackable, max_limit, total_quota, start_date, end_date)
* `promo_rules`: (id, promo_id, min_qty, min_amount)
* `promo_rewards`: (id, promo_id, product_id, discount_value, discount_type [%/Fixed])

### Sales & Transactions
* `orders`: (id, order_number, cashier_id, subtotal, total_discount, final_total, status [PAID/VOID])
* `order_items`: (id, order_id, product_id, batch_id, qty, unit_price, cost_price, discount_amount)
* `order_voids`: (id, order_id, manager_id, reason, created_at)