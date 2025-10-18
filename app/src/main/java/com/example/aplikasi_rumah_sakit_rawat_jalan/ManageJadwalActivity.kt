package com.example.aplikasi_rumah_sakit_rawat_jalan

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasi_rumah_sakit_rawat_jalan.adapter.JadwalAdapter
import com.example.aplikasi_rumah_sakit_rawat_jalan.model.Jadwal
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore

class ManageJadwalActivity : AppCompatActivity() {

    private lateinit var rvJadwal: RecyclerView
    private lateinit var btnTambahJadwal: MaterialButton
    private lateinit var adapter: JadwalAdapter
    private val jadwalList = mutableListOf<Jadwal>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_jadwal)

        supportActionBar?.title = "Kelola Jadwal"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initViews()
        setupRecyclerView()
        loadJadwalData()
        setupListeners()
    }

    private fun initViews() {
        rvJadwal = findViewById(R.id.rv_jadwal)
        btnTambahJadwal = findViewById(R.id.btn_tambah_jadwal)
    }

    private fun setupRecyclerView() {
        adapter = JadwalAdapter(jadwalList,
            onEditClick = { jadwal -> showAddEditDialog(jadwal) },
            onDeleteClick = { jadwal -> confirmDelete(jadwal) }
        )
        rvJadwal.layoutManager = LinearLayoutManager(this)
        rvJadwal.adapter = adapter
    }

    private fun setupListeners() {
        btnTambahJadwal.setOnClickListener {
            showAddEditDialog(null)
        }
    }

    private fun loadJadwalData() {
        db.collection("jadwal")
            .get()
            .addOnSuccessListener { documents ->
                jadwalList.clear()
                for (document in documents) {
                    val jadwal = document.toObject(Jadwal::class.java)
                    jadwal.id = document.id
                    jadwalList.add(jadwal)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal memuat data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showAddEditDialog(jadwal: Jadwal?) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_jadwal, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()

        val tvTitle = dialogView.findViewById<TextView>(R.id.tv_dialog_title)
        val etNamaDokter = dialogView.findViewById<EditText>(R.id.et_nama_dokter)
        val etNamaPoli = dialogView.findViewById<EditText>(R.id.et_nama_poli)
        val etHari = dialogView.findViewById<EditText>(R.id.et_hari)
        val etJamMulai = dialogView.findViewById<EditText>(R.id.et_jam_mulai)
        val etJamSelesai = dialogView.findViewById<EditText>(R.id.et_jam_selesai)
        val etKuota = dialogView.findViewById<EditText>(R.id.et_kuota)
        val btnCancel = dialogView.findViewById<Button>(R.id.btn_cancel)
        val btnSave = dialogView.findViewById<Button>(R.id.btn_save)

        // Set data jika edit
        if (jadwal != null) {
            tvTitle.text = "Edit Jadwal"
            etNamaDokter.setText(jadwal.namaDokter)
            etNamaPoli.setText(jadwal.namaPoli)
            etHari.setText(jadwal.hari)
            etJamMulai.setText(jadwal.jamMulai)
            etJamSelesai.setText(jadwal.jamSelesai)
            etKuota.setText(jadwal.kuotaPasien.toString())
        } else {
            tvTitle.text = "Tambah Jadwal Baru"
        }

        btnCancel.setOnClickListener { dialog.dismiss() }

        btnSave.setOnClickListener {
            val namaDokter = etNamaDokter.text.toString().trim()
            val namaPoli = etNamaPoli.text.toString().trim()
            val hari = etHari.text.toString().trim()
            val jamMulai = etJamMulai.text.toString().trim()
            val jamSelesai = etJamSelesai.text.toString().trim()
            val kuota = etKuota.text.toString().toIntOrNull() ?: 0

            if (namaDokter.isEmpty()) {
                etNamaDokter.error = "Nama dokter tidak boleh kosong"
                return@setOnClickListener
            }

            if (namaPoli.isEmpty()) {
                etNamaPoli.error = "Nama poli tidak boleh kosong"
                return@setOnClickListener
            }

            if (hari.isEmpty()) {
                etHari.error = "Hari tidak boleh kosong"
                return@setOnClickListener
            }

            val jadwalData = hashMapOf(
                "dokterId" to "",
                "namaDokter" to namaDokter,
                "poliId" to "",
                "namaPoli" to namaPoli,
                "hari" to hari,
                "jamMulai" to jamMulai,
                "jamSelesai" to jamSelesai,
                "kuotaPasien" to kuota,
                "isActive" to true
            )

            if (jadwal == null) {
                // Tambah baru
                db.collection("jadwal").add(jadwalData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Jadwal berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                        loadJadwalData()
                        dialog.dismiss()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Gagal menambahkan: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // Update
                db.collection("jadwal").document(jadwal.id).update(jadwalData as Map<String, Any>)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Jadwal berhasil diupdate", Toast.LENGTH_SHORT).show()
                        loadJadwalData()
                        dialog.dismiss()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Gagal mengupdate: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        dialog.show()
    }

    private fun confirmDelete(jadwal: Jadwal) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Jadwal")
            .setMessage("Apakah Anda yakin ingin menghapus jadwal ${jadwal.namaDokter} di ${jadwal.hari}?")
            .setPositiveButton("Hapus") { _, _ ->
                db.collection("jadwal").document(jadwal.id).delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Jadwal berhasil dihapus", Toast.LENGTH_SHORT).show()
                        loadJadwalData()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Gagal menghapus: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}