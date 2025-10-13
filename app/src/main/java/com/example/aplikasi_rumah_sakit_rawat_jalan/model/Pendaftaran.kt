package com.example.aplikasi_rumah_sakit_rawat_jalan.model

import java.io.Serializable

data class Pendaftaran(
    val id: String = "",
    val pasienId: String = "",
    val namaPasien: String = "",
    val noTelepon: String = "",
    val dokterId: String = "",
    val namaDokter: String = "",
    val poli: String = "",
    val tanggalKunjungan: String = "",
    val nomorAntrian: Int = 0,
    val keluhan: String = "",
    val status: String = "menunggu",
    val waktuDaftar: Long = 0L
) : Serializable