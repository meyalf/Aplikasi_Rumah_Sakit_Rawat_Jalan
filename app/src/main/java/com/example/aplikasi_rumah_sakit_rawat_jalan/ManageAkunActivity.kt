package com.example.aplikasi_rumah_sakit_rawat_jalan

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasi_rumah_sakit_rawat_jalan.adapter.UserAdapter
import com.example.aplikasi_rumah_sakit_rawat_jalan.model.User
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ManageAkunActivity : AppCompatActivity() {

    private lateinit var rvUsers: RecyclerView
    private lateinit var fabAddUser: FloatingActionButton
    private lateinit var spinnerFilterRole: Spinner
    private lateinit var adapter: UserAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private val userList = mutableListOf<User>()
    private val allUserList = mutableListOf<User>()

    private val roleList = listOf("Semua Role", "Admin", "Dokter", "Pasien")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_akun)

        supportActionBar?.title = "Kelola Akun Pengguna"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        initViews()
        setupFilterSpinner()
        loadUserData()
    }

    private fun initViews() {
        rvUsers = findViewById(R.id.rv_users)
        fabAddUser = findViewById(R.id.fab_add_user)
        spinnerFilterRole = findViewById(R.id.spinner_filter_role)

        rvUsers.layoutManager = LinearLayoutManager(this)

        adapter = UserAdapter(userList) { user, action ->
            when (action) {
                "reset" -> confirmResetPassword(user)
                "delete" -> confirmDeleteUser(user)
            }
        }

        rvUsers.adapter = adapter

        fabAddUser.setOnClickListener {
            showAddUserDialog()
        }
    }

    private fun setupFilterSpinner() {
        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            roleList
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFilterRole.adapter = spinnerAdapter

        spinnerFilterRole.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                filterUserByRole(roleList[position])
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
    }

    private fun filterUserByRole(role: String) {
        val filteredList = if (role == "Semua Role") {
            allUserList
        } else {
            allUserList.filter { it.role.equals(role, ignoreCase = true) }
        }
        adapter.updateList(filteredList)
    }

    private fun loadUserData() {
        db.collection("users")
            .get()
            .addOnSuccessListener { documents ->
                userList.clear()
                allUserList.clear()

                for (document in documents) {
                    val user = document.toObject(User::class.java)
                    user.id = document.id
                    userList.add(user)
                    allUserList.add(user)
                }

                adapter.notifyDataSetChanged()

                if (userList.isEmpty()) {
                    Toast.makeText(this, "Belum ada data user", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal memuat data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showAddUserDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_add_user)
        dialog.window?.setLayout(
            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val etNama = dialog.findViewById<EditText>(R.id.et_nama_user)
        val etEmail = dialog.findViewById<EditText>(R.id.et_email_user)
        val etPassword = dialog.findViewById<EditText>(R.id.et_password_user)
        val spinnerRole = dialog.findViewById<Spinner>(R.id.spinner_role)
        val btnSave = dialog.findViewById<Button>(R.id.btn_save_user)
        val btnCancel = dialog.findViewById<Button>(R.id.btn_cancel)

        val roleAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            listOf("Admin", "Dokter", "Pasien")
        )
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRole.adapter = roleAdapter

        btnSave.setOnClickListener {
            val nama = etNama.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val role = spinnerRole.selectedItem?.toString()?.lowercase() ?: "pasien"

            if (nama.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "Password minimal 6 karakter!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Buat user di Firebase Authentication
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { authResult ->
                    val userId = authResult.user?.uid ?: return@addOnSuccessListener

                    // Simpan data user ke Firestore
                    val userData = hashMapOf(
                        "nama" to nama,
                        "email" to email,
                        "role" to role
                    )

                    db.collection("users").document(userId)
                        .set(userData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "User berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                            loadUserData()
                            dialog.dismiss()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Gagal menyimpan data: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Gagal membuat akun: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun confirmResetPassword(user: User) {
        AlertDialog.Builder(this)
            .setTitle("Reset Password")
            .setMessage("Kirim email reset password ke ${user.email}?")
            .setPositiveButton("Kirim") { _, _ ->
                resetPassword(user)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun resetPassword(user: User) {
        auth.sendPasswordResetEmail(user.email)
            .addOnSuccessListener {
                Toast.makeText(this, "Email reset password telah dikirim ke ${user.email}", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal mengirim email: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun confirmDeleteUser(user: User) {
        AlertDialog.Builder(this)
            .setTitle("Hapus User")
            .setMessage("Apakah Anda yakin ingin menghapus user ${user.nama}?")
            .setPositiveButton("Hapus") { _, _ ->
                deleteUser(user)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun deleteUser(user: User) {
        // Hapus dari Firestore
        db.collection("users").document(user.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "User berhasil dihapus dari database", Toast.LENGTH_SHORT).show()
                loadUserData()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal menghapus: ${e.message}", Toast.LENGTH_SHORT).show()
            }

        // Note: Untuk hapus user dari Authentication, butuh Cloud Functions atau Admin SDK
        // Karena Firebase Authentication gak bisa dihapus dari client side
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}