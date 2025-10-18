package com.example.aplikasi_rumah_sakit_rawat_jalan.model

import java.text.SimpleDateFormat
import java.util.*

object AntrianManager {
    private val antrianList = mutableListOf<Appointment>()

    init {
        // Load dummy data awal
        loadDummyData()
    }

    private fun loadDummyData() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = dateFormat.format(Date())

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val tomorrow = dateFormat.format(calendar.time)

        antrianList.addAll(listOf(
            Appointment(
                id = "1",
                userId = "dummy_user_1",
                pasienId = "1",
                namaPasien = "Pasien Dummy 1",
                dokterId = "1",
                namaDokter = "Dr. Dummy 1",
                poliklinikId = "1",
                poli = "Poli Gigi",
                tanggalKunjungan = today,
                jamKunjungan = "09:00",
                jamPraktek = "09:00 - 12:00",
                keluhan = "Sakit gigi geraham kiri, nyeri sudah 2 hari",
                status = "terdaftar",
                nomorAntrian = 5,
                tanggalDaftar = System.currentTimeMillis(),
                catatan = "Bawa hasil rontgen gigi"
            ),
            Appointment(
                id = "2",
                userId = "dummy_user_1",
                pasienId = "1",
                namaPasien = "Pasien Dummy 1",
                dokterId = "6",
                namaDokter = "Dr. Dummy 6",
                poliklinikId = "2",
                poli = "Poli Mata",
                tanggalKunjungan = tomorrow,
                jamKunjungan = "10:00",
                jamPraktek = "10:00 - 13:00",
                keluhan = "Mata kabur saat membaca, periksa minus",
                status = "menunggu",
                nomorAntrian = 12,
                tanggalDaftar = System.currentTimeMillis(),
                catatan = "Kontrol mata rutin"
            )
        ))
    }

    fun getAllAntrian(): List<Appointment> {
        android.util.Log.d("AntrianManager", "Get all antrian: ${antrianList.size} items")
        return antrianList.sortedByDescending { it.id }
    }

    fun addAntrian(appointment: Appointment) {
        antrianList.add(0, appointment)
        android.util.Log.d("AntrianManager", "Antrian ditambahkan: ${appointment.nomorAntrian}, Total: ${antrianList.size}")
    }

    fun getNextId(): String {
        return if (antrianList.isEmpty()) {
            "1"
        } else {
            val maxId = antrianList.mapNotNull { it.id.toIntOrNull() }.maxOrNull() ?: 0
            (maxId + 1).toString()
        }
    }

    fun getNextNomorAntrian(): Int {
        return (antrianList.size + 1)
    }

    fun deleteAntrian(appointmentId: String) {
        antrianList.removeAll { it.id == appointmentId }
    }
}