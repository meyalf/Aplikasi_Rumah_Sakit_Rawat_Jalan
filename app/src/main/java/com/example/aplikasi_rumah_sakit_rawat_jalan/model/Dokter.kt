package com.example.aplikasi_rumah_sakit_rawat_jalan.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Dokter(
    val id: Int = 0,
    val nama: String = "",
    val spesialis: String = "",
    val poliklinik: String = "",
    val hari: List<String> = emptyList(),
    val jamPraktek: String = "",
    val foto: Int = 0,
    val status: String = "Tersedia"
) : Parcelable