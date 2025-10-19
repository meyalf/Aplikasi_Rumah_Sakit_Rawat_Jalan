package com.example.aplikasi_rumah_sakit_rawat_jalan

data class Pasien(
    val id: String = "",
    val nama: String = "",
    val nik: String = "",
    val email: String = "",
    val noHp: String = "",
    val alamat: String = "",
    val tanggalLahir: String = "",
    val jenisKelamin: String = "",
    val role: String = "pasien"
)