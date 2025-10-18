package com.example.aplikasi_rumah_sakit_rawat_jalan

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasi_rumah_sakit_rawat_jalan.adapter.AdminDokterAdapter
import com.example.aplikasi_rumah_sakit_rawat_jalan.model.Dokter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class ManageDokterActivity : AppCompatActivity() {

    private lateinit var rvDokter: RecyclerView
    private lateinit var fabAddDokter: FloatingActionButton
    private lateinit var spinnerFilterPoli: Spinner
    private lateinit var adapter: AdminDokterAdapter
    private lateinit var db: FirebaseFirestore

    private val dokterList = mutableListOf<Dokter>()
    private val allDokterList = mutableListOf<Dokter>()

    private val poliList = listOf("Semua Poli", "Poli Gigi", "Poli Mata")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_dokter)

        supportActionBar?.title = "Kelola Data Dokter"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = FirebaseFirestore.getInstance()

        initViews()
        setupFilterSpinner()
        loadDokterData()
    }

    private fun initViews() {
        rvDokter = findViewById(R.id.rv_dokter)
        fabAddDokter = findViewById(R.id.fab_add_dokter)
        spinnerFilterPoli = findViewById(R.id.spinner_filter_poli)

        rvDokter.layoutManager = LinearLayoutManager(this)

        adapter = AdminDokterAdapter(dokterList) { dokter, action ->
            when (action) {
                "edit" -> showEditDokterDialog(dokter)
                "delete" -> confirmDeleteDokter(dokter)
            }
        }

        rvDokter.adapter = adapter

        fabAddDokter.setOnClickListener {
            showAddDokterDialog()
        }
    }

    private fun setupFilterSpinner() {
        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            poliList
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFilterPoli.adapter = spinnerAdapter

        spinnerFilterPoli.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                filterDokterByPoli(poliList[position])
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
    }

    private fun filterDokterByPoli(poli: String) {
        val filteredList = if (poli == "Semua Poli") {
            allDokterList
        } else {
            allDokterList.filter { it.poliklinik == poli }
        }
        adapter.updateList(filteredList)
    }

    private fun loadDokterData() {
        db.collection("dokter")
            .get()
            .addOnSuccessListener { documents ->
                dokterList.clear()
                allDokterList.clear()

                var idCounter = 1
                for (document in documents) {
                    val nama = document.getString("nama") ?: document.getString("namaDokter") ?: ""
                    val spesialis = document.getString("spesialis") ?: document.getString("spesialisasi") ?: ""
                    val poli = document.getString("poliklinik") ?: document.getString("poli") ?: ""
                    val status = document.getString("status") ?: "Tersedia"

                    val dokter = Dokter(
                        id = idCounter++,
                        nama = nama,
                        spesialis = spesialis,
                        poliklinik = poli,
                        hari = emptyList(),
                        jamPraktek = "",
                        foto = 0,
                        status = status
                    )

                    dokterList.add(dokter)
                    allDokterList.add(dokter)
                }

                adapter.notifyDataSetChanged()

                if (dokterList.isEmpty()) {
                    Toast.makeText(this, "Belum ada data dokter", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal memuat data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showAddDokterDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_add_dokter)
        dialog.window?.setLayout(
            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val etNamaDokter = dialog.findViewById<EditText>(R.id.et_nama_dokter)
        val spinnerPoli = dialog.findViewById<Spinner>(R.id.spinner_poli)
        val etSpesialisasi = dialog.findViewById<EditText>(R.id.et_spesialisasi)
        val etNoStr = dialog.findViewById<EditText>(R.id.et_no_str)
        val btnSave = dialog.findViewById<Button>(R.id.btn_save_dokter)
        val btnCancel = dialog.findViewById<Button>(R.id.btn_cancel)

        val poliAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            listOf("Poli Gigi", "Poli Mata")
        )
        poliAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPoli.adapter = poliAdapter

        btnSave.setOnClickListener {
            val nama = etNamaDokter.text.toString().trim()
            val poli = spinnerPoli.selectedItem?.toString() ?: ""
            val spesialis = etSpesialisasi.text.toString().trim()

            if (nama.isEmpty() || spesialis.isEmpty()) {
                Toast.makeText(this, "Nama dan spesialisasi harus diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dokter = hashMapOf(
                "nama" to nama,
                "poliklinik" to poli,
                "spesialis" to spesialis,
                "status" to "Tersedia",
                "hari" to emptyList<String>(),
                "jamPraktek" to "",
                "foto" to 0
            )

            db.collection("dokter")
                .add(dokter)
                .addOnSuccessListener {
                    Toast.makeText(this, "Dokter berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                    loadDokterData()
                    dialog.dismiss()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Gagal menambahkan: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showEditDokterDialog(dokter: Dokter) {
        Toast.makeText(this, "Edit dokter: ${dokter.nama}", Toast.LENGTH_SHORT).show()
    }

    private fun confirmDeleteDokter(dokter: Dokter) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Dokter")
            .setMessage("Apakah Anda yakin ingin menghapus dokter ${dokter.nama}?")
            .setPositiveButton("Hapus") { _, _ ->
                deleteDokter(dokter)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun deleteDokter(dokter: Dokter) {
        // Cari dokter by nama karena ID adalah Int
        db.collection("dokter")
            .whereEqualTo("nama", dokter.nama)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.delete()
                }
                Toast.makeText(this, "Dokter berhasil dihapus", Toast.LENGTH_SHORT).show()
                loadDokterData()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal menghapus: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}