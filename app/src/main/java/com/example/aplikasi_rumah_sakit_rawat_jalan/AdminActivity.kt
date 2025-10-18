package com.example.aplikasi_rumah_sakit_rawat_jalan

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AdminActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    // Deklarasi CardView untuk setiap menu
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

        // Setup ActionBar
        supportActionBar?.title = "Dashboard Admin"

        // Inisialisasi Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Panggil fungsi untuk inisialisasi view dan listener
        initViews()
        setupClickListeners()
        checkAdminAccess()
    }

    // Fungsi untuk menghubungkan variabel dengan ID di XML
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

    // Fungsi untuk mengatur aksi ketika card diklik
    private fun setupClickListeners() {
        // Kelola Pasien
        cvManagePasien.setOnClickListener {
            Toast.makeText(this, "Kelola Pasien - Coming Soon", Toast.LENGTH_SHORT).show()
        }

        // Kelola Dokter
        cvManageDokter.setOnClickListener {
            Toast.makeText(this, "Kelola Dokter - Coming Soon", Toast.LENGTH_SHORT).show()
        }

        // ✅ Kelola Poli
        cvManagePoli.setOnClickListener {
            startActivity(Intent(this, ManagePoliActivity::class.java))
        }

        // ✅ Kelola Jadwal
        cvManageJadwal.setOnClickListener {
            startActivity(Intent(this, ManageJadwalActivity::class.java))
        }

        // ✅ Kelola Obat
        cvManageObat.setOnClickListener {
            startActivity(Intent(this, ManageObatActivity::class.java))
        }

        // Verifikasi Pendaftaran
        cvVerifikasiPendaftaran.setOnClickListener {
            startActivity(Intent(this, VerifikasiPendaftaranActivity::class.java))
        }

        // Laporan
        cvLaporan.setOnClickListener {
            Toast.makeText(this, "Laporan - Coming Soon", Toast.LENGTH_SHORT).show()
        }

        // Kelola Akun
        cvManageAkun.setOnClickListener {
            Toast.makeText(this, "Kelola Akun - Coming Soon", Toast.LENGTH_SHORT).show()
        }

        // Logout
        cvLogout.setOnClickListener {
            logoutAdmin()
        }
    }

    // Fungsi untuk cek apakah user adalah admin
    private fun checkAdminAccess() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            redirectToLogin()
            return
        }

        // Cek role user di Firestore
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

    // Fungsi logout
    private fun logoutAdmin() {
        auth.signOut()
        Toast.makeText(this, "Logout berhasil", Toast.LENGTH_SHORT).show()
        redirectToLogin()
    }

    // Fungsi untuk kembali ke halaman login
    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}