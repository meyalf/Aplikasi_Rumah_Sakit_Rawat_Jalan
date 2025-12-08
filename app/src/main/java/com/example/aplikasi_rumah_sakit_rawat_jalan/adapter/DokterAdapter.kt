package com.example.aplikasi_rumah_sakit_rawat_jalan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasi_rumah_sakit_rawat_jalan.R
import com.example.aplikasi_rumah_sakit_rawat_jalan.model.Dokter

class DokterAdapter(
    private val dokterList: List<Dokter>,
    private val onItemClick: (Dokter) -> Unit
) : RecyclerView.Adapter<DokterAdapter.DokterViewHolder>() {

    inner class DokterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvEmojiDokter: TextView = view.findViewById(R.id.tv_emoji_dokter)
        val tvNamaDokter: TextView = view.findViewById(R.id.tv_nama_dokter)
        val tvSpesialisasi: TextView = view.findViewById(R.id.tv_spesialisasi)
        val tvJadwal: TextView = view.findViewById(R.id.tv_jadwal)
        val tvStatus: TextView = view.findViewById(R.id.tv_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DokterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dokter, parent, false)
        return DokterViewHolder(view)
    }

    override fun onBindViewHolder(holder: DokterViewHolder, position: Int) {
        val dokter = dokterList[position]

        // Deteksi gender dari nama dokter
        val emoji = detectGenderFromName(dokter.nama, dokter.gender)
        holder.tvEmojiDokter.text = emoji

        // Set nama dokter
        holder.tvNamaDokter.text = dokter.nama

        // Set spesialisasi
        holder.tvSpesialisasi.text = "Spesialis: ${dokter.spesialis}"

        // Set jadwal
        val hariText = if (dokter.hari.isNotEmpty()) {
            dokter.hari.joinToString(", ")
        } else {
            "Senin - Jumat"
        }

        val jadwalText = if (dokter.jamPraktek.isNotEmpty()) {
            "$hariText • ${dokter.jamPraktek}"
        } else {
            "$hariText • 08:00 - 12:00"
        }
        holder.tvJadwal.text = jadwalText

        // Set status dengan warna
        holder.tvStatus.text = "Status: ${dokter.status}"

        if (dokter.status == "Tersedia") {
            holder.tvStatus.setTextColor(holder.itemView.context.getColor(android.R.color.holo_green_dark))
        } else {
            holder.tvStatus.setTextColor(holder.itemView.context.getColor(android.R.color.holo_red_dark))
        }

        // Click listener
        holder.itemView.setOnClickListener {
            onItemClick(dokter)
        }
    }

    override fun getItemCount(): Int = dokterList.size

    // Fungsi untuk deteksi gender dari nama
    private fun detectGenderFromName(nama: String, genderFromDb: String): String {
        // Kalau gender sudah ada di database, pakai itu
        if (genderFromDb.isNotEmpty()) {
            return when {
                genderFromDb.equals("Perempuan", ignoreCase = true) -> "👩🏻‍⚕️"
                genderFromDb.equals("Female", ignoreCase = true) -> "👩🏻‍⚕️"
                genderFromDb.equals("Laki-laki", ignoreCase = true) -> "👨🏻‍⚕️"
                genderFromDb.equals("Male", ignoreCase = true) -> "👨🏻‍⚕️"
                else -> "👨🏻‍⚕️"
            }
        }

        // Kalau gender kosong, deteksi dari nama
        val namaLower = nama.lowercase()

        // Daftar nama perempuan (tambahkan sesuai kebutuhan)
        val namaPerempuan = listOf(
            "sari", "maya", "rina", "amelia", "kusuma",
            "wijayanti", "putri", "dewi", "ayu", "sri",
            "fitri", "ratna", "indah", "lestari", "wulandari",
            "kartika", "nur", "siti", "diah", "eka"
        )

        // Daftar nama laki-laki (tambahkan sesuai kebutuhan)
        val namaLakiLaki = listOf(
            "ahmad", "budi", "prasetyo", "santoso", "dhani",
            "agus", "rudi", "andi", "dedi", "eko",
            "hadi", "joko", "bambang", "wahyu", "yanto"
        )

        // Cek apakah nama mengandung kata dari daftar perempuan
        for (keyword in namaPerempuan) {
            if (namaLower.contains(keyword)) {
                return "👩🏻‍⚕️"
            }
        }

        // Cek apakah nama mengandung kata dari daftar laki-laki
        for (keyword in namaLakiLaki) {
            if (namaLower.contains(keyword)) {
                return "👨🏻‍⚕️"
            }
        }

        // Default: laki-laki
        return "👨🏻‍⚕️"
    }
}