package com.example.aplikasi_rumah_sakit_rawat_jalan.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.aplikasi_rumah_sakit_rawat_jalan.MainActivity
import com.example.aplikasi_rumah_sakit_rawat_jalan.databinding.FragmentAmbilAntrianBinding
import com.example.aplikasi_rumah_sakit_rawat_jalan.model.Dokter
import com.example.aplikasi_rumah_sakit_rawat_jalan.model.Appointment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class AmbilAntrianFragment : Fragment() {

    private var _binding: FragmentAmbilAntrianBinding? = null
    private val binding get() = _binding!!

    private var selectedDokter: Dokter? = null
    private var selectedDate: Calendar = Calendar.getInstance()
    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAmbilAntrianBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ambil data dokter dari arguments
        selectedDokter = arguments?.getParcelable("dokter")

        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        selectedDokter?.let { dokter ->
            binding.tvNamaDokter.text = dokter.nama
            binding.tvSpesialisDokter.text = dokter.spesialis
            binding.tvPoliDokter.text = dokter.poliklinik
            binding.tvJadwalDokter.text = "${dokter.hari.joinToString(", ")} • ${dokter.jamPraktek}"
        }

        // Set tanggal hari ini sebagai default (jika hari ini ada di jadwal praktek)
        validateAndSetInitialDate()
    }

    private fun validateAndSetInitialDate() {
        selectedDokter?.let { dokter ->
            val today = Calendar.getInstance()
            val todayDayName = getDayName(today.get(Calendar.DAY_OF_WEEK))

            if (dokter.hari.contains(todayDayName)) {
                // Hari ini dokter praktek, pakai hari ini
                selectedDate = today
            } else {
                // Hari ini dokter tidak praktek, cari hari praktek terdekat
                selectedDate = getNextAvailableDate(dokter.hari)
            }
            updateDateDisplay()
        }
    }

    private fun setupListeners() {
        // Pilih Tanggal
        binding.btnPilihTanggal.setOnClickListener {
            showDatePicker()
        }

        // Button Ambil Antrian
        binding.btnAmbilAntrian.setOnClickListener {
            if (validateForm()) {
                submitAntrian()
            }
        }

        // Button Batal
        binding.btnBatal.setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(AppointmentFragment())
        }
    }

    private fun showDatePicker() {
        selectedDokter?.let { dokter ->
            val datePicker = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val tempDate = Calendar.getInstance()
                    tempDate.set(year, month, dayOfMonth)

                    val selectedDayName = getDayName(tempDate.get(Calendar.DAY_OF_WEEK))

                    // Validasi: cek apakah hari yang dipilih ada di jadwal praktek dokter
                    if (dokter.hari.contains(selectedDayName)) {
                        // ✅ Tanggal valid
                        selectedDate = tempDate
                        updateDateDisplay()
                    } else {
                        // ❌ Tanggal tidak valid
                        showInvalidDateDialog(selectedDayName, dokter.hari)
                    }
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
            )

            // Set minimum date = hari ini
            datePicker.datePicker.minDate = System.currentTimeMillis()

            // Custom DatePicker dengan validasi realtime
            datePicker.datePicker.setOnDateChangedListener { view, year, monthOfYear, dayOfMonth ->
                val tempDate = Calendar.getInstance()
                tempDate.set(year, monthOfYear, dayOfMonth)
                val dayName = getDayName(tempDate.get(Calendar.DAY_OF_WEEK))

                // Tampilkan peringatan jika bukan hari praktek (optional)
                if (!dokter.hari.contains(dayName)) {
                    // Bisa tambahkan visual feedback di sini
                }
            }

            datePicker.show()
        }
    }

    private fun showInvalidDateDialog(selectedDay: String, hariPraktek: List<String>) {
        AlertDialog.Builder(requireContext())
            .setTitle("⚠️ Hari Tidak Tersedia")
            .setMessage(
                "Dokter tidak praktek di hari $selectedDay.\n\n" +
                        "Dokter hanya praktek di:\n${hariPraktek.joinToString("\n• ", "• ")}\n\n" +
                        "Silakan pilih tanggal pada hari praktek dokter."
            )
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                // Buka date picker lagi
                showDatePicker()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    private fun getDayName(dayOfWeek: Int): String {
        return when (dayOfWeek) {
            Calendar.SUNDAY -> "Minggu"
            Calendar.MONDAY -> "Senin"
            Calendar.TUESDAY -> "Selasa"
            Calendar.WEDNESDAY -> "Rabu"
            Calendar.THURSDAY -> "Kamis"
            Calendar.FRIDAY -> "Jumat"
            Calendar.SATURDAY -> "Sabtu"
            else -> ""
        }
    }

    private fun getNextAvailableDate(hariPraktek: List<String>): Calendar {
        val calendar = Calendar.getInstance()

        // Cari tanggal terdekat yang sesuai jadwal praktek (maksimal cek 14 hari ke depan)
        for (i in 0..13) {
            val dayName = getDayName(calendar.get(Calendar.DAY_OF_WEEK))
            if (hariPraktek.contains(dayName)) {
                return calendar
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        // Kalau tidak ketemu, return hari ini (fallback)
        return Calendar.getInstance()
    }

    private fun updateDateDisplay() {
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
        binding.tvTanggalTerpilih.text = dateFormat.format(selectedDate.time)
    }

    private fun validateForm(): Boolean {
        val keluhan = binding.etKeluhan.text.toString().trim()

        if (keluhan.isEmpty()) {
            binding.etKeluhan.error = "Keluhan tidak boleh kosong"
            return false
        }

        if (keluhan.length < 10) {
            binding.etKeluhan.error = "Keluhan minimal 10 karakter"
            return false
        }

        // Validasi tambahan: pastikan tanggal yang dipilih sesuai jadwal praktek
        selectedDokter?.let { dokter ->
            val selectedDayName = getDayName(selectedDate.get(Calendar.DAY_OF_WEEK))
            if (!dokter.hari.contains(selectedDayName)) {
                Toast.makeText(
                    context,
                    "❌ Tanggal tidak valid. Dokter tidak praktek di hari $selectedDayName",
                    Toast.LENGTH_LONG
                ).show()
                return false
            }
        }

        return true
    }

    private fun submitAntrian() {
        val keluhan = binding.etKeluhan.text.toString().trim()
        val catatan = binding.etCatatan.text.toString().trim()
        val currentUser = auth.currentUser

        if (currentUser == null) {
            Toast.makeText(context, "Anda harus login terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }

        selectedDokter?.let { dokter ->
            // Dapatkan nomor antrian terakhir dari Firestore
            getNextNomorAntrian { nomorAntrian ->
                // Format tanggal jadi String
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val tanggalString = dateFormat.format(selectedDate.time)

                // Buat appointment baru dengan model yang sudah diupdate
                val newAppointment = hashMapOf(
                    "userId" to currentUser.uid,
                    "pasienId" to currentUser.uid,
                    "namaPasien" to (currentUser.displayName ?: "Pasien"),
                    "dokterId" to dokter.id.toString(),
                    "namaDokter" to dokter.nama,
                    "genderDokter" to dokter.gender, // Tambah gender dokter
                    "poliklinikId" to (if (dokter.poliklinik == "Poli Gigi") "1" else "2"),
                    "poli" to dokter.poliklinik,
                    "tanggalKunjungan" to tanggalString,
                    "jamKunjungan" to "09:00",
                    "jamPraktek" to dokter.jamPraktek,
                    "keluhan" to keluhan,
                    "status" to "terdaftar",
                    "nomorAntrian" to nomorAntrian,
                    "tanggalDaftar" to System.currentTimeMillis(),
                    "catatan" to catatan
                )

                // Simpan ke Firestore
                db.collection("appointments")
                    .add(newAppointment)
                    .addOnSuccessListener {
                        Toast.makeText(
                            context,
                            "✅ Antrian berhasil dibuat!\nNomor Antrian: $nomorAntrian",
                            Toast.LENGTH_LONG
                        ).show()

                        // Balik ke AppointmentFragment setelah delay kecil
                        Handler(Looper.getMainLooper()).postDelayed({
                            (activity as? MainActivity)?.navigateToFragment(AppointmentFragment())
                        }, 500)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            context,
                            "❌ Gagal menyimpan: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
        }
    }

    private fun getNextNomorAntrian(callback: (Int) -> Unit) {
        // Ambil semua appointment hari ini untuk hitung nomor antrian
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayString = dateFormat.format(selectedDate.time)

        db.collection("appointments")
            .whereEqualTo("tanggalKunjungan", todayString)
            .get()
            .addOnSuccessListener { documents ->
                val nomorAntrian = documents.size() + 1
                callback(nomorAntrian)
            }
            .addOnFailureListener {
                // Kalau gagal, default ke 1
                callback(1)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(dokter: Dokter): AmbilAntrianFragment {
            val fragment = AmbilAntrianFragment()
            val bundle = Bundle()
            bundle.putParcelable("dokter", dokter)
            fragment.arguments = bundle
            return fragment
        }
    }
}