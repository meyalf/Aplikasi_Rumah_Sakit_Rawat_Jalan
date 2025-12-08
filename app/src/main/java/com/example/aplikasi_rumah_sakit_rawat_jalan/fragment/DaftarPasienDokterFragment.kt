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
        val mainActivity = activity as? MainActivity
        val userId = mainActivity?.getUserId() ?: ""
        val userName = mainActivity?.getUserName() ?: ""
        val userRole = mainActivity?.getUserRole() ?: ""

        Log.d("DaftarPasien", "=== INFO USER DARI MAINACTIVITY ===")
        Log.d("DaftarPasien", "User ID: '$userId'")
        Log.d("DaftarPasien", "User Name: '$userName'")
        Log.d("DaftarPasien", "User Role: '$userRole'")
        Log.d("DaftarPasien", "===================================")

        // Format tanggal hari ini
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayString = dateFormat.format(Date())

        Log.d("DaftarPasien", "Tanggal hari ini: $todayString")
        Log.d("DaftarPasien", "")

        // Query SEMUA appointments untuk debugging
        Log.d("DaftarPasien", "=== QUERY KE FIRESTORE ===")
        db.collection("appointments")
            .get()
            .addOnSuccessListener { documents ->
                Log.d("DaftarPasien", "✅ Query berhasil!")
                Log.d("DaftarPasien", "Total dokumen: ${documents.size()}")
                Log.d("DaftarPasien", "")

                if (documents.isEmpty) {
                    Log.d("DaftarPasien", "❌ Collection 'appointments' KOSONG!")
                } else {
                    Log.d("DaftarPasien", "=== ISI SEMUA APPOINTMENTS ===")

                    var matchCount = 0

                    for ((index, document) in documents.withIndex()) {
                        val docId = document.id
                        val namaPasien = document.getString("namaPasien") ?: "null"
                        val namaDokter = document.getString("namaDokter") ?: "null"
                        val dokterId = document.getString("dokterId") ?: "null"
                        val tanggal = document.getString("tanggalKunjungan") ?: "null"
                        val status = document.getString("status") ?: "null"
                        val nomorAntrian = document.getLong("nomorAntrian")?.toInt() ?: 0

                        Log.d("DaftarPasien", "--- Document ${index + 1} ---")
                        Log.d("DaftarPasien", "Doc ID: $docId")
                        Log.d("DaftarPasien", "Pasien: $namaPasien")
                        Log.d("DaftarPasien", "Dokter: '$namaDokter'")
                        Log.d("DaftarPasien", "DokterId: '$dokterId'")
                        Log.d("DaftarPasien", "Tanggal: '$tanggal'")
                        Log.d("DaftarPasien", "Status: $status")
                        Log.d("DaftarPasien", "Nomor: $nomorAntrian")

                        // Cek apakah match dengan kriteria
                        val matchNama = namaDokter == userName
                        val matchId = dokterId == userId
                        val matchTanggal = tanggal == todayString

                        Log.d("DaftarPasien", "Match Nama: $matchNama (DB: '$namaDokter' vs Login: '$userName')")
                        Log.d("DaftarPasien", "Match ID: $matchId (DB: '$dokterId' vs Login: '$userId')")
                        Log.d("DaftarPasien", "Match Tanggal: $matchTanggal (DB: '$tanggal' vs Today: '$todayString')")

                        if (matchNama || matchId) {
                            Log.d("DaftarPasien", "✅ MATCHED!")
                            matchCount++
                        } else {
                            Log.d("DaftarPasien", "❌ NOT MATCHED")
                        }
                        Log.d("DaftarPasien", "")
                    }

                    Log.d("DaftarPasien", "=== SUMMARY ===")
                    Log.d("DaftarPasien", "Total appointments: ${documents.size()}")
                    Log.d("DaftarPasien", "Matched appointments: $matchCount")
                    Log.d("DaftarPasien", "===============")
                }

                // Tampilkan SEMUA data dulu (tanpa filter) untuk testing
                listPasien.clear()

                for (document in documents) {
                    val pendaftaran = Pendaftaran(
                        id = document.id,
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
                        waktuDaftar = document.getLong("tanggalDaftar") ?: 0L
                    )
                    listPasien.add(pendaftaran)
                }

                listPasien.sortBy { it.nomorAntrian }

                Log.d("DaftarPasien", "List Pasien size sebelum tampil: ${listPasien.size}")

                tampilkanData()
            }
            .addOnFailureListener { e ->
                Log.e("DaftarPasien", "❌ Query GAGAL!")
                Log.e("DaftarPasien", "Error: ${e.message}")
                e.printStackTrace()
                Toast.makeText(requireContext(), "Gagal memuat data: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun tampilkanData() {
        Log.d("DaftarPasien", "=== TAMPILKAN DATA ===")
        Log.d("DaftarPasien", "List size: ${listPasien.size}")

        if (listPasien.isEmpty()) {
            Log.d("DaftarPasien", "List KOSONG - tampilkan empty state")
            tvEmpty.visibility = View.VISIBLE
            rvPasien.visibility = View.GONE
            tvJumlahPasien.text = "Tidak ada pasien hari ini"
        } else {
            Log.d("DaftarPasien", "List ADA ISI - tampilkan RecyclerView")
            tvEmpty.visibility = View.GONE
            rvPasien.visibility = View.VISIBLE
            tvJumlahPasien.text = "Total Pasien: ${listPasien.size}"

            pasienAdapter = PasienDokterAdapter(listPasien) { pasien ->
                Log.d("DaftarPasien", "Clicked: ${pasien.namaPasien}")
                val fragment = FormPemeriksaanFragment.newInstance(pasien)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit()
            }

            rvPasien.adapter = pasienAdapter
            Log.d("DaftarPasien", "Adapter set ke RecyclerView")
        }

        Log.d("DaftarPasien", "======================")
    }
}