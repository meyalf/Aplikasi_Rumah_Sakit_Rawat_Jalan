package com.example.aplikasi_rumah_sakit_rawat_jalan.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.aplikasi_rumah_sakit_rawat_jalan.MainActivity
import com.example.aplikasi_rumah_sakit_rawat_jalan.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Nomor telepon RS (ganti dengan nomor asli)
    private val phoneNumber = "081234567890"

    // Nomor WhatsApp RS (format internasional tanpa +)
    private val whatsappNumber = "6281234567890"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Tombol Poli Gigi
        binding.cardPoliGigi.setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(PoliGigiFragment())
        }

        // Tombol Poli Mata
        binding.cardPoliMata.setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(PoliMataFragment())
        }

        // Tombol Antrian Saya
        binding.cardAntrianSaya.setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(AppointmentFragment())
        }

        // Tombol Hubungi RS - tampilkan dialog pilihan
        binding.cardHubungiRS.setOnClickListener {
            showContactOptions()
        }
    }

    private fun showContactOptions() {
        val options = arrayOf("Telepon", "WhatsApp")
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Hubungi RS")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> callPhone()
                    1 -> openWhatsApp()
                }
            }
            .show()
    }

    private fun callPhone() {
        try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phoneNumber")
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Tidak dapat membuka telepon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openWhatsApp() {
        try {
            val message = "Halo, saya ingin bertanya tentang layanan RS"
            val url = "https://wa.me/$whatsappNumber?text=${Uri.encode(message)}"
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "WhatsApp tidak terinstall", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
