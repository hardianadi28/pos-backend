# Epic: Modern Retail POS Backend (MVP) Initialization

## 🎯 Visi & Tujuan Utama
Membangun backend untuk aplikasi Modern Retail POS (MVP) yang difokuskan pada target Toko Retail. Backend bertugas sebagai *Server Database* yang berinteraksi dengan API dari Frontend/Local Offline DB yang menyinkronkan data transaksional sekaligus menjaga keamanan, integrity, dan konsistensi stok barang.

## 🏗 Arsitektur & Teknologi
- **Bahasa & Framework**: Java 17 + Spring Boot 3.x
- **Database**: PostgreSQL 16+
- **Architecture Pattern**: Clean Architecture (Hexagonal) yang membagi logika ke dalam 4 layer utama secara _strict_ (Domain, Usecase, Adapter, Infrastructure).
- **Project Structure**: Modular Monolith. Struktur codebase ditekankan pada pemisahan logika yang tegas menggunakan package atau folder berdasarkan modul/domain bisnis.

## 📦 Struktur Direktori Modular Monolith (High-Level Plan)

Sistem akan dipecah menjadi modul-modul (bounded contexts) secara logis di dalam satu codebase. Nantinya, 4 layer arsitektur _Clean Architecture_ akan ditempatkan **di dalam** masing-masing modul.

Struktur utama level direktori yang diusulkan:

```text
src/main/java/com/retail/pos/
├── core/                   # Kumpulan cross-cutting concerns: utils, exceptions, base classes
├── modules/
│   ├── auth/               # Modul Autentikasi, Otorisasi (RBAC), dan Manajemen User
│   ├── inventory/          # Modul Barang, Stok, Batch, dan Mutasi (Stock Logs)
│   ├── sales/              # Modul POS Engine, Order, Transaksi Penjualan, dan Void Logic
│   └── promo/              # Modul Promotion Engine, Capping, dan Configuration
└── PosApplication.java     # Entry point dari Spring Boot (Main class)
```

*(Catatan: Implementasi detail isi dari masing-masing folder modul (Domain, Usecase, Adapter, Infrastructure) akan didefinisikan pada sub-task/issue yang terpisah di kemudian hari).*

## 📋 Detail Tiap Modul Bisnis

### 1. Modul Auth (`modules/auth/`)
Menangani tanggung jawab Security, Access Control (RBAC), dan Audit otorisasi.
- Manajemen User: Pendaftaran user, session/login token.
- Role-Based Access Control (RBAC): Pemisahan Hak Akses aplikasi untuk Kasir, Manager, dan Owner.
- Void Authorization: Menangani request validasi PIN/Approval khusus yang dibutuhkan oleh aksi sensitif (Contoh: Menghapus item keranjang, membatalkan transaksi).

### 2. Modul Inventory (`modules/inventory/`)
Pusat kebenaran (Source of Truth) mengenai data produk dan pergerakan stok.
- Katalog Produk (Master Data): CRUD `products` & SKU.
- Batch Management: Pencatatan detil jumlah stok dari `stock_batches` dengan atribut waktu masuk, HPP (Cost Price), dan tanggal kadaluarsa (Expiry).
- Stock Logs: Menyimpan status *immutable* untuk Audit Trail riwayat inventori demi melacak (Sales, Inbound, Adjustment).

### 3. Modul Sales (`modules/sales/`)
Fokus memproses "POS Engine" (Daftar Penjualan dan Checkout).
- Order Management: Membuat `orders` dan keranjang item transaksi `order_items`.
- Fitur Auto-Splitting: Layanan (Usecase) yang harus memastikan bahwa pemotongan stok memecah qty ke dalam beberapa `batch_id` yang berbeda (FIFO/FEFO) di dalam sistem bila ada kekurangan kuantitas dalam satu batch (berjalan secara *Atomic*).
- Concurrency Control: Pendekatan isolasi/database locking untuk mencegah potensi *race condition*.

### 4. Modul Promo (`modules/promo/`)
Engine dinamis yang fleksibel terkait insentif harga.
- Konfigurasi Quantity-Based: Syarat aktivasi diskon berdasarkan besaran qty di keranjang (Misal: Beli 3 diskon Rp5.000).
- Stacking Logic Configurable: Menilai apakah multi-promo boleh digabungkan dengan flag khusus.
- Target & Limitation: Mengendalikan aturan Capping potongan diskon.

## 🛠 Panduan Implementasi Selanjutnya
Catatan untuk *Junior Programmer* atau *AI Model*:
1. Issue ini hanya mendefinisikan *high-level strategy* untuk kerangka dasar modul dan arsitektur foldernya.
2. Jangan melakukan *over-engineering*. Struktur layer domain dan persistensi baru akan dikerjakan per-modul (dimulai dari `auth` atau master data `inventory` terlebih dahulu) secara inkremental pada sprint mendatang.
3. Fokus sentral rancangan ke depan adalah relasi `order_items` dengan `batch_id` serta penguncian transaksi database untuk menjamin tingkat presisi akurasi inventaris tetap mutlak.
