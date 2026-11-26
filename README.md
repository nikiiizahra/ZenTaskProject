
# ZenTask

> **Focus. Plan. Achieve with Mindful Ease.**

ZenTask adalah aplikasi Android manajemen tugas (task manager) dengan antarmuka bergaya *pastel glass* yang tenang dan minim distraksi. Aplikasi ini membantu pengguna mengelola tugas harian, menentukan prioritas berdasarkan tingkat urgensi, memantau produktivitas, serta mengubah dokumen menjadi tugas melalui fitur Scan Task.

> **Catatan:** Proyek ini dikembangkan untuk keperluan Praktikum Pengembangan Aplikasi Perangkat Bergerak (PAPB) TIF-D 2026. ZenTask telah dikembangkan dari aplikasi berbasis UI dan navigasi menjadi aplikasi dengan pengelolaan data lokal menggunakan Room Database, pemrosesan data tugas, dan fitur kolaborasi lokal. Aplikasi belum menggunakan backend untuk sinkronisasi antarperangkat.

---

## Fitur

- **Splash screen beranimasi**: ikon, teks, dan transisi ungu menuju onboarding.
- **Onboarding 3 langkah**: pengenalan filosofi aplikasi dan fitur utama.
- **Autentikasi**: register, login, logout, dan pengelolaan sesi akun lokal.
- **Dashboard**: ringkasan tugas, progres penyelesaian, dan akses cepat ke fitur utama.
- **My Tasks**: daftar tugas dengan filter status (All, In Progress, Not Started, Completed).
- **Task Management**: membuat, melihat, mengedit, menghapus, dan memperbarui status tugas.
- **Smart Task Priority & Urgency Scoring**: menghitung tingkat urgensi berdasarkan deadline, prioritas, dan progres pengerjaan.
- **Vital Tasks**: mengidentifikasi tugas dengan urgensi High/Critical atau deadline maksimal 48 jam.
- **Task Categories**: pengelompokan tugas berdasarkan kategori beserta halaman detail kategori.
- **Dashboard & Analytics**: statistik tugas, completion rate, dan ringkasan produktivitas.
- **Scan Task**: mengambil gambar dari kamera atau galeri dan mengonversinya menjadi PDF.
- **Scan to Task & PDF Attachment**: membuat tugas dari hasil pemindaian dan menghubungkan PDF sebagai lampiran.
- **Local Collaboration**: berbagi akses tugas melalui kode undangan antarakun pada perangkat yang sama.
- **Room Chat**: ruang diskusi bagi anggota yang tergabung dalam tugas kolaborasi.
- **Collaboration Progress**: memantau perkembangan pengerjaan bagian tugas anggota.
- **Subtask Management**: membagi tugas utama menjadi pekerjaan kecil dan mengelola status penyelesaiannya.
- **Streak Reminder**: pengingat aktivitas yang dijadwalkan menggunakan WorkManager.
- **Notifikasi, Profile, Settings, dan Help Center**: informasi aktivitas, pengaturan akun, dan panduan penggunaan aplikasi.

### Empat Fungsi Utama (Problem Solving)

Pengembangan ZenTask berfokus pada empat fungsi utama yang melibatkan pemrosesan data, bukan hanya operasi CRUD.

**1. Smart Task Priority & Urgency Scoring**

Menghitung urgency score berdasarkan kedekatan deadline, tingkat prioritas, dan progres pengerjaan. Hasil perhitungan diklasifikasikan menjadi Low, Medium, High, atau Critical.

**2. Automatic Vital Task Detection & Reminder**

Mengidentifikasi tugas aktif yang memiliki tingkat urgensi High/Critical atau deadline maksimal 48 jam. ZenTask juga menyediakan mekanisme Streak Reminder untuk mendukung konsistensi aktivitas.

**3. Smart Productivity Summary**

Menghitung jumlah tugas berdasarkan status Not Started, In Progress, dan Completed, serta menghasilkan completion rate untuk ditampilkan pada Dashboard dan Analytics.

**4. Scan Task Document Processing**

