package com.example.aplikasi_rumah_sakit_rawat_jalan.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplikasi_rumah_sakit_rawat_jalan.adapter.AppointmentAdapter
import com.example.aplikasi_rumah_sakit_rawat_jalan.databinding.FragmentHistoryBinding
import com.example.aplikasi_rumah_sakit_rawat_jalan.model.Appointment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var historyAdapter: AppointmentAdapter
    private val historyList = mutableListOf<Appointment>()

    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        loadHistoryData()
    }

    private fun setupRecyclerView() {
        historyAdapter = AppointmentAdapter(historyList) { appointment, action ->
            // History adalah read-only, tidak ada action
            Toast.makeText(context, "Riwayat: ${appointment.nomorAntrian}", Toast.LENGTH_SHORT).show()
        }

        binding.rvHistory.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = historyAdapter
        }
    }

    private fun loadHistoryData() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            binding.layoutEmpty.visibility = View.VISIBLE
            binding.rvHistory.visibility = View.GONE
            return
        }

        // Ambil appointment yang statusnya "selesai" atau "dibatalkan"
        db.collection("appointments")
            .whereEqualTo("userId", currentUser.uid)
            .get()
            .addOnSuccessListener { documents ->
                historyList.clear()
                for (document in documents) {
                    val appointment = document.toObject(Appointment::class.java)
                    appointment.id = document.id

                    // Filter hanya yang selesai atau dibatalkan
                    val statusLower = appointment.status.lowercase()
                    if (statusLower == "selesai" || statusLower == "dibatalkan") {
                        historyList.add(appointment)
                    }
                }

                // Urutkan dari terbaru
                historyList.sortByDescending { it.tanggalDaftar }

                historyAdapter.notifyDataSetChanged()

                // Show/hide empty state
                if (historyList.isEmpty()) {
                    binding.layoutEmpty.visibility = View.VISIBLE
                    binding.rvHistory.visibility = View.GONE
                } else {
                    binding.layoutEmpty.visibility = View.GONE
                    binding.rvHistory.visibility = View.VISIBLE
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Gagal memuat riwayat: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}