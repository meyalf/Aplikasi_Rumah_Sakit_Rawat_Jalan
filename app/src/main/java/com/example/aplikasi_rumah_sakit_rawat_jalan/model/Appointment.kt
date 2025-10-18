package com.example.aplikasi_rumah_sakit_rawat_jalan.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Appointment(
    var id: String = "",
    var userId: String = "",
    var pasienId: String = "",
    var namaPasien: String = "",
    var poli: String = "",
    var poliklinikId: String = "",
    var dokterId: String = "",
    var namaDokter: String = "",
    var tanggalKunjungan: String = "",
    var jamKunjungan: String = "",
    var jamPraktek: String = "",
    var keluhan: String = "",
    var status: String = "menunggu",
    var nomorAntrian: Int = 0,
    var tanggalDaftar: Long = 0,
    var catatan: String = ""
) : Parcelable