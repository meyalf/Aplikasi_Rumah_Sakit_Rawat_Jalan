package com.example.aplikasi_rumah_sakit_rawat_jalan

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class ManagePasienActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var rvPasien: RecyclerView
    private lateinit var etSearch: EditText
    private lateinit var btnSearch: Button
    private lateinit var tvTotalPasien: TextView
    private lateinit var llEmptyState: LinearLayout

    private lateinit var pasienAdapter: PasienAdapter
    private var pasienList = mutableListOf<Pasien>()
    private var filteredList = mutableListOf<Pasien>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_pasien)

        supportActionBar?.title = "Kelola Pasien"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = FirebaseFirestore.getInstance()

        initViews()
        setupRecyclerView()
        setupSearch()
        loadPasien()
    }

    private fun initViews() {
        rvPasien = findViewById(R.id.rv_pasien)
        etSearch = findViewById(R.id.et_search)
        btnSearch = findViewById(R.id.btn_search)
        tvTotalPasien = findViewById(R.id.tv_total_pasien)
        llEmptyState = findViewById(R.id.ll_empty_state)
    }

    private fun setupRecyclerView() {
        pasienAdapter = PasienAdapter(
            filteredList,
            onEditClick = { pasien -> showEditDialog(pasien) },
            onDeleteClick = { pasien -> showDeleteConfirmation(pasien) }
        )

        rvPasien.apply {
            layoutManager = LinearLayoutManager(this@ManagePasienActivity)
            adapter = pasienAdapter
        }
    }

    private fun setupSearch() {
        btnSearch.setOnClickListener {
            filterPasien(etSearch.text.toString())
        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterPasien(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun loadPasien() {
        db.collection("users")
            .whereEqualTo("role", "pasien")
            .get()
            .addOnSuccessListener { documents ->
                pasienList.clear()
                for (document in documents) {
                    val pasien = Pasien(
                        id = document.id,
                        nama = document.getString("nama") ?: "",
                        nik = document.getString("nik") ?: "",
                        email = document.getString("email") ?: "",
                        noHp = document.getString("noHp") ?: "",
                        alamat = document.getString("alamat") ?: "",
                        tanggalLahir = document.getString("tanggalLahir") ?: "",
                        jenisKelamin = document.getString("jenisKelamin") ?: "",
                        role = "pasien"
                    )
                    pasienList.add(pasien)
                }

                filteredList.clear()
                filteredList.addAll(pasienList)
                pasienAdapter.updateData(filteredList)
                updateUI()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal memuat data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun filterPasien(query: String) {
        filteredList.clear()
        if (query.isEmpty()) {
            filteredList.addAll(pasienList)
        } else {
            val searchQuery = query.lowercase()
            filteredList.addAll(
                pasienList.filter {
                    it.nama.lowercase().contains(searchQuery) ||
                            it.nik.contains(searchQuery) ||
                            it.email.lowercase().contains(searchQuery)
                }
            )
        }
        pasienAdapter.updateData(filteredList)
        updateUI()
    }

    private fun updateUI() {
        tvTotalPasien.text = "Total Pasien: ${filteredList.size}"

        if (filteredList.isEmpty()) {
            rvPasien.visibility = View.GONE
            llEmptyState.visibility = View.VISIBLE
        } else {
            rvPasien.visibility = View.VISIBLE
            llEmptyState.visibility = View.GONE
        }
    }

    private fun showEditDialog(pasien: Pasien) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_pasien, null)

        val etNama = dialogView.findViewById<EditText>(R.id.et_edit_nama)
        val etNik = dialogView.findViewById<EditText>(R.id.et_edit_nik)
        val etNoHp = dialogView.findViewById<EditText>(R.id.et_edit_no_hp)
        val etAlamat = dialogView.findViewById<EditText>(R.id.et_edit_alamat)

        etNama.setText(pasien.nama)
        etNik.setText(pasien.nik)
        etNoHp.setText(pasien.noHp)
        etAlamat.setText(pasien.alamat)

        AlertDialog.Builder(this)
            .setTitle("Edit Data Pasien")
            .setView(dialogView)
            .setPositiveButton("Simpan") { _, _ ->
                val nama = etNama.text.toString().trim()
                val nik = etNik.text.toString().trim()
                val noHp = etNoHp.text.toString().trim()
                val alamat = etAlamat.text.toString().trim()

                // Validasi input
                if (nama.isEmpty() || nik.isEmpty() || noHp.isEmpty() || alamat.isEmpty()) {
                    Toast.makeText(this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (nik.length != 16) {
                    Toast.makeText(this, "NIK harus 16 digit!", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val updatedData = hashMapOf(
                    "nama" to nama,
                    "nik" to nik,
                    "noHp" to noHp,
                    "alamat" to alamat
                )

                updatePasien(pasien.id, updatedData)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun updatePasien(pasienId: String, data: HashMap<String, String>) {
        val updateData = hashMapOf<String, Any>(
            "nama" to data["nama"]!!,
            "nik" to data["nik"]!!,
            "noHp" to data["noHp"]!!,
            "alamat" to data["alamat"]!!
        )

        db.collection("users").document(pasienId)
            .update(updateData)
            .addOnSuccessListener {
                Toast.makeText(this, "Data pasien berhasil diupdate", Toast.LENGTH_SHORT).show()
                loadPasien()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal update data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showDeleteConfirmation(pasien: Pasien) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Pasien")
            .setMessage("Apakah Anda yakin ingin menghapus pasien ${pasien.nama}?\n\nPeringatan: Semua data appointment pasien ini juga akan terhapus!")
            .setPositiveButton("Hapus") { _, _ ->
                deletePasien(pasien)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun deletePasien(pasien: Pasien) {
        // Hapus data pasien dari collection users
        db.collection("users").document(pasien.id)
            .delete()
            .addOnSuccessListener {
                // Hapus juga semua appointment pasien ini
                deleteAppointmentsByPasien(pasien.id)
                Toast.makeText(this, "Pasien ${pasien.nama} berhasil dihapus", Toast.LENGTH_SHORT).show()
                loadPasien()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal menghapus pasien: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteAppointmentsByPasien(pasienId: String) {
        db.collection("appointments")
            .whereEqualTo("userId", pasienId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.delete()
                }
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}