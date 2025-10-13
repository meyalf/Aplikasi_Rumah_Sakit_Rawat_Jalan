package com.example.aplikasi_rumah_sakit_rawat_jalan.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.aplikasi_rumah_sakit_rawat_jalan.R
import com.example.aplikasi_rumah_sakit_rawat_jalan.model.HasilPemeriksaan
import com.example.aplikasi_rumah_sakit_rawat_jalan.model.Pendaftaran
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class FormPemeriksaanFragment : Fragment() {

    private lateinit var tvNamaPasien: TextView
    private lateinit var tvNoTelepon: TextView
    private lateinit var tvKeluhan: TextView
    private lateinit var etDiagnosis: TextInputEditText
    private lateinit var etTindakan: TextInputEditText
    private lateinit var etResep: TextInputEditText
    private lateinit var etCatatan: TextInputEditText
    private lateinit var btnSimpan: Button

    private val db = FirebaseFirestore.getInstance()
    private var pendaftaran: Pendaftaran? = null

    companion object {
        private const val ARG_PENDAFTARAN = "pendaftaran"

        fun newInstance(pendaftaran: Pendaftaran): FormPemeriksaanFragment {
            val fragment = FormPemeriksaanFragment()
            val bundle = Bundle()
            bundle.putSerializable(ARG_PENDAFTARAN, pendaftaran)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_form_pemeriksaan, container, false)

        // Inisialisasi view
        tvNamaPasien = view.findViewById(R.id.tv_nama_pasien)
        tvNoTelepon = view.findViewById(R.id.tv_no_telepon)
        tvKeluhan = view.findViewById(R.id.tv_keluhan)
        etDiagnosis = view.findViewById(R.id.et_diagnosis)
        etTindakan = view.findViewById(R.id.et_tindakan)
        etResep = view.findViewById(R.id.et_resep)
        etCatatan = view.findViewById(R.id.et_catatan)
        btnSimpan = view.findViewById(R.id.btn_simpan)

        // Ambil data pendaftaran dari arguments
        pendaftaran = arguments?.getSerializable(ARG_PENDAFTARAN) as? Pendaftaran

        // Tampilkan data pasien
        pendaftaran?.let {
            tvNamaPasien.text = it.namaPasien
            tvNoTelepon.text = it.noTelepon
            tvKeluhan.text = it.keluhan
        }

        btnSimpan.setOnClickListener {
            simpanHasilPemeriksaan()
        }

        return view
    }

    private fun simpanHasilPemeriksaan() {
        val diagnosis = etDiagnosis.text.toString().trim()
        val tindakan = etTindakan.text.toString().trim()
        val resep = etResep.text.toString().trim()
        val catatan = etCatatan.text.toString().trim()

        if (diagnosis.isEmpty() || tindakan.isEmpty()) {
            Toast.makeText(requireContext(), "Diagnosis dan Tindakan harus diisi!", Toast.LENGTH_SHORT).show()
            return
        }

        pendaftaran?.let { pend ->
            val hasilId = db.collection("hasil_pemeriksaan").document().id
            val tanggalPemeriksaan = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            val hasilPemeriksaan = HasilPemeriksaan(
                id = hasilId,
                pendaftaranId = pend.id,
                pasienId = pend.pasienId,
                namaPasien = pend.namaPasien,
                dokterId = pend.dokterId,
                namaDokter = pend.namaDokter,
                poli = pend.poli,
                tanggalPemeriksaan = tanggalPemeriksaan,
                diagnosis = diagnosis,
                tindakan = tindakan,
                resep = resep,
                catatanDokter = catatan,
                waktuPemeriksaan = System.currentTimeMillis()
            )

            // Simpan ke Firestore
            db.collection("hasil_pemeriksaan")
                .document(hasilId)
                .set(hasilPemeriksaan)
                .addOnSuccessListener {
                    // Update status pendaftaran jadi "selesai"
                    db.collection("pendaftaran")
                        .document(pend.id)
                        .update("status", "selesai")
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Hasil pemeriksaan berhasil disimpan!", Toast.LENGTH_SHORT).show()
                            requireActivity().supportFragmentManager.popBackStack()
                        }
                        .addOnFailureListener { e ->
                            Log.e("FormPemeriksaan", "Gagal update status: ${e.message}")
                        }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Gagal menyimpan: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("FormPemeriksaan", "Error: ${e.message}")
                }
        }
    }
}