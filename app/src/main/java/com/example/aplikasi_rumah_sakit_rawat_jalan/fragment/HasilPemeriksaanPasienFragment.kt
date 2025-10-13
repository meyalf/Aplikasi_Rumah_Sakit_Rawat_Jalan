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
import com.google.firebase.firestore.Query

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
        // Ambil ID pasien yang login dari MainActivity
        val pasienId = (activity as? MainActivity)?.getUserId() ?: ""

        Log.d("HasilPemeriksaan", "Pasien ID: $pasienId")

        if (pasienId.isEmpty()) {
            Toast.makeText(requireContext(), "User ID tidak ditemukan!", Toast.LENGTH_SHORT).show()
            return
        }

        // Query ke Firestore
        db.collection("hasil_pemeriksaan")
            .whereEqualTo("pasienId", pasienId)
            .orderBy("waktuPemeriksaan", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                Log.d("HasilPemeriksaan", "Query berhasil! Jumlah dokumen: ${documents.size()}")

                listHasil.clear()

                for (document in documents) {
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

                tampilkanData()
            }
            .addOnFailureListener { e ->
                Log.e("HasilPemeriksaan", "Query gagal: ${e.message}")
                Toast.makeText(requireContext(), "Gagal memuat data: ${e.message}", Toast.LENGTH_SHORT).show()
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