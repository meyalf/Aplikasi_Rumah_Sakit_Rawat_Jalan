package com.example.aplikasi_rumah_sakit_rawat_jalan.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.aplikasi_rumah_sakit_rawat_jalan.utils.NotificationHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Background Worker untuk cek status antrian secara berkala
 * Berjalan setiap 15 menit untuk cek apakah giliran user sudah dekat
 */
class AntrianCheckWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    companion object {
        private const val TAG = "AntrianWorker"
    }

    /**
     * Function utama yang dipanggil oleh WorkManager
     * Return Result.success() jika berhasil
     * Return Result.retry() jika gagal dan ingin diulang
     */
    override suspend fun doWork(): Result {
        return try {
            Log.d(TAG, "Checking antrian status...")
            checkAntrianStatus()
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}")
            Result.retry()  // Coba lagi nanti
        }
    }

    /**
     * Cek status antrian user yang sedang login
     */
    private suspend fun checkAntrianStatus() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.d(TAG, "User belum login")
            return
        }

        // Ambil semua antrian user yang statusnya masih aktif
        val appointments = db.collection("appointments")
            .whereEqualTo("userId", currentUser.uid)
            .whereIn("status", listOf("terdaftar", "menunggu"))
            .get()
            .await()

        Log.d(TAG, "Found ${appointments.size()} active appointments")

        // Loop setiap antrian
        for (doc in appointments.documents) {
            val nomorAntrian = doc.getLong("nomorAntrian")?.toInt() ?: continue
            val status = doc.getString("status") ?: continue
            val poli = doc.getString("poli") ?: "Unknown"

            Log.d(TAG, "Processing antrian #$nomorAntrian, status: $status")

            // Simulasi: hitung berapa antrian yang masih menunggu
            // Di production, ambil dari database atau real-time system
            val currentQueue = getCurrentQueueNumber(poli)
            val sisaAntrian = nomorAntrian - currentQueue

            // Kirim notifikasi jika tinggal 1-3 antrian lagi
            if (sisaAntrian in 1..3) {
                val estimasi = sisaAntrian * 10
                Log.d(TAG, "Sending notification: Sisa $sisaAntrian antrian")

                NotificationHelper.sendAntrianUpdateNotification(
                    applicationContext,
                    nomorAntrian,
                    sisaAntrian,
                    estimasi
                )
            }

            // Kirim notifikasi jika sudah giliran
            if (status == "sedang_dilayani" || sisaAntrian <= 0) {
                Log.d(TAG, "Sending notification: Giliran sekarang!")

                NotificationHelper.sendAntrianDipanggilNotification(
                    applicationContext,
                    nomorAntrian
                )
            }
        }
    }

    /**
     * Get nomor antrian yang sedang dilayani
     * TODO: Implementasi dengan real data dari Firestore
     */
    private suspend fun getCurrentQueueNumber(poli: String): Int {
        // Sementara pakai random untuk simulasi
        // Nanti bisa diganti dengan query Firestore yang sesungguhnya
        return (1..5).random()

        /* IMPLEMENTASI REAL (contoh):
        val result = db.collection("appointments")
            .whereEqualTo("poli", poli)
            .whereEqualTo("status", "sedang_dilayani")
            .get()
            .await()

        return result.documents.firstOrNull()
            ?.getLong("nomorAntrian")?.toInt() ?: 0
        */
    }
}