Memproses gambar dari kamera atau galeri menggunakan informasi orientasi Exif, kemudian mengonversinya menjadi PDF. Dokumen hasil scan dapat digunakan untuk membuat tugas beserta lampirannya.

### Status Scan Task

Alur yang telah dikembangkan:
- Mengambil gambar melalui kamera atau memilih dari galeri.
- Membaca orientasi gambar menggunakan Exif.
- Menyesuaikan orientasi dan ukuran gambar.
- Mengonversi gambar menjadi PDF.
- Menyimpan PDF pada penyimpanan internal aplikasi.
- Membuat tugas dari hasil scan.
- Menghubungkan dokumen PDF sebagai lampiran tugas.

Deteksi tepi dokumen secara otomatis masih menjadi bagian dari pengembangan selanjutnya.

### Status Collaboration

Collaboration menggunakan penyimpanan lokal melalui Room Database.

Fitur yang telah dikembangkan:
- Membuat kode undangan kolaborasi.
- Menghubungkan akun lain ke tugas bersama.
- Menyediakan Room Chat untuk diskusi.
- Memantau Collaboration Progress.
- Mengelola Subtask pada pekerjaan.

**Batasan:** Kolaborasi masih bersifat lokal pada perangkat yang sama dan belum mendukung sinkronisasi real-time antarperangkat.

---

## Teknologi

| Bagian | Teknologi |
|---|---|
| Bahasa | Kotlin |
| UI | Jetpack Compose, Material 3, Material Icons Extended |
| Navigasi | Navigation Compose |
| State | ViewModel, StateFlow |
| Database | Room Database (SQLite) |
| Asynchronous | Kotlin Coroutines & Flow |
| Kamera | CameraX |
| Dokumen | ExifInterface, PdfDocument |
| Background Task | WorkManager |
| Splash | AndroidX Core SplashScreen API |
| Build | Gradle (Kotlin DSL) |
| Version Control | Git & GitHub |
| Min SDK | 24 (Android 7.0) |

### Database

ZenTask menggunakan Room Database versi 5 dengan tujuh Entity utama:

1. `UserEntity`
2. `TaskEntity`
3. `CategoryEntity`
4. `CollabInviteEntity`
5. `CollabMemberEntity`
6. `TaskMessageEntity`
7. `SubtaskEntity`

Data disimpan secara lokal untuk mendukung pengelolaan akun, tugas, kategori, kolaborasi, pesan, dan subtask.

---

## Struktur Proyek

```text
app/src/main/java/com/example/zentask/
├── MainActivity.kt
├── data/
│   └── local/
│       ├── ZenTaskDatabase.kt
│       ├── CollabDao.kt
│       ├── CollabEntitas.kt
│       └── CollabRepository.kt
├── model/
│   └── TaskModels.kt
├── ui/
│   ├── components/
│   └── screens/
│       ├── SplashScreen.kt
│       ├── MyTasksScreen.kt
│       ├── TaskDetailScreen.kt
│       ├── EditTaskScreen.kt
│       ├── VitalTasksScreen.kt
│       ├── ScanTaskScreen.kt
│       ├── ScanUtils.kt
│       ├── CollaborationCard.kt
│       ├── SubtaskCard.kt
│       └── ...
└── viewmodel/
    └── ZenTaskViewModel.kt
```

Secara umum, alur pengelolaan data menggunakan:

`Jetpack Compose UI ↔ ViewModel ↔ Repository/DAO ↔ Room Database`

---

## Alur Navigasi

```text
Splash Screen
    ↓
Onboarding
    ↓
Authentication (Login/Register)
    ↓
Dashboard
    ├── My Tasks
    │   ├── Create Task
    │   ├── Task Detail
    │   │   ├── Edit Task
    │   │   ├── Subtask
    │   │   └── Collaboration
    │   │       ├── Room Chat
    │   │       └── Collaboration Progress
    │   └── Scan Task
    │       ├── Camera / Gallery
    │       ├── PDF
    │       └── Create Task from Scan
    ├── Vital Tasks
    ├── Categories
    ├── Analytics
    ├── Notifications
    └── Profile
        ├── Settings
        ├── Help Center
        └── Logout
```

