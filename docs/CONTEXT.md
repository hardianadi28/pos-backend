# CONTEXT.md - Product Specification & Business Logic

## 1. Product Vision: Modern Retail POS
Aplikasi Point of Sales khusus retail yang mengutamakan kecepatan transaksi (**3-Tap Rule**), akurasi stok tingkat tinggi, dan fleksibilitas promo tanpa mengorbankan margin keuntungan.

## 2. Core Architectural Principles (Clean Architecture)
* **Offline-First:** Kasir harus tetap bisa bertransaksi tanpa internet. Sinkronisasi dilakukan di background menggunakan *Outbox Pattern*.
* **Layered Architecture:** Pemisahan tegas antara **Domain** (Business Entities), **Usecase** (Business Logic), dan **Infrastructure** (Spring Boot Adapters).
* **Modular Monolith:** Kode dipisahkan secara modular (Inventory, Sales, Promo, Auth).
* **Atomic Transactions:** Pengurangan stok dan pencatatan order harus dilakukan dalam satu transaksi database (ACID compliance).

## 3. Key Business Rules (Source of Truth)

### A. Inventory & Stock Logic
* **Batch Management:** Stok dikelola per batch kedatangan. Setiap batch memiliki `cost_price` (HPP) dan `expiry_date` sendiri.
* **Auto-Splitting Logic:** Saat checkout, sistem otomatis mengambil stok dari batch tersedia menggunakan metode **FEFO** (First Expired, First Out) atau **FIFO**. Jika satu batch tidak cukup, sistem memecah item tersebut ke batch berikutnya secara otomatis di database.
* **Audit Trail:** Setiap mutasi stok (Sale, Void, Inbound) wajib dicatat di `stock_logs` dengan referensi ID yang jelas.

### B. Promotion Engine
* **Rule-Based:** Mendukung diskon kuantitas (Contoh: Beli 3 harga khusus).
* **Stacking Policy:** Promo bersifat *configurable*. Owner bisa mengatur apakah diskon dapat digabung dengan promo lain/diskon member atau tidak.
* **Limitation:** Wajib memiliki fitur *capping* (batas maksimal rupiah) dan limitasi frekuensi penggunaan.

### C. Security & Void
* **RBAC:** Cashier hanya memiliki izin untuk penjualan. Manager/Admin diperlukan untuk fitur sensitif.
* **Void Policy:** Pembatalan transaksi (Void) wajib memerlukan otorisasi Manager (PIN/QR) dan alasan pembatalan (`reason_code`).