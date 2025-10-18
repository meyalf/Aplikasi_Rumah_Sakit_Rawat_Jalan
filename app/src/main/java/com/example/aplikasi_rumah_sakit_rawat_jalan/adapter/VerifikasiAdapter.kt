package com.example.aplikasi_rumah_sakit_rawat_jalan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasi_rumah_sakit_rawat_jalan.R
import com.example.aplikasi_rumah_sakit_rawat_jalan.model.Appointment

class VerifikasiAdapter(
    private val appointmentList: List<Appointment>,
    private val onActionClick: (Appointment, String) -> Unit
) : RecyclerView.Adapter<VerifikasiAdapter.VerifikasiViewHolder>() {

    inner class VerifikasiViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNamaPasien: TextView = view.findViewById(R.id.tv_nama_pasien)
        val tvPoli: TextView = view.findViewById(R.id.tv_poli)
        val tvDokter: TextView = view.findViewById(R.id.tv_dokter)
        val tvTanggal: TextView = view.findViewById(R.id.tv_tanggal)
        val tvJam: TextView = view.findViewById(R.id.tv_jam)
        val tvStatus: TextView = view.findViewById(R.id.tv_status)
        val btnApprove: Button = view.findViewById(R.id.btn_approve)
        val btnReject: Button = view.findViewById(R.id.btn_reject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerifikasiViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_verifikasi_pendaftaran, parent, false)
        return VerifikasiViewHolder(view)
    }

    override fun onBindViewHolder(holder: VerifikasiViewHolder, position: Int) {
        val appointment = appointmentList[position]

        holder.tvNamaPasien.text = appointment.namaPasien.ifEmpty { "Nama tidak tersedia" }
        holder.tvPoli.text = "Poli: ${appointment.poli}"
        holder.tvDokter.text = "Dokter: ${appointment.namaDokter}"
        holder.tvTanggal.text = "Tanggal: ${appointment.tanggalKunjungan}"
        holder.tvJam.text = "Jam: ${appointment.jamPraktek}"
        holder.tvStatus.text = appointment.status.uppercase()

        // Tombol Approve
        holder.btnApprove.setOnClickListener {
            onActionClick(appointment, "approve")
        }

        // Tombol Reject
        holder.btnReject.setOnClickListener {
            onActionClick(appointment, "reject")
        }
    }

    override fun getItemCount(): Int = appointmentList.size
}