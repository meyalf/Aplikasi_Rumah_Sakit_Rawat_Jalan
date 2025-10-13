package com.example.aplikasi_rumah_sakit_rawat_jalan.model

data class HasilPemeriksaan(
    val id: String = "",
    val pendaftaranId: String = "",
    val pasienId: String = "",
    val namaPasien: String = "",
    val dokterId: String = "",
    val namaDokter: String = "",
    val poli: String = "",
    val tanggalPemeriksaan: String = "",
    val diagnosis: String = "",
    val tindakan: String = "",
    val resep: String = "",
    val catatanDokter: String = "",
    val waktuPemeriksaan: Long = 0L
)