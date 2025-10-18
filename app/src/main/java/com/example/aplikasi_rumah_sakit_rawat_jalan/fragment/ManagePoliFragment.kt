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
import com.example.aplikasi_rumah_sakit_rawat_jalan.adapter.PoliAdapter
import com.example.aplikasi_rumah_sakit_rawat_jalan.model.Poli
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore

class ManagePoliActivity : AppCompatActivity() {

    private lateinit var rvPoli: RecyclerView
    private lateinit var btnTambahPoli: MaterialButton
    private lateinit var adapter: PoliAdapter
    private val poliList = mutableListOf<Poli>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_poli)

        supportActionBar?.title = "Kelola Poli"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initViews()
        setupRecyclerView()
        loadPoliData()
        setupListeners()

        // Tambah data default jika belum ada
        checkAndAddDefaultPoli()
    }

    private fun initViews() {
        rvPoli = findViewById(R.id.rv_poli)
        btnTambahPoli = findViewById(R.id.btn_tambah_poli)
    }

    private fun setupRecyclerView() {
        adapter = PoliAdapter(poliList,
            onEditClick = { poli -> showAddEditDialog(poli) },
            onDeleteClick = { poli -> confirmDelete(poli) }
        )
        rvPoli.layoutManager = LinearLayoutManager(this)
        rvPoli.adapter = adapter
    }

    private fun setupListeners() {
        btnTambahPoli.setOnClickListener {
            showAddEditDialog(null)
        }
    }

    private fun loadPoliData() {
        db.collection("poli")
            .get()
            .addOnSuccessListener { documents ->
                poliList.clear()
                for (document in documents) {
                    val poli = document.toObject(Poli::class.java)
                    poli.id = document.id
                    poliList.add(poli)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal memuat data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkAndAddDefaultPoli() {
        db.collection("poli").get().addOnSuccessListener { documents ->
            if (documents.isEmpty) {
                // Tambah 2 poli default: Gigi & Mata
                val defaultPoli = listOf(
                    hashMapOf(
                        "namaPoli" to "Poli Gigi",
                        "deskripsi" to "Pelayanan kesehatan gigi dan mulut",
                        "iconName" to "ic_dental",
                        "isActive" to true
                    ),
                    hashMapOf(
                        "namaPoli" to "Poli Mata",
                        "deskripsi" to "Pelayanan kesehatan mata",
                        "iconName" to "ic_eye",
                        "isActive" to true
                    )
                )

                defaultPoli.forEach { data ->
                    db.collection("poli").add(data)
                }

                Toast.makeText(this, "Data poli default berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                loadPoliData()
            }
        }
    }

    private fun showAddEditDialog(poli: Poli?) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_poli, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()

        val tvTitle = dialogView.findViewById<TextView>(R.id.tv_dialog_title)
        val etNamaPoli = dialogView.findViewById<EditText>(R.id.et_nama_poli)
        val etDeskripsi = dialogView.findViewById<EditText>(R.id.et_deskripsi)
        val btnCancel = dialogView.findViewById<Button>(R.id.btn_cancel)
        val btnSave = dialogView.findViewById<Button>(R.id.btn_save)

        // Set data jika edit
        if (poli != null) {
            tvTitle.text = "Edit Poli"
            etNamaPoli.setText(poli.namaPoli)
            etDeskripsi.setText(poli.deskripsi)
        } else {
            tvTitle.text = "Tambah Poli Baru"
        }

        btnCancel.setOnClickListener { dialog.dismiss() }

        btnSave.setOnClickListener {
            val namaPoli = etNamaPoli.text.toString().trim()
            val deskripsi = etDeskripsi.text.toString().trim()

            if (namaPoli.isEmpty()) {
                etNamaPoli.error = "Nama poli tidak boleh kosong"
                return@setOnClickListener
            }

            val poliData = hashMapOf(
                "namaPoli" to namaPoli,
                "deskripsi" to deskripsi,
                "iconName" to "ic_home",
                "isActive" to true
            )

            if (poli == null) {
                // Tambah baru
                db.collection("poli").add(poliData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Poli berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                        loadPoliData()
                        dialog.dismiss()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Gagal menambahkan: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // Update
                db.collection("poli").document(poli.id).update(poliData as Map<String, Any>)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Poli berhasil diupdate", Toast.LENGTH_SHORT).show()
                        loadPoliData()
                        dialog.dismiss()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Gagal mengupdate: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        dialog.show()
    }

    private fun confirmDelete(poli: Poli) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Poli")
            .setMessage("Apakah Anda yakin ingin menghapus ${poli.namaPoli}?")
            .setPositiveButton("Hapus") { _, _ ->
                db.collection("poli").document(poli.id).delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Poli berhasil dihapus", Toast.LENGTH_SHORT).show()
                        loadPoliData()
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
