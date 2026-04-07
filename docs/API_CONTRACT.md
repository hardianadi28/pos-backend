# API_CONTRACT.md - Common Endpoints (v1)

## Base URL: `/api/v1`
**Header:** `Authorization: Bearer <JWT>`, `X-Idempotency-Key: <UUID>`

### 1. Authentication
* `POST /auth/login`: Login kasir menggunakan PIN atau Username.

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
* `POST /users/register**`
    * **Permission:** `USER_CREATE`
    * **Description:** Mendaftarkan user/kasir baru ke dalam sistem.
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