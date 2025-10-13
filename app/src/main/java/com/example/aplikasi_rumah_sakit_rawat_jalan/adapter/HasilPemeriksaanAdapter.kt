package com.example.aplikasi_rumah_sakit_rawat_jalan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasi_rumah_sakit_rawat_jalan.R
import com.example.aplikasi_rumah_sakit_rawat_jalan.model.HasilPemeriksaan
import java.text.SimpleDateFormat
import java.util.*

class HasilPemeriksaanAdapter(
    private val listHasil: List<HasilPemeriksaan>
) : RecyclerView.Adapter<HasilPemeriksaanAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTanggal: TextView = view.findViewById(R.id.tv_tanggal)
        val tvPoli: TextView = view.findViewById(R.id.tv_poli)
        val tvNamaDokter: TextView = view.findViewById(R.id.tv_nama_dokter)
        val tvDiagnosis: TextView = view.findViewById(R.id.tv_diagnosis)
        val tvTindakan: TextView = view.findViewById(R.id.tv_tindakan)
        val tvResep: TextView = view.findViewById(R.id.tv_resep)
        val labelCatatan: TextView = view.findViewById(R.id.label_catatan)
        val tvCatatan: TextView = view.findViewById(R.id.tv_catatan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hasil_pemeriksaan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hasil = listHasil[position]

        // Format tanggal
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        val tanggal = try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = dateFormat.parse(hasil.tanggalPemeriksaan)
            sdf.format(date ?: Date())
        } catch (e: Exception) {
            hasil.tanggalPemeriksaan
        }

        holder.tvTanggal.text = tanggal
        holder.tvPoli.text = hasil.poli
        holder.tvNamaDokter.text = hasil.namaDokter
        holder.tvDiagnosis.text = hasil.diagnosis
        holder.tvTindakan.text = hasil.tindakan

        // Resep (kalau kosong, tampilkan "-")
        holder.tvResep.text = if (hasil.resep.isEmpty()) "-" else hasil.resep

        // Catatan Dokter (kalau kosong, sembunyikan)
        if (hasil.catatanDokter.isEmpty()) {
            holder.labelCatatan.visibility = View.GONE
            holder.tvCatatan.visibility = View.GONE
        } else {
            holder.labelCatatan.visibility = View.VISIBLE
            holder.tvCatatan.visibility = View.VISIBLE
            holder.tvCatatan.text = hasil.catatanDokter
        }
    }

    override fun getItemCount(): Int = listHasil.size
}