Diagram di atas merupakan ringkasan alur fitur, bukan nama route internal Navigation Compose.

---

## Cara Menjalankan

### Prasyarat

- Android Studio dengan dukungan Kotlin dan Jetpack Compose.
- JDK yang kompatibel dengan konfigurasi Gradle proyek.
- Emulator Android atau perangkat fisik.

### Langkah

1. Clone repositori:

   ```bash
   git clone https://github.com/nikiiizahra/ZenTaskProject.git
   ```

2. Buka folder `ZenTaskProject` di Android Studio melalui **File > Open**.
3. Tunggu proses Gradle Sync selesai.
4. Pilih emulator atau perangkat Android.
5. Klik **Run** untuk menjalankan aplikasi.

### Build melalui Terminal

Pada Windows PowerShell:

```powershell
.\gradlew.bat :app:assembleDebug
```

### Tips

- Fitur **Scan Task** lebih baik diuji pada perangkat fisik karena kamera emulator menggunakan simulasi.
- Berikan izin kamera saat diminta.
- Untuk menguji Collaboration lokal, gunakan dua akun yang tersimpan pada perangkat yang sama.
- Jangan menghapus data aplikasi ketika sedang menguji migrasi Room Database, karena data lokal dapat hilang.

---

## Alur Kerja Git

- `main` berisi hasil integrasi fitur kelompok.
- Setiap anggota mengembangkan fitur melalui branch masing-masing.
- Perubahan digabungkan ke `main` menggunakan Pull Request.
- Setelah integrasi, aplikasi diperiksa melalui proses build dan pengujian fitur.

Sebelum memulai pekerjaan baru:

```bash
git checkout main
git pull origin main
```

---

## Tim

**Kelompok 3 – Praktikum PAPB TIF-D 2026**

| Nama | NIM | Fungsi Utama |
|---|---|---|
| Alyaa Rahma Nabiilah | 245150207111029 | Smart Task Priority & Urgency Scoring |
| Rosyida Dimitri | 245150201111014 | Automatic Vital Task Detection & Reminder |
| Niquita Aislam Az Zahara | 245150207111057 | Smart Productivity Summary |
| Aniza Helwa Mahanani | 245150201111043 | Scan Task Document Processing |

Selain pembagian fungsi utama tersebut, anggota kelompok juga berkontribusi dalam pengembangan UI/UX, navigasi, pengelolaan data, integrasi fitur, pengujian, dan dokumentasi proyek.

---

## Screenshot

### Splash Screen

<img width="717" height="1600" alt="splash zen" src="https://github.com/user-attachments/assets/e5291cea-4110-4fb0-8ba9-977f15a380ba" />

### Onboarding

<img width="717" height="1600" alt="onboarding zentask png" src="https://github.com/user-attachments/assets/41fdfa0b-2bac-46b5-8b13-0b9569546bbc" />

### My Tasks

<img width="540" height="1491" alt="mytask zen" src="https://github.com/user-attachments/assets/9b4536f8-b67a-41b9-9ac3-d4f495b7fd5f" />

### Scan Task

<img width="717" height="1600" alt="scantask zen" src="https://github.com/user-attachments/assets/a87ae227-d83a-4f71-be6c-e55693cb2f65" />

---

## Rencana Pengembangan

- [x] Daftar task dinamis dari ViewModel.
- [x] Penyimpanan data lokal menggunakan Room Database.
- [x] Fitur kolaborasi melalui kode undangan.
- [x] Lampiran PDF hasil scan pada tugas.
- [x] Room Chat dan Collaboration Progress lokal.
- [x] Subtask Management.
- [ ] Deteksi tepi dokumen otomatis saat scan.
- [ ] Sinkronisasi data melalui cloud.
- [ ] Collaboration dan Room Chat real-time antarperangkat.
- [ ] Notifikasi deadline yang lebih terjadwal.
- [ ] OCR untuk membaca informasi tugas dari dokumen.

---

## Lisensi

Proyek ini dibuat untuk keperluan akademik dalam Praktikum Pengembangan Aplikasi Perangkat Bergerak (PAPB), Fakultas Ilmu Komputer, Universitas Brawijaya, 2026.
