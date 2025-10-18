package com.example.aplikasi_rumah_sakit_rawat_jalan.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Jadwal(
    var id: String = "",
    var dokterId: String = "",
    var namaDokter: String = "",
    var poliId: String = "",
    var namaPoli: String = "",
    var hari: String = "", // Senin, Selasa, dst
    var jamMulai: String = "",
    var jamSelesai: String = "",
    var kuotaPasien: Int = 0,
    var isActive: Boolean = true
) : Parcelable