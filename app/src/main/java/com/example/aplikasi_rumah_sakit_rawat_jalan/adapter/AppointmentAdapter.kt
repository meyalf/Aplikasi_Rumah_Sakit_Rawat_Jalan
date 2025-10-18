package com.example.aplikasi_rumah_sakit_rawat_jalan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasi_rumah_sakit_rawat_jalan.R
import com.example.aplikasi_rumah_sakit_rawat_jalan.model.Appointment
import java.text.SimpleDateFormat
import java.util.*

class AppointmentAdapter(
    private val appointments: List<Appointment>,
    private val onActionClick: (Appointment, String) -> Unit
) : RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    class AppointmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textNomorAntrian: TextView = view.findViewById(R.id.textNomorAntrian)
        val textTanggalJam: TextView = view.findViewById(R.id.textTanggalJam)
        val textKeluhan: TextView = view.findViewById(R.id.textKeluhan)
        val textStatus: TextView = view.findViewById(R.id.textStatus)
        val textEstimasiWaktu: TextView = view.findViewById(R.id.textEstimasiWaktu)
        val textSisaAntrian: TextView = view.findViewById(R.id.textSisaAntrian)
        val buttonDownloadStruk: Button = view.findViewById(R.id.buttonDownloadStruk)
        val buttonDetail: Button = view.findViewById(R.id.buttonDetail)
        val buttonCancel: Button = view.findViewById(R.id.buttonCancel)
        val buttonRefresh: Button = view.findViewById(R.id.buttonRefresh)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_appointment, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appointment = appointments[position]

        // Set nomor antrian
        holder.textNomorAntrian.text = appointment.nomorAntrian.toString()

        // Set tanggal & jam
        holder.textTanggalJam.text = "${appointment.tanggalKunjungan} - ${appointment.jamKunjungan}"

        // Set keluhan
        holder.textKeluhan.text = appointment.keluhan.ifEmpty { "Tidak ada keluhan" }

        // Set status berdasarkan String (bukan enum)
        when (appointment.status.lowercase()) {
            "terdaftar" -> {
                holder.textStatus.text = "Terdaftar"
                holder.textStatus.setBackgroundResource(R.drawable.status_terdaftar_bg)
            }
            "menunggu" -> {
                holder.textStatus.text = "Menunggu"
                holder.textStatus.setBackgroundResource(R.drawable.status_menunggu_bg)
            }
            "sedang_dilayani", "sedang dilayani" -> {
                holder.textStatus.text = "Sedang Dilayani"
                holder.textStatus.setBackgroundResource(R.drawable.status_dipanggil_bg)
            }
            "selesai" -> {
                holder.textStatus.text = "Selesai"
                holder.textStatus.setBackgroundResource(R.drawable.status_selesai_bg)
            }
            "dibatalkan" -> {
                holder.textStatus.text = "Dibatalkan"
                holder.textStatus.setBackgroundResource(R.drawable.status_dibatalkan_bg)
            }
            "tidak_hadir", "tidak hadir" -> {
                holder.textStatus.text = "Tidak Hadir"
                holder.textStatus.setBackgroundResource(R.drawable.status_dibatalkan_bg)
            }
            else -> {
                holder.textStatus.text = appointment.status.uppercase()
                holder.textStatus.setBackgroundResource(R.drawable.status_menunggu_bg)
            }
        }

        // Tampilkan info antrian jika status menunggu atau terdaftar
        val statusLower = appointment.status.lowercase()
        if (statusLower == "menunggu" || statusLower == "terdaftar") {
            val sisaAntrian = (1..5).random()
            val estimasiWaktu = sisaAntrian * 10

            holder.textSisaAntrian.text = "$sisaAntrian antrian lagi"
            holder.textEstimasiWaktu.text = "± $estimasiWaktu menit"

            holder.textSisaAntrian.visibility = View.VISIBLE
            holder.textEstimasiWaktu.visibility = View.VISIBLE
        } else {
            holder.textSisaAntrian.visibility = View.GONE
            holder.textEstimasiWaktu.visibility = View.GONE
        }

        // Tombol Download Struk
        holder.buttonDownloadStruk.setOnClickListener {
            onActionClick(appointment, "download")
        }

        // Tombol Detail
        holder.buttonDetail.setOnClickListener {
            onActionClick(appointment, "detail")
        }

        // Tombol Cancel
        holder.buttonCancel.setOnClickListener {
            onActionClick(appointment, "cancel")
        }

        // Tombol Refresh
        holder.buttonRefresh.setOnClickListener {
            onActionClick(appointment, "refresh")
        }

        // Sembunyikan tombol cancel jika sudah selesai/dibatalkan/tidak hadir
        if (statusLower == "selesai" || statusLower == "dibatalkan" || statusLower == "tidak_hadir" || statusLower == "tidak hadir") {
            holder.buttonCancel.visibility = View.GONE
        } else {
            holder.buttonCancel.visibility = View.VISIBLE
        }
    }

    override fun getItemCount() = appointments.size
}