package com.example.aplikasi_rumah_sakit_rawat_jalan.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Obat(
    var id: String = "",
    var namaObat: String = "",
    var jenisObat: String = "", // Tablet, Sirup, Kapsul, dll
    var stok: Int = 0,
    var harga: Int = 0,
    var deskripsi: String = "",
    var isActive: Boolean = true
) : Parcelable