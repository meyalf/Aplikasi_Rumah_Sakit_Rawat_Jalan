package com.example.aplikasi_rumah_sakit_rawat_jalan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasi_rumah_sakit_rawat_jalan.R
import com.example.aplikasi_rumah_sakit_rawat_jalan.model.Jadwal

class JadwalAdapter(
    private val jadwalList: List<Jadwal>,
    private val onEditClick: (Jadwal) -> Unit,
    private val onDeleteClick: (Jadwal) -> Unit
) : RecyclerView.Adapter<JadwalAdapter.JadwalViewHolder>() {

    inner class JadwalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNamaDokter: TextView = view.findViewById(R.id.tv_nama_dokter_jadwal)
        val tvPoli: TextView = view.findViewById(R.id.tv_poli_jadwal)
        val tvHariJam: TextView = view.findViewById(R.id.tv_hari_jam)
        val tvKuota: TextView = view.findViewById(R.id.tv_kuota)
        val tvStatus: TextView = view.findViewById(R.id.tv_status_jadwal)
        val btnEdit: ImageButton = view.findViewById(R.id.btn_edit)
        val btnDelete: ImageButton = view.findViewById(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JadwalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_jadwal, parent, false)
        return JadwalViewHolder(view)
    }

    override fun onBindViewHolder(holder: JadwalViewHolder, position: Int) {
        val jadwal = jadwalList[position]

        holder.tvNamaDokter.text = jadwal.namaDokter
        holder.tvPoli.text = jadwal.namaPoli
        holder.tvHariJam.text = "${jadwal.hari}, ${jadwal.jamMulai} - ${jadwal.jamSelesai}"
        holder.tvKuota.text = "Kuota: ${jadwal.kuotaPasien} pasien"

        // Set status
        if (jadwal.isActive) {
            holder.tvStatus.text = "Aktif"
            holder.tvStatus.setBackgroundResource(R.drawable.status_terdaftar_bg)
        } else {
            holder.tvStatus.text = "Nonaktif"
            holder.tvStatus.setBackgroundResource(R.drawable.status_dibatalkan_bg)
        }

        holder.btnEdit.setOnClickListener { onEditClick(jadwal) }
        holder.btnDelete.setOnClickListener { onDeleteClick(jadwal) }
    }

    override fun getItemCount(): Int = jadwalList.size
}