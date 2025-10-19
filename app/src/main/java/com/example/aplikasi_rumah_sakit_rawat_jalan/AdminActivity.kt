package com.example.aplikasi_rumah_sakit_rawat_jalan

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AdminActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var cvManagePasien: CardView
    private lateinit var cvManageDokter: CardView
    private lateinit var cvManagePoli: CardView
    private lateinit var cvManageJadwal: CardView
    private lateinit var cvManageObat: CardView
    private lateinit var cvVerifikasiPendaftaran: CardView
    private lateinit var cvLaporan: CardView
    private lateinit var cvManageAkun: CardView
    private lateinit var cvLogout: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        supportActionBar?.title = "Dashboard Admin"

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        initViews()
        setupClickListeners()
        checkAdminAccess()
    }

    private fun initViews() {
        cvManagePasien = findViewById(R.id.cv_manage_pasien)
        cvManageDokter = findViewById(R.id.cv_manage_dokter)
        cvManagePoli = findViewById(R.id.cv_manage_poli)
        cvManageJadwal = findViewById(R.id.cv_manage_jadwal)
        cvManageObat = findViewById(R.id.cv_manage_obat)
        cvVerifikasiPendaftaran = findViewById(R.id.cv_verifikasi_pendaftaran)
        cvLaporan = findViewById(R.id.cv_laporan)
        cvManageAkun = findViewById(R.id.cv_manage_akun)
        cvLogout = findViewById(R.id.cv_logout)
    }

    private fun setupClickListeners() {
        // ✅ KELOLA PASIEN - AKTIF!
        cvManagePasien.setOnClickListener {
            startActivity(Intent(this, ManagePasienActivity::class.java))
        }

        cvManageDokter.setOnClickListener {
            Log.d("AdminActivity", "Kelola Dokter diklik!")
            try {
                val intent = Intent(this, ManageDokterActivity::class.java)
                startActivity(intent)
                Log.d("AdminActivity", "Intent berhasil dijalankan")
            } catch (e: Exception) {
                Log.e("AdminActivity", "Error: ${e.message}")
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        cvManagePoli.setOnClickListener {
            startActivity(Intent(this, ManagePoliActivity::class.java))
        }

        cvManageJadwal.setOnClickListener {
            startActivity(Intent(this, ManageJadwalActivity::class.java))
        }

        cvManageObat.setOnClickListener {
            startActivity(Intent(this, ManageObatActivity::class.java))
        }

        cvVerifikasiPendaftaran.setOnClickListener {
            startActivity(Intent(this, VerifikasiPendaftaranActivity::class.java))
        }

        // ✅ LAPORAN - AKTIF!
        cvLaporan.setOnClickListener {
            startActivity(Intent(this, LaporanActivity::class.java))
        }

        // ✅ KELOLA AKUN - AKTIF!
        cvManageAkun.setOnClickListener {
            startActivity(Intent(this, ManageAkunActivity::class.java))
        }

        cvLogout.setOnClickListener {
            logoutAdmin()
        }
    }

    private fun checkAdminAccess() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            redirectToLogin()
            return
        }

        db.collection("users").document(currentUser.uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val role = document.getString("role")
                    if (role != "admin") {
                        Toast.makeText(this, "Akses ditolak! Anda bukan admin.", Toast.LENGTH_SHORT).show()
                        redirectToLogin()
                    }
                } else {
                    Toast.makeText(this, "Data pengguna tidak ditemukan", Toast.LENGTH_SHORT).show()
                    redirectToLogin()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal memverifikasi akses", Toast.LENGTH_SHORT).show()
                redirectToLogin()
            }
    }

    private fun logoutAdmin() {
        auth.signOut()
        Toast.makeText(this, "Logout berhasil", Toast.LENGTH_SHORT).show()
        redirectToLogin()
    }

    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}