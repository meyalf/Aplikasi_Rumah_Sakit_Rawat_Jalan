package com.example.aplikasi_rumah_sakit_rawat_jalan.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.aplikasi_rumah_sakit_rawat_jalan.MainActivity
import com.example.aplikasi_rumah_sakit_rawat_jalan.R

/**
 * Helper untuk manage notifications di aplikasi
 * Digunakan untuk notifikasi update antrian
 */
object NotificationHelper {

    // Konstanta untuk notification channel
    private const val CHANNEL_ID = "antrian_channel"
    private const val CHANNEL_NAME = "Notifikasi Antrian"
    private const val CHANNEL_DESC = "Notifikasi untuk update antrian pasien"

    /**
     * Membuat notification channel (diperlukan untuk Android 8.0+)
     * Panggil ini di onCreate MainActivity atau Application class
     */
    fun createNotificationChannel(context: Context) {
        // Notification channel hanya diperlukan di Android 8.0 (API 26) ke atas
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableVibration(true)  // Aktifkan vibration
                enableLights(true)      // Aktifkan LED notification
            }

            // Register channel ke system
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Kirim notifikasi generic
     */
    fun sendAntrianNotification(
        context: Context,
        title: String,
        message: String,
        nomorAntrian: Int
    ) {
        // Intent untuk buka MainActivity ketika notifikasi diklik
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Build notification
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_doctor)  // Icon kecil di status bar
            .setContentTitle(title)               // Judul notifikasi
            .setContentText(message)              // Isi singkat
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))  // Isi lengkap
            .setPriority(NotificationCompat.PRIORITY_HIGH)  // Priority tinggi
            .setAutoCancel(true)                  // Auto dismiss ketika diklik
            .setContentIntent(pendingIntent)      // Action ketika diklik
            .setVibrate(longArrayOf(1000, 1000, 1000))  // Pattern vibration
            .build()

        // Tampilkan notifikasi
        with(NotificationManagerCompat.from(context)) {
            notify(nomorAntrian, notification)  // ID = nomor antrian
        }
    }

    /**
     * Notifikasi: Antrian hampir tiba
     * Dipanggil ketika tinggal 3 antrian lagi
     */
    fun sendAntrianUpdateNotification(
        context: Context,
        nomorAntrian: Int,
        sisaAntrian: Int,
        estimasiMenit: Int
    ) {
        val title = "⏰ Giliran Anda Segera Tiba!"
        val message = """
            Nomor Antrian: $nomorAntrian
            Sisa Antrian: $sisaAntrian orang
            Estimasi Waktu: ±$estimasiMenit menit
            
            Mohon bersiap ke ruang tunggu.
        """.trimIndent()

        sendAntrianNotification(context, title, message, nomorAntrian)
    }

    /**
     * Notifikasi: Giliran Anda SEKARANG
     * Dipanggil ketika nomor antrian dipanggil
     */
    fun sendAntrianDipanggilNotification(
        context: Context,
        nomorAntrian: Int
    ) {
        val title = "🔔 GILIRAN ANDA SEKARANG!"
        val message = "Nomor Antrian $nomorAntrian dipanggil. Silakan menuju ruang pemeriksaan."

        sendAntrianNotification(context, title, message, nomorAntrian)
    }
}