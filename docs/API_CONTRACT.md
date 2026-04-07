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