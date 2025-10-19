package com.example.aplikasi_rumah_sakit_rawat_jalan.model

data class User(
    var id: String = "",
    var uid: String = "",  // ← Tambah ini
    var nama: String = "",
    var email: String = "",
    var role: String = "pasien",
    var noTelp: String = "",
    var noTelepon: String = "",  // ← Tambah ini (alias dari noTelp)
    var spesialis: String = "",  // ← Tambah ini (untuk dokter)
    var tanggalLahir: String = "",
    var alamat: String = ""
)