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

class MainActivity : AppCompatActivity() {
    // ... sisanya tetap sama, GAK USAH DIUBAH

    private lateinit var binding: ActivityMainBinding
    private var userRole: String = "pasien"
    private var userId: String = ""
    private var userName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)

        userId = intent.getStringExtra("USER_ID") ?: ""
        userName = intent.getStringExtra("USER_NAME") ?: ""
        userRole = intent.getStringExtra("USER_ROLE") ?: "pasien"

        Log.d("MainActivity", "User ID: $userId, Name: $userName, Role: $userRole")

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