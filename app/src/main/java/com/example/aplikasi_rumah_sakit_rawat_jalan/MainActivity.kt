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
import com.example.aplikasi_rumah_sakit_rawat_jalan.utils.NotificationHelper
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var userRole: String = "pasien"
    private var userId: String = ""
    private var userName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ✅ TAMBAHKAN INI - Initialize Firebase
        FirebaseApp.initializeApp(this)

        // ✅ TAMBAHKAN INI - Setup Notification Channel
        NotificationHelper.createNotificationChannel(this)

        // ✅ AMBIL DATA DARI INTENT
        userId = intent.getStringExtra("USER_ID") ?: ""
        userName = intent.getStringExtra("USER_NAME") ?: ""
        userRole = intent.getStringExtra("USER_ROLE") ?: "pasien"

        // ✅ LOG UNTUK DEBUG (dengan quote biar keliatan spasi!)
        Log.d("MainActivity", "=== DATA USER ===")
        Log.d("MainActivity", "User ID: $userId")
        Log.d("MainActivity", "User Name: $userName")
        Log.d("MainActivity", "User Role: '$userRole'")  // ← QUOTE BIAR KELIATAN SPASI!
        Log.d("MainActivity", "Role Length: ${userRole.length}")  // ← CEK PANJANG STRING!
        Log.d("MainActivity", "================")

        // ✅ LOAD FRAGMENT BERDASARKAN ROLE (DENGAN TRIM!)
        if (savedInstanceState == null) {
            when (userRole.trim()) {  // ← TAMBAH .trim()!
                "dokter" -> {
                    Log.d("MainActivity", "✅ Loading DaftarPasienDokterFragment")
                    loadFragment(DaftarPasienDokterFragment())
                }
                "admin" -> {
                    Log.d("MainActivity", "✅ Admin detected, redirect to AdminActivity")
                    // Kalau somehow admin masuk ke sini, redirect
                    finish()
                }
                else -> {
                    Log.d("MainActivity", "✅ Loading HomeFragment (Pasien)")
                    loadFragment(HomeFragment())
                }
            }
        }

        setupBottomNavigationByRole()
    }

    private fun setupBottomNavigationByRole() {
        // ✅ CEK ROLE DENGAN TRIM!
        when (userRole.trim()) {
            "dokter" -> {
                Log.d("MainActivity", "✅ Setup Bottom Navigation DOKTER")
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
            }
            else -> {
                Log.d("MainActivity", "✅ Setup Bottom Navigation PASIEN")
                setupBottomNavigation()
            }
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
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    fun getUserId(): String = userId
    fun getUserName(): String = userName
    fun getUserRole(): String = userRole
}