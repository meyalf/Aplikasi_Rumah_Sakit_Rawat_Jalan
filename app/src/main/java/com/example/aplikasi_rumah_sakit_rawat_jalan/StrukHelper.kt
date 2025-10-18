package com.example.aplikasi_rumah_sakit_rawat_jalan

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.aplikasi_rumah_sakit_rawat_jalan.model.Pendaftaran
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

object StrukHelper {

    fun generateAndSaveStruk(context: Context, pendaftaran: Pendaftaran): String? {
        try {
            // Inflate layout struk
            val inflater = LayoutInflater.from(context)
            val strukView = inflater.inflate(R.layout.layout_struk_antrian, null)

            // Set data ke view
            strukView.findViewById<TextView>(R.id.tv_nomor_antrian).text =
                pendaftaran.nomorAntrian.toString().padStart(3, '0')
            strukView.findViewById<TextView>(R.id.tv_nama_pasien).text =
                pendaftaran.namaPasien
            strukView.findViewById<TextView>(R.id.tv_poli).text =
                pendaftaran.poli
            strukView.findViewById<TextView>(R.id.tv_dokter).text =
                pendaftaran.namaDokter

            // Format tanggal
            val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
            val tanggal = try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = dateFormat.parse(pendaftaran.tanggalKunjungan)
                sdf.format(date ?: Date())
            } catch (e: Exception) {
                pendaftaran.tanggalKunjungan
            }
            strukView.findViewById<TextView>(R.id.tv_tanggal).text = tanggal

            // Status
            val statusTextView = strukView.findViewById<TextView>(R.id.tv_status)
            statusTextView.text = when (pendaftaran.status) {
                "menunggu" -> "Menunggu"
                "selesai" -> "Selesai"
                else -> "Menunggu"
            }
            statusTextView.setTextColor(
                when (pendaftaran.status) {
                    "selesai" -> 0xFFFF9800.toInt()
                    else -> 0xFF4CAF50.toInt()
                }
            )

            // Waktu dicetak
            val waktuCetak = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
                .format(Date())
            strukView.findViewById<TextView>(R.id.tv_waktu_daftar).text =
                "Dicetak: $waktuCetak"

            // Measure & layout view
            strukView.measure(
                View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            strukView.layout(0, 0, strukView.measuredWidth, strukView.measuredHeight)

            // Create bitmap
            val bitmap = Bitmap.createBitmap(
                strukView.measuredWidth,
                strukView.measuredHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            strukView.draw(canvas)

            // Save bitmap
            return saveBitmapToGallery(context, bitmap, pendaftaran)

        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun saveBitmapToGallery(
        context: Context,
        bitmap: Bitmap,
        pendaftaran: Pendaftaran
    ): String? {
        val filename = "Struk_Antrian_${pendaftaran.nomorAntrian}_${System.currentTimeMillis()}.jpg"
        var imageUri: Uri? = null

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Struk Antrian")
                }

                imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                imageUri?.let { uri ->
                    resolver.openOutputStream(uri)?.use { fos ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                    }
                }

            } else {
                // Android 9 ke bawah
                val imagesDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES
                ).toString() + "/Struk Antrian"
                val image = File(imagesDir)
                if (!image.exists()) {
                    image.mkdirs()
                }

                val file = File(imagesDir, filename)
                FileOutputStream(file).use { fos ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                }
                imageUri = Uri.fromFile(file)
            }

            return imageUri?.toString()

        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}