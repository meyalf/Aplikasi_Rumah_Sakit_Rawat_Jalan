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
import com.example.aplikasi_rumah_sakit_rawat_jalan.adapter.HasilPemeriksaanAdapter
import com.example.aplikasi_rumah_sakit_rawat_jalan.model.HasilPemeriksaan
import com.google.firebase.firestore.FirebaseFirestore

class HasilPemeriksaanPasienFragment : Fragment() {

    private lateinit var rvHasil: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var hasilAdapter: HasilPemeriksaanAdapter

    private val db = FirebaseFirestore.getInstance()
    private val listHasil = mutableListOf<HasilPemeriksaan>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_hasil_pemeriksaan_pasien, container, false)

        rvHasil = view.findViewById(R.id.rv_hasil)
        tvEmpty = view.findViewById(R.id.tv_empty)

        rvHasil.layoutManager = LinearLayoutManager(requireContext())

        loadHasilPemeriksaan()

        return view
    }

    private fun loadHasilPemeriksaan() {
        // Ambil UID pasien yang login dari MainActivity
        val userId = (activity as? MainActivity)?.getUserId() ?: ""

        Log.d("HasilPemeriksaan", "=== QUERY PARAMETER ===")
        Log.d("HasilPemeriksaan", "User ID (UID): $userId")
        Log.d("HasilPemeriksaan", "======================")

        if (userId.isEmpty()) {
            Toast.makeText(requireContext(), "User ID tidak ditemukan!", Toast.LENGTH_SHORT).show()
            return
        }

        // STEP 1: Ambil data user untuk dapetin nama pasien
        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { userDoc ->
                val namaPasien = userDoc.getString("nama") ?: ""

                Log.d("HasilPemeriksaan", "Nama Pasien: $namaPasien")

                if (namaPasien.isEmpty()) {
                    Toast.makeText(requireContext(), "Data pasien tidak ditemukan!", Toast.LENGTH_SHORT).show()
                    tampilkanData()
                    return@addOnSuccessListener
                }

                // STEP 2: Query hasil pemeriksaan berdasarkan nama pasien
                db.collection("hasil_pemeriksaan")
                    .whereEqualTo("namaPasien", namaPasien)
                    .get()
                    .addOnSuccessListener { documents ->
                        Log.d("HasilPemeriksaan", "Query berhasil! Jumlah dokumen: ${documents.size()}")

                        listHasil.clear()

                        for (document in documents) {
                            Log.d("HasilPemeriksaan", "Dokumen ID: ${document.id}")

                            val hasil = HasilPemeriksaan(
                                id = document.getString("id") ?: "",
                                pendaftaranId = document.getString("pendaftaranId") ?: "",
                                pasienId = document.getString("pasienId") ?: "",
                                namaPasien = document.getString("namaPasien") ?: "",
                                dokterId = document.getString("dokterId") ?: "",
                                namaDokter = document.getString("namaDokter") ?: "",
                                poli = document.getString("poli") ?: "",
                                tanggalPemeriksaan = document.getString("tanggalPemeriksaan") ?: "",
                                diagnosis = document.getString("diagnosis") ?: "",
                                tindakan = document.getString("tindakan") ?: "",
                                resep = document.getString("resep") ?: "",
                                catatanDokter = document.getString("catatanDokter") ?: "",
                                waktuPemeriksaan = document.getLong("waktuPemeriksaan") ?: 0L
                            )
                            listHasil.add(hasil)
                        }

                        // Sort dari yang terbaru
                        listHasil.sortByDescending { it.waktuPemeriksaan }

                        Log.d("HasilPemeriksaan", "Total hasil di list: ${listHasil.size}")

                        tampilkanData()
                    }
                    .addOnFailureListener { e ->
                        Log.e("HasilPemeriksaan", "Query hasil pemeriksaan gagal: ${e.message}")
                        Toast.makeText(requireContext(), "Gagal memuat data: ${e.message}", Toast.LENGTH_SHORT).show()
                        tampilkanData()
                    }
            }
            .addOnFailureListener { e ->
                Log.e("HasilPemeriksaan", "Gagal ambil data user: ${e.message}")
                Toast.makeText(requireContext(), "Gagal memuat data user!", Toast.LENGTH_SHORT).show()
                tampilkanData()
            }
    }

    private fun tampilkanData() {
        if (listHasil.isEmpty()) {
            tvEmpty.visibility = View.VISIBLE
            rvHasil.visibility = View.GONE
        } else {
            tvEmpty.visibility = View.GONE
            rvHasil.visibility = View.VISIBLE

            hasilAdapter = HasilPemeriksaanAdapter(listHasil)
            rvHasil.adapter = hasilAdapter
        }
    }
}