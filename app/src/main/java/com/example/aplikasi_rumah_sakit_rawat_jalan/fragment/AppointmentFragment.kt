package com.example.aplikasi_rumah_sakit_rawat_jalan.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.*
import com.example.aplikasi_rumah_sakit_rawat_jalan.StrukHelper
import com.example.aplikasi_rumah_sakit_rawat_jalan.adapter.AppointmentAdapter
import com.example.aplikasi_rumah_sakit_rawat_jalan.databinding.FragmentAppointmentBinding
import com.example.aplikasi_rumah_sakit_rawat_jalan.model.Appointment
import com.example.aplikasi_rumah_sakit_rawat_jalan.model.Pendaftaran
import com.example.aplikasi_rumah_sakit_rawat_jalan.utils.NotificationHelper
import com.example.aplikasi_rumah_sakit_rawat_jalan.viewmodel.AppointmentViewModel
import com.example.aplikasi_rumah_sakit_rawat_jalan.worker.AntrianCheckWorker
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class AppointmentFragment : Fragment() {

    private var _binding: FragmentAppointmentBinding? = null
    private val binding get() = _binding!!

    // ViewModel dengan delegation
    private val viewModel: AppointmentViewModel by viewModels()

    private lateinit var appointmentAdapter: AppointmentAdapter
    private var pendingDownloadAppointment: Appointment? = null

    // Permission launcher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pendingDownloadAppointment?.let { downloadStruk(it) }
        } else {
            Toast.makeText(context, "Permission ditolak!", Toast.LENGTH_SHORT).show()
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

        // Setup notification
        NotificationHelper.createNotificationChannel(requireContext())

        // Setup WorkManager
        setupAntrianWorker()

        // Request notification permission
        requestNotificationPermission()

        // Setup UI
        setupRecyclerView()
        observeViewModel()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        appointmentAdapter = AppointmentAdapter(emptyList()) { appointment, action ->
            handleAppointmentAction(appointment, action)
        }

        binding.rvAppointments.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = appointmentAdapter
        }
    }

    private fun observeViewModel() {
        // Observe appointments
        viewModel.appointments.observe(viewLifecycleOwner) { appointments ->
            appointmentAdapter = AppointmentAdapter(appointments) { appointment, action ->
                handleAppointmentAction(appointment, action)
            }
            binding.rvAppointments.adapter = appointmentAdapter
        }

        // Observe loading
        lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }

        // Observe empty state
        lifecycleScope.launch {
            viewModel.isEmpty.collect { isEmpty ->
                binding.layoutEmpty.visibility = if (isEmpty) View.VISIBLE else View.GONE
                binding.rvAppointments.visibility = if (isEmpty) View.GONE else View.VISIBLE
            }
        }

        // Observe error
        lifecycleScope.launch {
            viewModel.error.collect { error ->
                error?.let {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupClickListeners() {
        // FAB Refresh
        binding.fabRefresh.setOnClickListener {
            viewModel.refreshAppointments()
            Toast.makeText(context, "Memperbarui data...", Toast.LENGTH_SHORT).show()
        }

        // Button Buat Antrian (di empty state)
        binding.btnBuatAntrian?.setOnClickListener {
            // Navigate ke HomeFragment
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, HomeFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun handleAppointmentAction(appointment: Appointment, action: String) {
        when (action) {
            "detail" -> {
                Toast.makeText(context, "Detail antrian #${appointment.nomorAntrian}", Toast.LENGTH_SHORT).show()
            }
            "cancel" -> {
                showCancelDialog(appointment)
            }
            "refresh" -> {
                viewModel.refreshAppointments()
            }
            "download" -> {
                handleDownloadStruk(appointment)
            }
        }
    }

    private fun showCancelDialog(appointment: Appointment) {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Batalkan Antrian")
            .setMessage("Apakah Anda yakin ingin membatalkan antrian #${appointment.nomorAntrian}?")
            .setPositiveButton("Ya, Batalkan") { _, _ ->
                cancelAppointment(appointment)
            }
            .setNegativeButton("Tidak", null)
            .show()
    }

    private fun cancelAppointment(appointment: Appointment) {
        viewModel.cancelAppointment(
            appointment.id,
            onSuccess = {
                Toast.makeText(context, "✅ Antrian berhasil dibatalkan", Toast.LENGTH_SHORT).show()
            },
            onError = { error ->
                Toast.makeText(context, "❌ Gagal: $error", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun setupAntrianWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<AntrianCheckWorker>(
            15, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(requireContext())
            .enqueueUniquePeriodicWork(
                "antrian_check",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )

        Log.d("AppointmentFragment", "✅ WorkManager scheduled")
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun handleDownloadStruk(appointment: Appointment) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            downloadStruk(appointment)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            downloadStruk(appointment)
        } else {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                downloadStruk(appointment)
            } else {
                pendingDownloadAppointment = appointment
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    private fun downloadStruk(appointment: Appointment) {
        Toast.makeText(context, "📄 Membuat struk...", Toast.LENGTH_SHORT).show()

        try {
            val namaPoli = appointment.poli.ifEmpty { "Poli Umum" }

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

            val result = StrukHelper.generateAndSaveStruk(requireContext(), pendaftaran)

            if (result != null) {
                showSuccessDialog()
            } else {
                Toast.makeText(context, "❌ Gagal menyimpan struk!", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Log.e("StrukDownload", "Error: ${e.message}", e)
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun showSuccessDialog() {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("✅ Berhasil!")
            .setMessage("Struk antrian berhasil disimpan!\n\nLokasi: Galeri > Struk Antrian")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshAppointments()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}