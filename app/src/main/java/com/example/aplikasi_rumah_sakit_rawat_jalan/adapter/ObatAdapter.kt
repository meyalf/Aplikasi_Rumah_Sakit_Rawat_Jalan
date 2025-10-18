package com.example.aplikasi_rumah_sakit_rawat_jalan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasi_rumah_sakit_rawat_jalan.R
import com.example.aplikasi_rumah_sakit_rawat_jalan.model.Obat
import java.text.NumberFormat
import java.util.*

class ObatAdapter(
    private val obatList: List<Obat>,
    private val onEditClick: (Obat) -> Unit,
    private val onDeleteClick: (Obat) -> Unit
) : RecyclerView.Adapter<ObatAdapter.ObatViewHolder>() {

    inner class ObatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNamaObat: TextView = view.findViewById(R.id.tv_nama_obat)
        val tvJenisObat: TextView = view.findViewById(R.id.tv_jenis_obat)
        val tvStokObat: TextView = view.findViewById(R.id.tv_stok_obat)
        val tvHargaObat: TextView = view.findViewById(R.id.tv_harga_obat)
        val tvStatusObat: TextView = view.findViewById(R.id.tv_status_obat)
        val btnEdit: ImageButton = view.findViewById(R.id.btn_edit)
        val btnDelete: ImageButton = view.findViewById(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_obat, parent, false)
        return ObatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ObatViewHolder, position: Int) {
        val obat = obatList[position]

        holder.tvNamaObat.text = obat.namaObat
        holder.tvJenisObat.text = obat.jenisObat
        holder.tvStokObat.text = "Stok: ${obat.stok}"

        // Format harga
        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        holder.tvHargaObat.text = formatter.format(obat.harga)

        // Set warna stok berdasarkan jumlah
        when {
            obat.stok == 0 -> {
                holder.tvStokObat.setTextColor(holder.itemView.context.getColor(android.R.color.holo_red_dark))
            }
            obat.stok < 20 -> {
                holder.tvStokObat.setTextColor(holder.itemView.context.getColor(android.R.color.holo_orange_dark))
            }
            else -> {
                holder.tvStokObat.setTextColor(holder.itemView.context.getColor(android.R.color.holo_green_dark))
            }
        }

        // Set status
        if (obat.isActive && obat.stok > 0) {
            holder.tvStatusObat.text = "Tersedia"
            holder.tvStatusObat.setBackgroundResource(R.drawable.status_terdaftar_bg)
        } else if (obat.stok == 0) {
            holder.tvStatusObat.text = "Habis"
            holder.tvStatusObat.setBackgroundResource(R.drawable.status_dibatalkan_bg)
        } else {
            holder.tvStatusObat.text = "Nonaktif"
            holder.tvStatusObat.setBackgroundResource(R.drawable.status_menunggu_bg)
        }

        holder.btnEdit.setOnClickListener { onEditClick(obat) }
        holder.btnDelete.setOnClickListener { onDeleteClick(obat) }
    }

    override fun getItemCount(): Int = obatList.size
}