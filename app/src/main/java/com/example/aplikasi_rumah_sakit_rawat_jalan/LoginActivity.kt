package com.example.aplikasi_rumah_sakit_rawat_jalan

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegister: TextView

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inisialisasi Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Inisialisasi View
        etEmail = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)
        btnLogin = findViewById(R.id.btn_login)
        tvRegister = findViewById(R.id.tv_register)

        Log.d("LoginDebug", "LoginActivity onCreate berhasil")

        btnLogin.setOnClickListener {
            Log.d("LoginDebug", "Tombol Login diklik!")
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan password harus diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginUser(email, password)
        }

        tvRegister.setOnClickListener {
            Log.d("LoginDebug", "Tombol Register diklik!")
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun loginUser(email: String, password: String) {
        Log.d("LoginDebug", "Mulai login: $email")

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid ?: return@addOnSuccessListener
                Log.d("LoginDebug", "Login berhasil! UID: $uid")

                // Ambil data user dari Firestore menggunakan document ID = UID
                db.collection("users").document(uid).get()
                    .addOnSuccessListener { document ->
                        Log.d("LoginDebug", "Firestore query berhasil. Document exists: ${document.exists()}")

                        if (!document.exists()) {
                            Log.e("LoginDebug", "Data user TIDAK ditemukan di Firestore!")
                            Toast.makeText(this, "Data user tidak ditemukan!", Toast.LENGTH_SHORT).show()
                            return@addOnSuccessListener
                        }

                        val role = document.getString("role") ?: "pasien"
                        val nama = document.getString("nama") ?: "User"

                        Log.d("LoginDebug", "Role: $role, Nama: $nama")

                        // Redirect berdasarkan role
                        when (role) {
                            "admin" -> {
                                Log.d("LoginDebug", "Navigasi ke AdminActivity...")
                                Toast.makeText(this, "Selamat datang Admin!", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, AdminActivity::class.java))
                                finish()
                            }
                            "dokter" -> {
                                Log.d("LoginDebug", "Navigasi ke DokterActivity...")
                                Toast.makeText(this, "Selamat datang Dokter!", Toast.LENGTH_SHORT).show()
                                // startActivity(Intent(this, DokterActivity::class.java))
                                // Sementara ke MainActivity dulu kalau DokterActivity belum ada
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                            }
                            else -> {
                                Log.d("LoginDebug", "Navigasi ke MainActivity (Pasien)...")
                                Toast.makeText(this, "Selamat datang!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, MainActivity::class.java)
                                intent.putExtra("USER_ROLE", role)
                                intent.putExtra("USER_ID", uid)
                                intent.putExtra("USER_NAME", nama)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("LoginDebug", "Gagal query Firestore: ${e.message}")
                        Toast.makeText(this, "Gagal mengambil data: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Log.e("LoginDebug", "Login gagal: ${e.message}")
                Toast.makeText(this, "Login gagal: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}