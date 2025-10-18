package com.example.aplikasi_rumah_sakit_rawat_jalan.model

data class DokterAdmin(
    var id: String = "",
    var namaDokter: String = "",
    var spesialisasi: String = "",
    var poli: String = "",
    var noStr: String = "",
    var status: String = "aktif"
)