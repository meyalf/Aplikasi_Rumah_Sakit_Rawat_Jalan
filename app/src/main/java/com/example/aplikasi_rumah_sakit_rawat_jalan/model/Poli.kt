package com.example.aplikasi_rumah_sakit_rawat_jalan.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Poli(
    var id: String = "",
    var namaPoli: String = "",
    var deskripsi: String = "",
    var iconName: String = "",
    var isActive: Boolean = true
) : Parcelable