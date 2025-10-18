package com.example.aplikasi_rumah_sakit_rawat_jalan.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplikasi_rumah_sakit_rawat_jalan.StrukHelper
import com.example.aplikasi_rumah_sakit_rawat_jalan.adapter.AppointmentAdapter
import com.example.aplikasi_rumah_sakit_rawat_jalan.databinding.FragmentAppointmentBinding
import com.example.aplikasi_rumah_sakit_rawat_jalan.model.Appointment
import com.example.aplikasi_rumah_sakit_rawat_jalan.model.Pendaftaran
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AppointmentFragment : Fragment() {

    private var _binding: FragmentAppointmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var appointmentAdapter: AppointmentAdapter
    private val appointmentList = mutableListOf<Appointment>()

    private var pendingDownloadAppointment: Appointment? = null

    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    // Permission launcher untuk Android 13+
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pendingDownloadAppointment?.let { downloadStruk(it) }
        } else {
            Toast.makeText(context, "Permission ditolak! Tidak bisa menyimpan struk.", Toast.LENGTH_SHORT).show()
        }
        pendingDownloadAppointment = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppointmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        loadAppointmentsFromFirestore()

        // Cek notifikasi setelah 2 detik (simulasi)
        Handler(Looper.getMainLooper()).postDelayed({
            checkAndShowNotification()
        }, 2000)
    }

    override fun onResume() {
        super.onResume()
        // Refresh data setiap kali fragment muncul
        loadAppointmentsFromFirestore()
    }

    private fun setupRecyclerView() {
        appointmentAdapter = AppointmentAdapter(appointmentList) { appointment, action ->
            handleAppointmentAction(appointment, action)
        }

        binding.rvAppointments.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = appointmentAdapter
        }
    }

    private fun handleAppointmentAction(appointment: Appointment, action: String) {
        when (action) {
            "detail" -> {
                Toast.makeText(context, "Detail antrian #${appointment.nomorAntrian}", Toast.LENGTH_SHORT).show()
            }
            "cancel" -> {
                cancelAppointment(appointment)
            }
            "refresh" -> {
                Toast.makeText(context, "Refresh status antrian", Toast.LENGTH_SHORT).show()
                loadAppointmentsFromFirestore() // Refresh data
            }
            "download" -> {
                Log.d("StrukDownload", "=== TOMBOL DOWNLOAD DIKLIK ===")
                handleDownloadStruk(appointment)
            }
        }
    }

    private fun cancelAppointment(appointment: Appointment) {
        // Update status jadi "dibatalkan"
        db.collection("appointments").document(appointment.id)
            .update("status", "dibatalkan")
            .addOnSuccessListener {
                Toast.makeText(context, "Antrian berhasil dibatalkan", Toast.LENGTH_SHORT).show()
                loadAppointmentsFromFirestore()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Gagal membatalkan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadAppointmentsFromFirestore() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            binding.layoutEmpty.visibility = View.VISIBLE
            binding.rvAppointments.visibility = View.GONE
            return
        }

        // Ambil appointment milik user yang sedang login
        db.collection("appointments")
            .whereEqualTo("userId", currentUser.uid)
            .get()
            .addOnSuccessListener { documents ->
                appointmentList.clear()
                for (document in documents) {
                    val appointment = document.toObject(Appointment::class.java)
                    appointment.id = document.id
                    appointmentList.add(appointment)
                }
                appointmentAdapter.notifyDataSetChanged()

                // Show/hide empty state
                if (appointmentList.isEmpty()) {
                    binding.layoutEmpty.visibility = View.VISIBLE
                    binding.rvAppointments.visibility = View.GONE
                } else {
                    binding.layoutEmpty.visibility = View.GONE
                    binding.rvAppointments.visibility = View.VISIBLE
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Gagal memuat data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun handleDownloadStruk(appointment: Appointment) {
        Log.d("StrukDownload", "=== CHECK PERMISSION ===")
        Log.d("StrukDownload", "Android Version: ${Build.VERSION.SDK_INT}")

        // Cek permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Log.d("StrukDownload", "Android 13+ - Langsung download")
            downloadStruk(appointment)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Log.d("StrukDownload", "Android 10-12 - Langsung download")
            downloadStruk(appointment)
        } else {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("StrukDownload", "Android 9- - Permission OK")
                downloadStruk(appointment)
            } else {
                Log.d("StrukDownload", "Android 9- - Minta permission")
                pendingDownloadAppointment = appointment
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    private fun downloadStruk(appointment: Appointment) {
        Log.d("StrukDownload", "=== MULAI DOWNLOAD STRUK ===")
        Toast.makeText(context, "Membuat struk...", Toast.LENGTH_SHORT).show()

        try {
            Log.d("StrukDownload", "Appointment ID: ${appointment.id}")
            Log.d("StrukDownload", "Nomor Antrian: ${appointment.nomorAntrian}")

            // Ambil nama poli
            val namaPoli = appointment.poli.ifEmpty { "Poli Umum" }
            Log.d("StrukDownload", "Nama Poli: $namaPoli")

            // Convert Appointment ke Pendaftaran
            val pendaftaran = Pendaftaran(
                id = appointment.id,
                pasienId = appointment.pasienId,
                namaPasien = appointment.namaPasien.ifEmpty { "Pasien" },
                noTelepon = "",
                dokterId = appointment.dokterId,
                namaDokter = appointment.namaDokter,
                poli = namaPoli,
                tanggalKunjungan = appointment.tanggalKunjungan,
                nomorAntrian = appointment.nomorAntrian,
                keluhan = appointment.keluhan,
                status = appointment.status,
                waktuDaftar = appointment.tanggalDaftar
            )
            Log.d("StrukDownload", "Pendaftaran object created")

            // Generate & save struk
            Log.d("StrukDownload", "Mulai generate struk...")
            val result = StrukHelper.generateAndSaveStruk(requireContext(), pendaftaran)
            Log.d("StrukDownload", "Result: $result")

            if (result != null) {
                Log.d("StrukDownload", "=== BERHASIL! ===")
                showSuccessDialog()
            } else {
                Log.e("StrukDownload", "=== GAGAL! Result null ===")
                Toast.makeText(context, "Gagal menyimpan struk! Cek Logcat", Toast.LENGTH_LONG).show()
            }

        } catch (e: Exception) {
            Log.e("StrukDownload", "=== EXCEPTION! ===")
            Log.e("StrukDownload", "Error: ${e.message}", e)
            e.printStackTrace()
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun showSuccessDialog() {
        val alertDialog = android.app.AlertDialog.Builder(requireContext())
            .setTitle("✅ Berhasil!")
            .setMessage(
                "Struk antrian berhasil disimpan!\n\n" +
                        "Lokasi: Galeri > Struk Antrian\n\n" +
                        "Anda bisa melihatnya di aplikasi Galeri/Photos."
            )
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .setNeutralButton("Buka Galeri") { _, _ ->
                Toast.makeText(context, "Silakan buka aplikasi Galeri", Toast.LENGTH_SHORT).show()
            }
            .create()

        alertDialog.show()
    }

    private fun checkAndShowNotification() {
        // Cek setiap antrian yang statusnya menunggu atau terdaftar
        appointmentList.forEach { appointment ->
            val statusLower = appointment.status.lowercase()
            if (statusLower == "menunggu" || statusLower == "terdaftar") {
                // Simulasi: hitung sisa antrian (random 1-5)
                val sisaAntrian = (1..5).random()

                // Jika tinggal 3 atau kurang, tampilkan notifikasi
                if (sisaAntrian <= 3) {
                    showAlertDialog(appointment, sisaAntrian)
                    return@forEach // Hanya tampilkan 1 notifikasi
                }
            }
        }
    }

    private fun showAlertDialog(appointment: Appointment, sisaAntrian: Int) {
        val alertDialog = android.app.AlertDialog.Builder(requireContext())
            .setTitle("⚠️ Segera Bersiap!")
            .setMessage(
                "Antrian Anda hampir tiba!\n\n" +
                        "Nomor Antrian: ${appointment.nomorAntrian}\n" +
                        "Sisa Antrian: $sisaAntrian orang\n" +
                        "Estimasi: ${sisaAntrian * 10} menit\n\n" +
                        "Mohon bersiap dan datang ke ruang tunggu."
            )
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .setNeutralButton("Lihat Detail") { _, _ ->
                Toast.makeText(
                    context,
                    "Detail antrian #${appointment.nomorAntrian}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setCancelable(false)
            .create()

        alertDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}