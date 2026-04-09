# API_CONTRACT.md - Common Endpoints (v1)

## Base URL: `/api/v1`
**Header:** `Authorization: Bearer <JWT>`, `X-Idempotency-Key: <UUID>`

### 1. Authentication
* `POST /auth/login`: Login menggunakan username dan password. Mengembalikan JWT Token dan waktu kedaluwarsa (hingga akhir hari). Bersifat publik (Permit All) tanpa perlu header Authorization.

### 2. Synchronization (Offline Support)
* `GET /sync/products?last_sync=<timestamp>`: Mengambil perubahan data master (Differential Sync).

### 3. Inventory Operations
* `POST /inventory/inbound`: Menambah stok baru (membuat entry `stock_batches`).
* `GET /inventory/low-stock`: Daftar barang yang menyentuh batas minimum stok.

### 4. Sales Transaction
* `POST /orders`:
    * **Logic:** Backend memproses validasi promo dan melakukan **Auto-splitting batch**.
    * **Payload Example:** `{ items: [{product_id, qty}], payment_method }`
* `POST /orders/{id}/void`:
    * **Logic:** Memeriksa otorisasi manager sebelum mengembalikan stok ke batch awal.

### 5. Reporting (Owner Only)
* `GET /reports/dashboard`: Ringkasan omzet, estimasi profit (P&L), dan grafik transaksi harian.
* `GET /reports/void-logs`: Laporan pembatalan transaksi untuk kebutuhan audit.

### 6. User Management (Privileged)
* `POST /users/register`
    * **Permission/Role:** Wajib memiliki role `ADMIN` atau `SUPERVISOR` (menggunakan JWT Auth).
    * **Description:** Mendaftarkan user/kasir baru ke dalam sistem.
    * **Request Payload:**
    ```json
    {
    "username": "azkia_pos",
    "password": "SecurePassword123",
    "name": "Azkia",
    "role_id": "uuid-role-cashier"
    }
* `PATCH /users/{id}/deactivate`
    * **Permission:** `USER_DEACTIVATE`
    * **Description:** Menonaktifkan akses user (is_active = false).
* `PATCH /users/{id}/reset-password`
    * **Permission:** `USER_RESET_PASSWORD`
    * **Header:** `X-Manager-PIN` (Otorisasi Manager)
    * **Payload:** `{ "new_password": "..." }`
    * **Description:** Reset password user lain oleh Manager.

### 7. User Self-Service
* `PATCH /users/me/change-password`
    * **Auth:** Login User
    * **Payload:** `{ "old_password": "...", "new_password": "..." }`
    * **Description:** User mengganti password mereka sendiri.

### 8. Stock Management Module (Detailed)

* `POST /api/v1/inventory/inbound`
    * **Description:** Menambah stok baru dan membuat batch.
    * **Request:**
    ```json
    {
    "product_id": "uuid",
    "supplier_id": "uuid",
    "qty": 50,
    "cost_price": 8500,
    "expiry_date": "2027-01-01",
    "batch_number": "PO-2026-001"
    }

* `POST /api/v1/inventory/stock-opname`
    * **Description:** Input hasil audit fisik (Blind Count).
    * **Request:**
    ```json
    {
    "batch_id": "uuid",
    "physical_qty": 48,
    "reason": "Damaged/Lost",
    "authorized_by_manager_pin": "123456"
    }

* `GET /api/v1/inventory/expiring-soon`
    * **Description:** List batch yang mendekati kadaluarsa dalam X hari.
    * **Response:**
    ```json
    {
        "success": true,
        "data": [
            {
            "batch_id": "b1",
            "product_name": "Susu UHT",
            "expiry_date": "2026-05-01",
            "days_left": 23
            }
        ]
    }
* `GET /api/v1/inventory/logs/{product_id}`
    * **Description:** Melihat sejarah pergerakan stok untuk audit.
    * **Response:**
    ```json
    {
        "success": true,
        "data": [
            { "type": "SALE", "qty_change": -1, "balance": 49, "ref": "INV-001" },
            { "type": "INBOUND", "qty_change": 50, "balance": 50, "ref": "PO-99" }
        ]
    }

# API_CONTRACT.md - Standardized API Documentation

## 1. Global Response Wrapper
Semua API wajib mengembalikan struktur berikut:
* `success`: boolean
* `message`: string (user-friendly message)
* `data`: object/array/null
* `metadata`: object (opsional, untuk pagination)
* `error`: object (null jika success=true)
* `timestamp`: string (ISO 8601)