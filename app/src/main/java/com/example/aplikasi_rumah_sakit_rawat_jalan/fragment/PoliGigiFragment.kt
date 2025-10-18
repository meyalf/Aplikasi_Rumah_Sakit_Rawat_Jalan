package com.example.aplikasi_rumah_sakit_rawat_jalan.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplikasi_rumah_sakit_rawat_jalan.MainActivity
import com.example.aplikasi_rumah_sakit_rawat_jalan.R
import com.example.aplikasi_rumah_sakit_rawat_jalan.adapter.DokterAdapter
import com.example.aplikasi_rumah_sakit_rawat_jalan.databinding.FragmentPoliGigiBinding
import com.example.aplikasi_rumah_sakit_rawat_jalan.model.Dokter

class PoliGigiFragment : Fragment() {

    private var _binding: FragmentPoliGigiBinding? = null
    private val binding get() = _binding!!

    private lateinit var dokterAdapter: DokterAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPoliGigiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val dokterList = getDokterGigi()

        dokterAdapter = DokterAdapter(dokterList) { dokter ->
            val fragment = AmbilAntrianFragment.newInstance(dokter)
            (activity as? MainActivity)?.navigateToFragment(fragment)
        }

        binding.rvDokterGigi.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = dokterAdapter
        }
    }

    private fun getDokterGigi(): List<Dokter> {
        return listOf(
            Dokter(
                id = 1,
                nama = "drg. Ahmad Santoso",
                spesialis = "Dokter Gigi Umum",
                poliklinik = "Poli Gigi",
                hari = listOf("Senin", "Rabu"),
                jamPraktek = "08:00 - 14:00",
                foto = R.drawable.ic_doctor,
                status = "Tersedia"
            ),
            Dokter(
                id = 2,
                nama = "drg. Sari Wijayanti",
                spesialis = "Spesialis Ortodontik",
                poliklinik = "Poli Gigi",
                hari = listOf("Selasa", "Kamis"),
                jamPraktek = "13:00 - 19:00",
                foto = R.drawable.ic_doctor,
                status = "Tersedia"
            ),
            Dokter(
                id = 3,
                nama = "drg. Budi Prasetyo",
                spesialis = "Spesialis Bedah Mulut",
                poliklinik = "Poli Gigi",
                hari = listOf("Senin", "Jumat"),
                jamPraktek = "07:30 - 13:30",
                foto = R.drawable.ic_doctor,
                status = "Tersedia"
            ),
            Dokter(
                id = 4,
                nama = "drg. Maya Kusuma",
                spesialis = "Spesialis Periodonsia",
                poliklinik = "Poli Gigi",
                hari = listOf("Rabu", "Sabtu"),
                jamPraktek = "14:00 - 20:00",
                foto = R.drawable.ic_doctor,
                status = "Tersedia"
            ),
            Dokter(
                id = 5,
                nama = "drg. Rina Amelia",
                spesialis = "Spesialis Gigi Anak",
                poliklinik = "Poli Gigi",
                hari = listOf("Kamis", "Sabtu"),
                jamPraktek = "09:00 - 15:00",
                foto = R.drawable.ic_doctor,
                status = "Tersedia"
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}