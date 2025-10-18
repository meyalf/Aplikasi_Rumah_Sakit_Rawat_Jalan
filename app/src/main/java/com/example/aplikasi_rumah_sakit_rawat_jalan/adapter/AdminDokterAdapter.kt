package com.example.aplikasi_rumah_sakit_rawat_jalan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasi_rumah_sakit_rawat_jalan.R
import com.example.aplikasi_rumah_sakit_rawat_jalan.model.Dokter

class AdminDokterAdapter(
    private val dokterList: MutableList<Dokter>,
    private val onActionClick: (Dokter, String) -> Unit
) : RecyclerView.Adapter<AdminDokterAdapter.DokterViewHolder>() {

    inner class DokterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivIconDokter: ImageView = view.findViewById(R.id.iv_icon_dokter)
        val ivIconPoli: ImageView = view.findViewById(R.id.iv_icon_poli)
        val tvNamaDokter: TextView = view.findViewById(R.id.tv_nama_dokter)
        val tvPoli: TextView = view.findViewById(R.id.tv_poli_dokter)
        val tvSpesialisasi: TextView = view.findViewById(R.id.tv_spesialisasi)
        val tvNoStr: TextView = view.findViewById(R.id.tv_no_str)
        val btnEdit: ImageButton = view.findViewById(R.id.btn_edit_dokter)
        val btnDelete: ImageButton = view.findViewById(R.id.btn_delete_dokter)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DokterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_dokter, parent, false)
        return DokterViewHolder(view)
    }

    override fun onBindViewHolder(holder: DokterViewHolder, position: Int) {
        val dokter = dokterList[position]

        holder.tvNamaDokter.text = dokter.nama
        holder.tvPoli.text = dokter.poliklinik
        holder.tvSpesialisasi.text = "Spesialisasi: ${dokter.spesialis}"
        holder.tvNoStr.text = "Status: ${dokter.status}"

        // Ubah icon poli
        if (dokter.poliklinik.contains("Gigi", ignoreCase = true)) {
            holder.ivIconPoli.setImageResource(R.drawable.ic_dental)
        } else if (dokter.poliklinik.contains("Mata", ignoreCase = true)) {
            holder.ivIconPoli.setImageResource(R.drawable.ic_eye)
        }

        holder.btnEdit.setOnClickListener { onActionClick(dokter, "edit") }
        holder.btnDelete.setOnClickListener { onActionClick(dokter, "delete") }
    }

    override fun getItemCount(): Int = dokterList.size

    fun updateList(newList: List<Dokter>) {
        dokterList.clear()
        dokterList.addAll(newList)
        notifyDataSetChanged()
    }
}
