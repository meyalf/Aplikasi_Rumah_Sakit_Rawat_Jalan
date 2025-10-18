package com.example.aplikasi_rumah_sakit_rawat_jalan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasi_rumah_sakit_rawat_jalan.R
import com.example.aplikasi_rumah_sakit_rawat_jalan.model.Dokter

class DokterAdapter(
    private val dokterList: List<Dokter>,
    private val onItemClick: (Dokter) -> Unit
) : RecyclerView.Adapter<DokterAdapter.DokterViewHolder>() {

    inner class DokterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivIconDokter: ImageView = view.findViewById(R.id.iv_icon_dokter)
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

        holder.tvNamaDokter.text = dokter.nama
        holder.tvSpesialisasi.text = "Spesialis: ${dokter.spesialis}"

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

        holder.tvStatus.text = "Status: ${dokter.status}"

        if (dokter.status == "Tersedia") {
            holder.tvStatus.setTextColor(holder.itemView.context.getColor(android.R.color.holo_green_dark))
        } else {
            holder.tvStatus.setTextColor(holder.itemView.context.getColor(android.R.color.holo_red_dark))
        }

        if (dokter.foto != 0) {
            holder.ivIconDokter.setImageResource(dokter.foto)
        } else {
            holder.ivIconDokter.setImageResource(android.R.drawable.ic_menu_myplaces)
        }

        holder.itemView.setOnClickListener {
            onItemClick(dokter)
        }
    }

    override fun getItemCount(): Int = dokterList.size
}