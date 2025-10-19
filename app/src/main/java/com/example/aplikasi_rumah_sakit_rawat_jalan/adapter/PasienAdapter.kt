package com.example.aplikasi_rumah_sakit_rawat_jalan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PasienAdapter(
    private var pasienList: List<Pasien>,
    private val onEditClick: (Pasien) -> Unit,
    private val onDeleteClick: (Pasien) -> Unit
) : RecyclerView.Adapter<PasienAdapter.PasienViewHolder>() {

    class PasienViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNamaPasien: TextView = view.findViewById(R.id.tv_nama_pasien)
        val tvNik: TextView = view.findViewById(R.id.tv_nik)
        val tvEmail: TextView = view.findViewById(R.id.tv_email)
        val tvNoHp: TextView = view.findViewById(R.id.tv_no_hp)
        val tvAlamat: TextView = view.findViewById(R.id.tv_alamat)
        val btnEdit: Button = view.findViewById(R.id.btn_edit)
        val btnDelete: Button = view.findViewById(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PasienViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pasien, parent, false)
        return PasienViewHolder(view)
    }

    override fun onBindViewHolder(holder: PasienViewHolder, position: Int) {
        val pasien = pasienList[position]

        holder.tvNamaPasien.text = pasien.nama
        holder.tvNik.text = "NIK: ${pasien.nik}"
        holder.tvEmail.text = pasien.email
        holder.tvNoHp.text = pasien.noHp
        holder.tvAlamat.text = pasien.alamat

        holder.btnEdit.setOnClickListener {
            onEditClick(pasien)
        }

        holder.btnDelete.setOnClickListener {
            onDeleteClick(pasien)
        }
    }

    override fun getItemCount(): Int = pasienList.size

    fun updateData(newList: List<Pasien>) {
        pasienList = newList
        notifyDataSetChanged()
    }
}