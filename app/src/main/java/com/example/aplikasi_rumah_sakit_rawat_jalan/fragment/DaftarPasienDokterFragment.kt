package com.example.aplikasi_rumah_sakit_rawat_jalan.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasi_rumah_sakit_rawat_jalan.MainActivity
import com.example.aplikasi_rumah_sakit_rawat_jalan.R
import com.example.aplikasi_rumah_sakit_rawat_jalan.adapter.PasienDokterAdapter
import com.example.aplikasi_rumah_sakit_rawat_jalan.model.Pendaftaran
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*

class DaftarPasienDokterFragment : Fragment() {

    private lateinit var tvTanggal: TextView
    private lateinit var tvJumlahPasien: TextView
    private lateinit var rvPasien: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var pasienAdapter: PasienDokterAdapter

    private val db = FirebaseFirestore.getInstance()
    private val listPasien = mutableListOf<Pendaftaran>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_daftar_pasien_dokter, container, false)

        tvTanggal = view.findViewById(R.id.tv_tanggal)
        tvJumlahPasien = view.findViewById(R.id.tv_jumlah_pasien)
        rvPasien = view.findViewById(R.id.rv_pasien)
        tvEmpty = view.findViewById(R.id.tv_empty)

        rvPasien.layoutManager = LinearLayoutManager(requireContext())

        // Tampilkan tanggal hari ini
        val sdf = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
        tvTanggal.text = sdf.format(Date())

        loadDaftarPasien()

        return view
    }

    private fun loadDaftarPasien() {
        // Ambil tanggal hari ini dalam format yyyy-MM-dd
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // Ambil ID dokter yang login dari MainActivity
        val dokterId = (activity as? MainActivity)?.getUserId() ?: ""

        Log.d("DaftarPasien", "=== QUERY PARAMETER ===")
        Log.d("DaftarPasien", "Dokter ID: $dokterId")
        Log.d("DaftarPasien", "Tanggal: $today")
        Log.d("DaftarPasien", "======================")

        if (dokterId.isEmpty()) {
            Toast.makeText(requireContext(), "User ID tidak ditemukan!", Toast.LENGTH_SHORT).show()
            return
        }

        // Query ke Firestore
        db.collection("pendaftaran")
            .whereEqualTo("dokterId", dokterId)
            .whereEqualTo("tanggalKunjungan", today)
            .orderBy("nomorAntrian", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { documents ->
                Log.d("DaftarPasien", "Query berhasil! Jumlah dokumen: ${documents.size()}")

                listPasien.clear()

                for (document in documents) {
                    Log.d("DaftarPasien", "Dokumen ID: ${document.id}")
                    Log.d("DaftarPasien", "Data: ${document.data}")

                    val pendaftaran = Pendaftaran(
                        id = document.getString("id") ?: "",
                        pasienId = document.getString("pasienId") ?: "",
                        namaPasien = document.getString("namaPasien") ?: "",
                        noTelepon = document.getString("noTelepon") ?: "",
                        dokterId = document.getString("dokterId") ?: "",
                        namaDokter = document.getString("namaDokter") ?: "",
                        poli = document.getString("poli") ?: "",
                        tanggalKunjungan = document.getString("tanggalKunjungan") ?: "",
                        nomorAntrian = document.getLong("nomorAntrian")?.toInt() ?: 0,
                        keluhan = document.getString("keluhan") ?: "",
                        status = document.getString("status") ?: "menunggu",
                        waktuDaftar = document.getTimestamp("waktuDaftar")?.toDate()?.time ?: 0L
                    )
                    listPasien.add(pendaftaran)
                }

                tampilkanData()
            }
            .addOnFailureListener { e ->
                Log.e("DaftarPasien", "Query gagal: ${e.message}")
                Toast.makeText(requireContext(), "Gagal memuat data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun tampilkanData() {
        if (listPasien.isEmpty()) {
            tvEmpty.visibility = View.VISIBLE
            rvPasien.visibility = View.GONE
            tvJumlahPasien.text = "Tidak ada pasien hari ini"
        } else {
            tvEmpty.visibility = View.GONE
            rvPasien.visibility = View.VISIBLE
            tvJumlahPasien.text = "Total Pasien: ${listPasien.size}"

            pasienAdapter = PasienDokterAdapter(listPasien) { pasien ->
                // Navigasi ke form pemeriksaan
                val fragment = FormPemeriksaanFragment.newInstance(pasien)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit()
            }

            rvPasien.adapter = pasienAdapter
        }
    }
}