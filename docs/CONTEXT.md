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
* **Authentication:** Menggunakan JWT (JSON Web Token) yang bersifat stateless. Token memiliki batas waktu kadaluarsa (expiry) hingga akhir hari (End of Day - 23:59:59).
* **RBAC:** Akses endpoint dilindungi menggunakan Spring Security berbasis role (contoh: endpoint pendaftaran user hanya bisa diakses oleh ADMIN atau SUPERVISOR). Cashier hanya memiliki izin untuk penjualan.
* **Void Policy:** Pembatalan transaksi (Void) wajib memerlukan otorisasi Manager (PIN/QR) dan alasan pembatalan (`reason_code`).

### D. User Management Policy
* **Privileged Actions:** Register user baru, Deaktivasi user, dan Reset password adalah aksi sensitif yang memerlukan permission khusus (`USER_MANAGEMENT`).
* **Reset Password Flow:** * Self-service: User bisa mengubah password sendiri (wajib input password lama).
    * Administrative Reset: Jika user lupa password, Manager dengan permission `RESET_PASSWORD` harus memberikan otorisasi (PIN/Token) untuk menyetel ulang password user tersebut.
* **Deactivation:** User tidak dihapus dari database (Soft Delete), melainkan diubah status `is_active`-nya menjadi `false` untuk menjaga integritas data transaksi masa lalu.

## 4. Logging & Observability Strategy

### A. Standardized Structured Logging
* **Format:** Semua log wajib menggunakan format **JSON** agar mudah diurai oleh log manajemen (seperti Loki atau ELK).
* **Traceability:** Setiap request harus memiliki **Trace ID** (Correlation ID) yang konsisten dari Controller, Usecase, hingga Repository. Gunakan `Micrometer Tracing` atau `Slf4j MDC`.

### B. Categorization of Logs
1. **Operational Logs:** Mencatat kesehatan aplikasi (Startup, Database connection, Error/Exception).
2. **Audit Logs (Crucial):** Mencatat aksi perubahan data sensitif oleh user. 
   * *Format:* `[Timestamp] [User_ID] [Action] [Resource_ID] [Old_Value] [New_Value]`.
   * *Wajib untuk:* Void transaksi, Perubahan harga, Reset password, dan Adjustment stok.
3. **Request/Response Logs:** Mencatat metadata HTTP request (Endpoint, Status Code, Latency).

### C. Log Security & Privacy
* **Data Masking:** Informasi sensitif seperti `password`, `pin`, dan `customer_phone` **haram** tercatat secara eksplisit di log. Gunakan masking (Contoh: `******`).
* **Retention:** Operational logs disimpan selama 30 hari, sementara Audit Logs disimpan minimal 1 tahun untuk kebutuhan audit finansial.

### D. Tech Stack Requirement (Backend)
* **Framework:** Logback (default Spring Boot) dengan konfigurasi `logback-spring.xml`.
* **Standard Fields:** Setiap log entry wajib menyertakan: `timestamp`, `level`, `trace_id`, `user_id` (jika ada), `module`, dan `message`.

## 5. Detailed Stock Management Logic

### A. Inbound & Batching
* **Batch Creation:** Setiap transaksi masuk (Stock-In) harus menghasilkan entry baru di `stock_batches`.
* **UoM Conversion:** Jika input dalam satuan besar (Contoh: Dus), sistem wajib mengonversi ke unit terkecil sebelum disimpan ke `qty` di `stock_batches`.
* **Cost Tracking:** `cost_price` dicatat per batch untuk mendukung perhitungan margin keuntungan yang presisi per item terjual.

### B. Stock Opname & Adjustment
* **Blind Count System:** Aplikasi mobile/frontend tidak menampilkan qty sistem saat staf melakukan input fisik.
* **Adjustment Policy:** Setiap selisih (Variance) harus melalui persetujuan Manager. Setelah disetujui, sistem membuat log `ADJUSTMENT` di `stock_logs` dan memperbarui `qty` di `stock_batches` terkait.

### C. Expiry & Markdown Management
* **Smart Audit:** Sistem melacak `expiry_date` pada level batch.
* **Auto-Discount Trigger:** Batch yang mendekati kadaluarsa (misal: H-30) dapat ditandai statusnya menjadi `EXPIRING_SOON`. 
* **Pricing Logic:** Saat kasir scan barang, Promotion Engine mengecek status batch. Jika `EXPIRING_SOON`, diskon cuci gudang diaplikasikan secara otomatis.

### D. Audit Trail (The Log Rule)
Setiap perubahan stok harus memiliki entri di `stock_logs` dengan format:
`[Timestamp] [Product_ID] [Batch_ID] [Type] [Qty_Change] [Balance_After] [Reference_ID]`
*Type: SALE, INBOUND, VOID, ADJUSTMENT, RETURN.*