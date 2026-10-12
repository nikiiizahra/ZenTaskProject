# ZenTask

> **Focus. Plan. Achieve with Mindful Ease.**

ZenTask adalah aplikasi Android manajemen tugas (task manager) dengan antarmuka bergaya *pastel glass* yang tenang dan minim distraksi. Aplikasi ini membantu pengguna mengatur tugas harian, menyorot tugas yang mendesak, dan mengubah dokumen fisik menjadi tugas lewat fitur scan.

> **Catatan:** Proyek ini dikerjakan untuk keperluan perkuliahan dan **berfokus pada sisi front end (UI dan navigasi)**. Data masih berupa contoh (dummy) dan belum terhubung ke backend.

---

## Fitur

- **Splash screen beranimasi**: ikon, teks, dan transisi "boom" ungu menuju onboarding.
- **Onboarding 3 langkah**: pengenalan filosofi aplikasi, fitur utama, dan pilihan tampilan awal.
- **Autentikasi**: tampilan login, register, dan verifikasi OTP.
- **Dashboard**: ringkasan tugas dan akses cepat.
- **My Tasks**: daftar tugas dengan filter status (All, In Progress, Not Started, Completed).
- **Detail Task dan Edit Task**: melihat dan mengubah informasi tugas.
- **Vital Tasks**: tugas mendesak (kurang dari 48 jam) disorot otomatis.
- **Task Categories**: pengelompokan tugas per kategori beserta halaman detail kategori.
- **Scan Task**: ambil foto dokumen lewat kamera (CameraX) atau pilih dari galeri, lalu simpan sebagai PDF.
- **Notifikasi**, **Profile**, dan **Help Center**.

### Status Scan Task

Alur yang sudah berjalan: kamera, ambil foto atau pilih dari galeri, lalu konversi menjadi PDF yang tersimpan di penyimpanan internal aplikasi.

Belum diimplementasikan: PDF belum menempel sebagai lampiran pada task, dan deteksi tepi dokumen masih berupa label statis.

---

## Teknologi

| Bagian | Teknologi |
|---|---|
| Bahasa | Kotlin |
| UI | Jetpack Compose, Material 3, Material Icons Extended |
| Navigasi | Navigation Compose |
| State | ViewModel (`ZenTaskViewModel`) |
| Kamera | CameraX |
| Splash | AndroidX Core SplashScreen API |
| Build | Gradle (Kotlin DSL) |
| Min SDK | 24 (Android 7.0) |

---

## Struktur Proyek

```
app/src/main/java/com/example/zentask/
├── MainActivity.kt          # Entry point, ZenTaskTheme, dan NavHost
├── ui/
│   ├── components/          # Komponen bersama (background, nav bar, dialog, dll.)
│   └── screens/             # Seluruh layar aplikasi
│       ├── SplashScreen.kt
│       ├── OnboardingStep1Screen.kt ... OnboardingStep3Screen.kt
│       ├── OnboardingComponents.kt
│       ├── MyTasksScreen.kt
│       ├── TaskDetailScreen.kt / EditTaskScreen.kt
│       ├── ScanTaskScreen.kt / ScanUtils.kt
│       └── ...
└── viewmodel/
    └── ZenTaskViewModel.kt
```

---

## Alur Navigasi

```
splash → onboarding/step1 → step2 → step3 → auth → dashboard
                                                     ├── tasks → task_detail / scan
                                                     ├── vital
                                                     ├── categories
                                                     └── profile
```

---

## Cara Menjalankan

### Prasyarat

- Android Studio versi terbaru
- JDK 17 (bawaan Android Studio sudah cukup)
- Emulator Android (disarankan API 34 atau 35) atau perangkat fisik

### Langkah

1. Clone repositori:
   ```bash
   git clone https://github.com/nikiiizahra/ZenTaskProject.git
   ```
2. Buka folder `ZenTaskProject` di Android Studio (**File > Open**), lalu pilih **Trust Project**.
3. Tunggu Gradle sync selesai.
4. Pilih emulator atau perangkat, lalu klik **Run**.

### Tips

- Fitur **Scan Task** paling baik dicoba di **perangkat fisik**, karena kamera emulator hanya simulasi.
- Izinkan akses kamera saat diminta.
- Jika muncul dialog "App isn't 16 KB compatible" di emulator berbasis 16 KB, itu hanya peringatan dan aplikasi tetap berjalan.

---

## Alur Kerja Git

- `main` berisi versi stabil.
- Setiap fitur dikerjakan di branch sendiri (contoh: `fitur-scan-task`), lalu digabung ke `main` lewat Pull Request.
- Sebelum memulai pekerjaan baru: `git checkout main` lalu `git pull`.

---

## Tim

| Nama | Peran |
|---|---|
| Niki Zahra | Perancangan Ide, UI/UX Design, pembuatan dashboard, profile, vital task |
| Alyaa | UI navigasi, detail task, edit task |
| Aniza Helwa | Splash screen, onboarding, scan task, penyusunan README, dan penggabungan branch |
| Rosyida Dimitri | Penyusunan Laporan |

---

## Screenshot


<img width="717" height="1600" alt="splash zen" src="https://github.com/user-attachments/assets/e5291cea-4110-4fb0-8ba9-977f15a380ba" /> 

<img width="717" height="1600" alt="onboarding zentask png" src="https://github.com/user-attachments/assets/41fdfa0b-2bac-46b5-8b13-0b9569546bbc" /> 

<img width="540" height="1491" alt="mytask zen" src="https://github.com/user-attachments/assets/9b4536f8-b67a-41b9-9ac3-d4f495b7fd5f" /> 

<img width="717" height="1600" alt="scantask zen" src="https://github.com/user-attachments/assets/a87ae227-d83a-4f71-be6c-e55693cb2f65" /> 

---

## Rencana Pengembangan

- [ ] Lampirkan PDF hasil scan ke task dan tampilkan di detail task
- [ ] Deteksi tepi dokumen otomatis saat scan
- [ ] Daftar task dinamis dari ViewModel (menggantikan kartu statis)
- [ ] Penyimpanan data lokal (Room) dan sinkronisasi cloud
- [ ] Fitur kolaborasi lewat kode undangan 6 digit

---

## Lisensi

Proyek ini dibuat untuk keperluan akademik. 
