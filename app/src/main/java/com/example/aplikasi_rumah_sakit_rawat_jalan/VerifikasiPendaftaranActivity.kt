package com.example.aplikasi_rumah_sakit_rawat_jalan

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasi_rumah_sakit_rawat_jalan.adapter.VerifikasiAdapter
import com.example.aplikasi_rumah_sakit_rawat_jalan.model.Appointment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class VerifikasiPendaftaranActivity : AppCompatActivity() {

    private lateinit var rvPendaftaran: RecyclerView
    private lateinit var adapter: VerifikasiAdapter
    private lateinit var db: FirebaseFirestore
    private val pendaftaranList = mutableListOf<Appointment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verifikasi_pendaftaran)

        // Setup ActionBar
        supportActionBar?.title = "Verifikasi Pendaftaran"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Inisialisasi Firebase
        db = FirebaseFirestore.getInstance()

        // Setup RecyclerView
        initRecyclerView()

        // Load data pendaftaran
        loadPendaftaranData()
    }

    private fun initRecyclerView() {
        rvPendaftaran = findViewById(R.id.rv_pendaftaran)
        rvPendaftaran.layoutManager = LinearLayoutManager(this)

        // Setup adapter dengan callback untuk tombol
        adapter = VerifikasiAdapter(pendaftaranList) { appointment, action ->
            when (action) {
                "approve" -> confirmApprove(appointment)
                "reject" -> confirmReject(appointment)
            }
        }

        rvPendaftaran.adapter = adapter
    }

    private fun loadPendaftaranData() {
        // Ambil data appointment yang statusnya "menunggu"
        db.collection("appointments")
            .whereEqualTo("status", "menunggu")
            .orderBy("tanggalDaftar", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                pendaftaranList.clear()
                for (document in documents) {
                    val appointment = document.toObject(Appointment::class.java)
                    appointment.id = document.id
                    pendaftaranList.add(appointment)
                }
                adapter.notifyDataSetChanged()

                if (pendaftaranList.isEmpty()) {
                    Toast.makeText(this, "Tidak ada pendaftaran yang perlu diverifikasi", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal memuat data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun confirmApprove(appointment: Appointment) {
        AlertDialog.Builder(this)
            .setTitle("Setujui Pendaftaran")
            .setMessage("Apakah Anda yakin ingin menyetujui pendaftaran pasien ${appointment.namaPasien}?")
            .setPositiveButton("Setujui") { _, _ ->
                updateStatus(appointment, "terdaftar")
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun confirmReject(appointment: Appointment) {
        AlertDialog.Builder(this)
            .setTitle("Tolak Pendaftaran")
            .setMessage("Apakah Anda yakin ingin menolak pendaftaran pasien ${appointment.namaPasien}?")
            .setPositiveButton("Tolak") { _, _ ->
                updateStatus(appointment, "dibatalkan")
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun updateStatus(appointment: Appointment, newStatus: String) {
        // Update status di Firestore
        db.collection("appointments").document(appointment.id)
            .update("status", newStatus)
            .addOnSuccessListener {
                val message = if (newStatus == "terdaftar") {
                    "Pendaftaran berhasil disetujui"
                } else {
                    "Pendaftaran berhasil ditolak"
                }
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                // Reload data
                loadPendaftaranData()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal mengupdate status: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Tombol back di ActionBar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}