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
import com.example.aplikasi_rumah_sakit_rawat_jalan.adapter.ObatAdapter
import com.example.aplikasi_rumah_sakit_rawat_jalan.model.Obat
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore

class ManageObatActivity : AppCompatActivity() {

    private lateinit var rvObat: RecyclerView
    private lateinit var btnTambahObat: MaterialButton
    private lateinit var adapter: ObatAdapter
    private val obatList = mutableListOf<Obat>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_obat)

        supportActionBar?.title = "Kelola Obat"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initViews()
        setupRecyclerView()
        loadObatData()
        setupListeners()
    }

    private fun initViews() {
        rvObat = findViewById(R.id.rv_obat)
        btnTambahObat = findViewById(R.id.btn_tambah_obat)
    }

    private fun setupRecyclerView() {
        adapter = ObatAdapter(obatList,
            onEditClick = { obat -> showAddEditDialog(obat) },
            onDeleteClick = { obat -> confirmDelete(obat) }
        )
        rvObat.layoutManager = LinearLayoutManager(this)
        rvObat.adapter = adapter
    }

    private fun setupListeners() {
        btnTambahObat.setOnClickListener {
            showAddEditDialog(null)
        }
    }

    private fun loadObatData() {
        db.collection("obat")
            .get()
            .addOnSuccessListener { documents ->
                obatList.clear()
                for (document in documents) {
                    val obat = document.toObject(Obat::class.java)
                    obat.id = document.id
                    obatList.add(obat)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal memuat data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showAddEditDialog(obat: Obat?) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_obat, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()

        val tvTitle = dialogView.findViewById<TextView>(R.id.tv_dialog_title)
        val etNamaObat = dialogView.findViewById<EditText>(R.id.et_nama_obat)
        val etJenisObat = dialogView.findViewById<EditText>(R.id.et_jenis_obat)
        val etStok = dialogView.findViewById<EditText>(R.id.et_stok)
        val etHarga = dialogView.findViewById<EditText>(R.id.et_harga)
        val etDeskripsi = dialogView.findViewById<EditText>(R.id.et_deskripsi)
        val btnCancel = dialogView.findViewById<Button>(R.id.btn_cancel)
        val btnSave = dialogView.findViewById<Button>(R.id.btn_save)

        // Set data jika edit
        if (obat != null) {
            tvTitle.text = "Edit Obat"
            etNamaObat.setText(obat.namaObat)
            etJenisObat.setText(obat.jenisObat)
            etStok.setText(obat.stok.toString())
            etHarga.setText(obat.harga.toString())
            etDeskripsi.setText(obat.deskripsi)
        } else {
            tvTitle.text = "Tambah Obat Baru"
        }

        btnCancel.setOnClickListener { dialog.dismiss() }

        btnSave.setOnClickListener {
            val namaObat = etNamaObat.text.toString().trim()
            val jenisObat = etJenisObat.text.toString().trim()
            val stok = etStok.text.toString().toIntOrNull() ?: 0
            val harga = etHarga.text.toString().toIntOrNull() ?: 0
            val deskripsi = etDeskripsi.text.toString().trim()

            if (namaObat.isEmpty()) {
                etNamaObat.error = "Nama obat tidak boleh kosong"
                return@setOnClickListener
            }

            if (jenisObat.isEmpty()) {
                etJenisObat.error = "Jenis obat tidak boleh kosong"
                return@setOnClickListener
            }

            val obatData = hashMapOf(
                "namaObat" to namaObat,
                "jenisObat" to jenisObat,
                "stok" to stok,
                "harga" to harga,
                "deskripsi" to deskripsi,
                "isActive" to true
            )

            if (obat == null) {
                // Tambah baru
                db.collection("obat").add(obatData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Obat berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                        loadObatData()
                        dialog.dismiss()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Gagal menambahkan: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // Update
                db.collection("obat").document(obat.id).update(obatData as Map<String, Any>)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Obat berhasil diupdate", Toast.LENGTH_SHORT).show()
                        loadObatData()
                        dialog.dismiss()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Gagal mengupdate: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        dialog.show()
    }

    private fun confirmDelete(obat: Obat) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Obat")
            .setMessage("Apakah Anda yakin ingin menghapus ${obat.namaObat}?")
            .setPositiveButton("Hapus") { _, _ ->
                db.collection("obat").document(obat.id).delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Obat berhasil dihapus", Toast.LENGTH_SHORT).show()
                        loadObatData()
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