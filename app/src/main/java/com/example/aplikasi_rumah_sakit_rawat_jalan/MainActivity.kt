package com.example.aplikasi_rumah_sakit_rawat_jalan

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.aplikasi_rumah_sakit_rawat_jalan.databinding.ActivityMainBinding
import com.example.aplikasi_rumah_sakit_rawat_jalan.fragment.AppointmentFragment
import com.example.aplikasi_rumah_sakit_rawat_jalan.fragment.DaftarPasienDokterFragment
import com.example.aplikasi_rumah_sakit_rawat_jalan.fragment.HasilPemeriksaanPasienFragment
import com.example.aplikasi_rumah_sakit_rawat_jalan.fragment.HistoryFragment
import com.example.aplikasi_rumah_sakit_rawat_jalan.fragment.HomeFragment
import com.example.aplikasi_rumah_sakit_rawat_jalan.fragment.PoliGigiFragment
import com.example.aplikasi_rumah_sakit_rawat_jalan.fragment.PoliMataFragment
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var userRole: String = "pasien" // Default role
    private var userId: String = ""
    private var userName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 🔥 Inisialisasi Firebase
        FirebaseApp.initializeApp(this)

        // Ambil data user dari intent (dari LoginActivity)
        userId = intent.getStringExtra("USER_ID") ?: ""
        userName = intent.getStringExtra("USER_NAME") ?: ""
        userRole = intent.getStringExtra("USER_ROLE") ?: "pasien"

        // LOG PENTING: Cek data user
        Log.d("MainActivity", "=== DATA USER ===")
        Log.d("MainActivity", "User ID: $userId")
        Log.d("MainActivity", "User Name: $userName")
        Log.d("MainActivity", "User Role: $userRole")
        Log.d("MainActivity", "================")

        // 🔥 Tes koneksi Firestore
        val db = Firebase.firestore
        val testData = hashMapOf(
            "nama" to "Tes Pasien",
            "keluhan" to "Demam"
        )

        db.collection("antrian")
            .add(testData)
            .addOnSuccessListener { docRef ->
                Log.d("FirestoreTest", "Dokumen ditambahkan dengan ID: ${docRef.id}")
            }
            .addOnFailureListener { e ->
                Log.w("FirestoreTest", "Gagal menambahkan dokumen", e)
            }

        // Load fragment pertama berdasarkan role
        if (savedInstanceState == null) {
            if (userRole == "dokter") {
                loadFragment(DaftarPasienDokterFragment())
            } else {
                loadFragment(HomeFragment())
            }
        }

        setupBottomNavigationByRole()
    }

    private fun setupBottomNavigationByRole() {
        if (userRole == "dokter") {
            // Menu untuk DOKTER
            binding.bottomNavigation.menu.clear()
            binding.bottomNavigation.inflateMenu(R.menu.bottom_nav_menu_dokter)

            binding.bottomNavigation.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.nav_daftar_pasien -> {
                        loadFragment(DaftarPasienDokterFragment())
                        true
                    }
                    R.id.nav_riwayat_dokter -> {
                        loadFragment(HistoryFragment())
                        true
                    }
                    else -> false
                }
            }
        } else {
            // Menu untuk PASIEN
            setupBottomNavigation()
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_poli_gigi -> {
                    loadFragment(PoliGigiFragment())
                    true
                }
                R.id.nav_poli_mata -> {
                    loadFragment(PoliMataFragment())
                    true
                }
                R.id.nav_antrian -> {
                    loadFragment(AppointmentFragment())
                    true
                }
                R.id.nav_history -> {
                    // Untuk PASIEN: Tampilkan Hasil Pemeriksaan
                    loadFragment(HasilPemeriksaanPasienFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    fun navigateToFragment(fragment: Fragment) {
        loadFragment(fragment)

        // Update bottom navigation
        when (fragment) {
            is HomeFragment -> binding.bottomNavigation.selectedItemId = R.id.nav_home
            is PoliGigiFragment -> binding.bottomNavigation.selectedItemId = R.id.nav_poli_gigi
            is PoliMataFragment -> binding.bottomNavigation.selectedItemId = R.id.nav_poli_mata
            is AppointmentFragment -> binding.bottomNavigation.selectedItemId = R.id.nav_antrian
            is HasilPemeriksaanPasienFragment -> binding.bottomNavigation.selectedItemId = R.id.nav_history
            is DaftarPasienDokterFragment -> binding.bottomNavigation.selectedItemId = R.id.nav_daftar_pasien
        }
    }

    // Fungsi untuk mendapatkan User ID (bisa dipanggil dari fragment)
    fun getUserId(): String {
        return userId
    }
}