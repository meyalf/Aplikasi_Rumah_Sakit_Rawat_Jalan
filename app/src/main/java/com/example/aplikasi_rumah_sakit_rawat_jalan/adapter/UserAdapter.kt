package com.example.aplikasi_rumah_sakit_rawat_jalan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasi_rumah_sakit_rawat_jalan.R
import com.example.aplikasi_rumah_sakit_rawat_jalan.model.User

class UserAdapter(
    private val userList: MutableList<User>,
    private val onActionClick: (User, String) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivIconUser: ImageView = view.findViewById(R.id.iv_icon_user)
        val tvNamaUser: TextView = view.findViewById(R.id.tv_nama_user)
        val tvEmailUser: TextView = view.findViewById(R.id.tv_email_user)
        val tvRoleUser: TextView = view.findViewById(R.id.tv_role_user)
        val btnResetPassword: ImageButton = view.findViewById(R.id.btn_reset_password)
        val btnDelete: ImageButton = view.findViewById(R.id.btn_delete_user)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]

        holder.tvNamaUser.text = user.nama
        holder.tvEmailUser.text = user.email
        holder.tvRoleUser.text = "Role: ${user.role.uppercase()}"

        // Ubah warna badge berdasarkan role
        when (user.role.lowercase()) {
            "admin" -> holder.tvRoleUser.setBackgroundColor(0xFF2196F3.toInt())
            "dokter" -> holder.tvRoleUser.setBackgroundColor(0xFF4CAF50.toInt())
            "pasien" -> holder.tvRoleUser.setBackgroundColor(0xFF9E9E9E.toInt())
        }

        // Tombol Reset Password
        holder.btnResetPassword.setOnClickListener {
            onActionClick(user, "reset")
        }

        // Tombol Delete
        holder.btnDelete.setOnClickListener {
            onActionClick(user, "delete")
        }
    }

    override fun getItemCount(): Int = userList.size

    fun updateList(newList: List<User>) {
        userList.clear()
        userList.addAll(newList)
        notifyDataSetChanged()
    }
}