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
import com.example.aplikasi_rumah_sakit_rawat_jalan.databinding.FragmentPoliMataBinding
import com.example.aplikasi_rumah_sakit_rawat_jalan.model.Dokter

class PoliMataFragment : Fragment() {

    private var _binding: FragmentPoliMataBinding? = null
    private val binding get() = _binding!!

    private lateinit var dokterAdapter: DokterAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPoliMataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val dokterList = getDokterMata()

        dokterAdapter = DokterAdapter(dokterList) { dokter ->
            val fragment = AmbilAntrianFragment.newInstance(dokter)
            (activity as? MainActivity)?.navigateToFragment(fragment)
        }

        binding.rvDokterMata.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = dokterAdapter
        }
    }

    private fun getDokterMata(): List<Dokter> {
        return listOf(
            Dokter(
                id = 6,
                nama = "Dr. Sari Dewi, Sp.M",
                spesialis = "Dokter Mata Umum",
                poliklinik = "Poli Mata",
                hari = listOf("Senin", "Rabu"),
                jamPraktek = "08:00 - 14:00",
                foto = R.drawable.ic_doctor,
                status = "Tersedia"
            ),
            Dokter(
                id = 7,
                nama = "Dr. Ahmad Rizki, Sp.M",
                spesialis = "Spesialis Katarak",
                poliklinik = "Poli Mata",
                hari = listOf("Selasa", "Kamis"),
                jamPraktek = "10:00 - 16:00",
                foto = R.drawable.ic_doctor,
                status = "Tersedia"
            ),
            Dokter(
                id = 8,
                nama = "Dr. Budi Hermawan, Sp.M",
                spesialis = "Spesialis Retina",
                poliklinik = "Poli Mata",
                hari = listOf("Senin", "Jumat"),
                jamPraktek = "07:00 - 13:00",
                foto = R.drawable.ic_doctor,
                status = "Tersedia"
            ),
            Dokter(
                id = 9,
                nama = "Dr. Maya Sari, Sp.M",
                spesialis = "Spesialis Glaukoma",
                poliklinik = "Poli Mata",
                hari = listOf("Rabu", "Jumat"),
                jamPraktek = "13:00 - 19:00",
                foto = R.drawable.ic_doctor,
                status = "Tersedia"
            ),
            Dokter(
                id = 10,
                nama = "Dr. Rina Kartika, Sp.M",
                spesialis = "Spesialis Pediatrik",
                poliklinik = "Poli Mata",
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