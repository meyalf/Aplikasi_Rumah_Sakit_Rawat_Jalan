package com.example.aplikasi_rumah_sakit_rawat_jalan.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplikasi_rumah_sakit_rawat_jalan.model.Appointment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * ViewModel untuk AppointmentFragment
 *
 * KENAPA PAKAI VIEWMODEL?
 * 1. Data survive screen rotation
 * 2. Pisahkan business logic dari UI
 * 3. Manage loading states dengan clean
 * 4. Lifecycle-aware (otomatis cleanup)
 */
class AppointmentViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // ===== LIVEDATA untuk data appointments =====
    // Private MutableLiveData = bisa diubah di dalam ViewModel
    private val _appointments = MutableLiveData<List<Appointment>>()
    // Public LiveData = read-only untuk Fragment
    val appointments: LiveData<List<Appointment>> = _appointments

    // ===== STATEFLOW untuk UI states =====
    // StateFlow lebih modern dari LiveData, support Kotlin Flow

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isEmpty = MutableStateFlow(false)
    val isEmpty: StateFlow<Boolean> = _isEmpty.asStateFlow()

    // Init block = dipanggil otomatis ketika ViewModel dibuat
    init {
        loadAppointments()
    }

    /**
     * Load appointments dari Firestore
     * Menggunakan coroutines untuk async operation
     */
    fun loadAppointments() {
        // viewModelScope = coroutine scope yang otomatis dibatalkan
        // ketika ViewModel di-destroy
        viewModelScope.launch {
            try {
                // Set loading = true
                _isLoading.value = true
                _error.value = null

                // Cek user login
                val currentUser = auth.currentUser
                if (currentUser == null) {
                    _isEmpty.value = true
                    _isLoading.value = false
                    return@launch
                }

                // Query Firestore dengan suspend function
                // .await() = convert Task<> jadi suspend function
                val snapshot = db.collection("appointments")
                    .whereEqualTo("userId", currentUser.uid)
                    .get()
                    .await()

                // Convert documents jadi list Appointment
                val appointmentList = mutableListOf<Appointment>()
                for (document in snapshot.documents) {
                    val appointment = document.toObject(Appointment::class.java)
                    if (appointment != null) {
                        appointment.id = document.id
                        appointmentList.add(appointment)
                    }
                }

                // Sort by newest (descending)
                appointmentList.sortByDescending { it.tanggalDaftar }

                // Update LiveData (otomatis notify observer)
                _appointments.value = appointmentList
                _isEmpty.value = appointmentList.isEmpty()

            } catch (e: Exception) {
                // Handle error
                _error.value = "Gagal memuat data: ${e.message}"
            } finally {
                // Always execute, set loading = false
                _isLoading.value = false
            }
        }
    }

    /**
     * Cancel appointment dengan callback
     */
    fun cancelAppointment(
        appointmentId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                // Update status di Firestore
                db.collection("appointments")
                    .document(appointmentId)
                    .update("status", "dibatalkan")
                    .await()

                // Callback success
                onSuccess()

                // Refresh data
                loadAppointments()

            } catch (e: Exception) {
                // Callback error
                onError(e.message ?: "Gagal membatalkan")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Refresh appointments (pull to refresh)
     */
    fun refreshAppointments() {
        loadAppointments()
    }

    // onCleared() dipanggil otomatis ketika ViewModel di-destroy
    // Bisa digunakan untuk cleanup resources
    override fun onCleared() {
        super.onCleared()
        // Cleanup jika perlu (coroutines otomatis dibatalkan)
    }
}