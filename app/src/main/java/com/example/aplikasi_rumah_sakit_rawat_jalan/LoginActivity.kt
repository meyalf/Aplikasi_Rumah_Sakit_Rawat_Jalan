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

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        etEmail = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)
        btnLogin = findViewById(R.id.btn_login)
        tvRegister = findViewById(R.id.tv_register)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan password harus diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginUser(email, password)
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun loginUser(email: String, password: String) {
        Log.d("LoginDebug", "Mulai login: $email")

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid ?: return@addOnSuccessListener
                Log.d("LoginDebug", "Login berhasil! UID: $uid")

                db.collection("users").document(uid).get()
                    .addOnSuccessListener { document ->
                        if (!document.exists()) {
                            Toast.makeText(this, "Data user tidak ditemukan!", Toast.LENGTH_SHORT).show()
                            return@addOnSuccessListener
                        }

                        // ✅ TRIM UNTUK HAPUS SPASI!
                        val role = document.getString("role")?.trim() ?: "pasien"
                        val nama = document.getString("nama")?.trim() ?: "User"

                        Log.d("LoginDebug", "Role: '$role', Nama: '$nama'")

                        // ✅ NAVIGASI BERDASARKAN ROLE
                        when (role) {
                            "admin" -> {
                                Log.d("LoginDebug", "✅ Navigasi ke AdminActivity")
                                Toast.makeText(this, "Selamat datang Admin!", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, AdminActivity::class.java))
                                finish()
                            }
                            "dokter" -> {
                                Log.d("LoginDebug", "✅ Navigasi ke MainActivity DOKTER")
                                Toast.makeText(this, "Selamat datang Dokter $nama!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, MainActivity::class.java)
                                intent.putExtra("USER_ID", uid)
                                intent.putExtra("USER_NAME", nama)
                                intent.putExtra("USER_ROLE", "dokter")  // ← PENTING!
                                startActivity(intent)
                                finish()
                            }
                            else -> {
                                Log.d("LoginDebug", "✅ Navigasi ke MainActivity PASIEN")
                                Toast.makeText(this, "Selamat datang $nama!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, MainActivity::class.java)
                                intent.putExtra("USER_ID", uid)
                                intent.putExtra("USER_NAME", nama)
                                intent.putExtra("USER_ROLE", "pasien")
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Gagal mengambil data: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Login gagal: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}