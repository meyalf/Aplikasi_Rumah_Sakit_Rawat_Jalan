package com.example.aplikasi_rumah_sakit_rawat_jalan

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class LaporanActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    private lateinit var btnHarian: Button
    private lateinit var btnMingguan: Button
    private lateinit var btnBulanan: Button

    private lateinit var tvTotalPasien: TextView
    private lateinit var tvPendaftaranBaru: TextView
    private lateinit var tvTerdaftar: TextView
    private lateinit var tvMenunggu: TextView
    private lateinit var tvSelesai: TextView
    private lateinit var tvDibatalkan: TextView
    private lateinit var tvPoliGigi: TextView
    private lateinit var tvPoliMata: TextView
    private lateinit var tvPeriode: TextView

    private var currentFilter = "harian"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_laporan)

        supportActionBar?.title = "Laporan"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = FirebaseFirestore.getInstance()

        initViews()
        setupClickListeners()
        loadLaporan("harian")
    }

    private fun initViews() {
        btnHarian = findViewById(R.id.btn_harian)
        btnMingguan = findViewById(R.id.btn_mingguan)
        btnBulanan = findViewById(R.id.btn_bulanan)

        tvTotalPasien = findViewById(R.id.tv_total_pasien)
        tvPendaftaranBaru = findViewById(R.id.tv_pendaftaran_baru)
        tvTerdaftar = findViewById(R.id.tv_terdaftar)
        tvMenunggu = findViewById(R.id.tv_menunggu)
        tvSelesai = findViewById(R.id.tv_selesai)
        tvDibatalkan = findViewById(R.id.tv_dibatalkan)
        tvPoliGigi = findViewById(R.id.tv_poli_gigi)
        tvPoliMata = findViewById(R.id.tv_poli_mata)
        tvPeriode = findViewById(R.id.tv_periode)
    }

    private fun setupClickListeners() {
        btnHarian.setOnClickListener {
            currentFilter = "harian"
            updateButtonColors()
            loadLaporan("harian")
        }

        btnMingguan.setOnClickListener {
            currentFilter = "mingguan"
            updateButtonColors()
            loadLaporan("mingguan")
        }

        btnBulanan.setOnClickListener {
            currentFilter = "bulanan"
            updateButtonColors()
            loadLaporan("bulanan")
        }
    }

    private fun updateButtonColors() {
        val activeColor = 0xFF2196F3.toInt()
        val inactiveColor = 0xFF9E9E9E.toInt()

        btnHarian.setBackgroundColor(if (currentFilter == "harian") activeColor else inactiveColor)
        btnMingguan.setBackgroundColor(if (currentFilter == "mingguan") activeColor else inactiveColor)
        btnBulanan.setBackgroundColor(if (currentFilter == "bulanan") activeColor else inactiveColor)
    }

    private fun loadLaporan(periode: String) {
        val calendar = Calendar.getInstance()
        val endDate = calendar.timeInMillis

        // Hitung tanggal mulai berdasarkan periode
        val startDate = when (periode) {
            "harian" -> {
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                calendar.timeInMillis
            }
            "mingguan" -> {
                calendar.add(Calendar.DAY_OF_YEAR, -7)
                calendar.timeInMillis
            }
            "bulanan" -> {
                calendar.add(Calendar.DAY_OF_YEAR, -30)
                calendar.timeInMillis
            }
            else -> endDate
        }

        // Update text periode
        val periodeText = when (periode) {
            "harian" -> "Periode: Hari ini (${getCurrentDate()})"
            "mingguan" -> "Periode: 7 hari terakhir"
            "bulanan" -> "Periode: 30 hari terakhir"
            else -> "Periode: -"
        }
        tvPeriode.text = periodeText

        // Load data dari Firestore
        loadStatistikAppointments(startDate, endDate)
        loadTotalPasien()
    }

    private fun loadStatistikAppointments(startDate: Long, endDate: Long) {
        db.collection("appointments")
            .whereGreaterThanOrEqualTo("tanggalDaftar", startDate)
            .whereLessThanOrEqualTo("tanggalDaftar", endDate)
            .get()
            .addOnSuccessListener { documents ->
                var totalPendaftaran = 0
                var countTerdaftar = 0
                var countMenunggu = 0
                var countSelesai = 0
                var countDibatalkan = 0
                var countPoliGigi = 0
                var countPoliMata = 0

                for (document in documents) {
                    totalPendaftaran++

                    val status = document.getString("status") ?: ""
                    val poli = document.getString("poli") ?: ""

                    // Hitung per status
                    when (status.lowercase()) {
                        "terdaftar" -> countTerdaftar++
                        "menunggu" -> countMenunggu++
                        "selesai" -> countSelesai++
                        "dibatalkan" -> countDibatalkan++
                    }

                    // Hitung per poli
                    when {
                        poli.contains("Gigi", ignoreCase = true) -> countPoliGigi++
                        poli.contains("Mata", ignoreCase = true) -> countPoliMata++
                    }
                }

                // Update UI
                tvPendaftaranBaru.text = totalPendaftaran.toString()
                tvTerdaftar.text = countTerdaftar.toString()
                tvMenunggu.text = countMenunggu.toString()
                tvSelesai.text = countSelesai.toString()
                tvDibatalkan.text = countDibatalkan.toString()
                tvPoliGigi.text = countPoliGigi.toString()
                tvPoliMata.text = countPoliMata.toString()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal memuat data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadTotalPasien() {
        db.collection("users")
            .whereEqualTo("role", "pasien")
            .get()
            .addOnSuccessListener { documents ->
                tvTotalPasien.text = documents.size().toString()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal memuat total pasien: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale("id", "ID"))
        return sdf.format(Date())
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}