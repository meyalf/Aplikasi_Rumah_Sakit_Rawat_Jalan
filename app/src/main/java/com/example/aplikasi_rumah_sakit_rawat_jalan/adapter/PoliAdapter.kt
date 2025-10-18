package com.example.aplikasi_rumah_sakit_rawat_jalan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasi_rumah_sakit_rawat_jalan.R
import com.example.aplikasi_rumah_sakit_rawat_jalan.model.Poli

class PoliAdapter(
    private val poliList: List<Poli>,
    private val onEditClick: (Poli) -> Unit,
    private val onDeleteClick: (Poli) -> Unit
) : RecyclerView.Adapter<PoliAdapter.PoliViewHolder>() {

    inner class PoliViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivIconPoli: ImageView = view.findViewById(R.id.iv_icon_poli)
        val tvNamaPoli: TextView = view.findViewById(R.id.tv_nama_poli)
        val tvDeskripsiPoli: TextView = view.findViewById(R.id.tv_deskripsi_poli)
        val tvStatusPoli: TextView = view.findViewById(R.id.tv_status_poli)
        val btnEdit: ImageButton = view.findViewById(R.id.btn_edit)
        val btnDelete: ImageButton = view.findViewById(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PoliViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_poli, parent, false)
        return PoliViewHolder(view)
    }

    override fun onBindViewHolder(holder: PoliViewHolder, position: Int) {
        val poli = poliList[position]

        holder.tvNamaPoli.text = poli.namaPoli
        holder.tvDeskripsiPoli.text = poli.deskripsi

        // Set status
        if (poli.isActive) {
            holder.tvStatusPoli.text = "Aktif"
            holder.tvStatusPoli.setBackgroundResource(R.drawable.status_terdaftar_bg)
        } else {
            holder.tvStatusPoli.text = "Nonaktif"
            holder.tvStatusPoli.setBackgroundResource(R.drawable.status_dibatalkan_bg)
        }

        // Set icon berdasarkan nama poli
        when (poli.namaPoli.lowercase()) {
            "poli gigi" -> holder.ivIconPoli.setImageResource(R.drawable.ic_dental)
            "poli mata" -> holder.ivIconPoli.setImageResource(R.drawable.ic_eye)
            else -> holder.ivIconPoli.setImageResource(R.drawable.ic_home)
        }

        holder.btnEdit.setOnClickListener { onEditClick(poli) }
        holder.btnDelete.setOnClickListener { onDeleteClick(poli) }
    }

    override fun getItemCount(): Int = poliList.size
}