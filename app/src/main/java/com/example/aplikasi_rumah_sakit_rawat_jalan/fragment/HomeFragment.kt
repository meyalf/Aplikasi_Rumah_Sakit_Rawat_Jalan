package com.example.aplikasi_rumah_sakit_rawat_jalan.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.aplikasi_rumah_sakit_rawat_jalan.MainActivity
import com.example.aplikasi_rumah_sakit_rawat_jalan.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val phoneNumber = "081234567890"
    private val whatsappNumber = "6281234567890"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        setupArticleClickListeners()
    }

    private fun setupClickListeners() {
        binding.cardPoliGigi.setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(PoliGigiFragment())
        }

        binding.cardPoliMata.setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(PoliMataFragment())
        }

        binding.cardAntrianSaya.setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(AppointmentFragment())
        }

        binding.cardHubungiRS.setOnClickListener {
            showContactOptions()
        }
    }

    private fun setupArticleClickListeners() {
        binding.cardArtikel1.setOnClickListener {
            openArticleDetail(
                title = "Tips Menjaga Kesehatan Gigi",
                icon = "🦷",
                content = """
                    Kesehatan gigi dan mulut sangat penting untuk kesehatan tubuh secara keseluruhan. Berikut adalah tips lengkap untuk menjaga kesehatan gigi Anda:
                    
                    1. Sikat Gigi dengan Benar
                    - Sikat gigi minimal 2 kali sehari (pagi dan malam sebelum tidur)
                    - Gunakan pasta gigi yang mengandung fluoride
                    - Sikat dengan gerakan memutar selama 2 menit
                    - Jangan lupa sikat lidah untuk menghilangkan bakteri
                    
                    2. Gunakan Benang Gigi (Dental Floss)
                    - Bersihkan sela-sela gigi yang tidak terjangkau sikat
                    - Lakukan minimal 1 kali sehari
                    
                    3. Batasi Konsumsi Makanan Manis
                    - Gula dapat menyebabkan kerusakan gigi
                    - Kurangi minuman bersoda dan permen
                    
                    4. Kunjungi Dokter Gigi Secara Rutin
                    - Periksa gigi minimal 6 bulan sekali
                    - Lakukan pembersihan karang gigi (scaling)
                    
                    5. Hindari Kebiasaan Buruk
                    - Jangan merokok
                    - Hindari menggigit benda keras
                    - Jangan menggunakan gigi untuk membuka kemasan
                    
                    Dengan menerapkan tips di atas secara konsisten, Anda dapat menjaga kesehatan gigi dan mulut dengan optimal.
                """.trimIndent()
            )
        }

        binding.cardArtikel2.setOnClickListener {
            openArticleDetail(
                title = "Cara Merawat Kesehatan Mata",
                icon = "👁️",
                content = """
                    Mata adalah jendela dunia. Merawat kesehatan mata sangat penting di era digital ini. Berikut panduan lengkapnya:
                    
                    1. Aturan 20-20-20
                    - Setiap 20 menit menatap layar
                    - Istirahatkan mata dengan melihat objek sejauh 20 kaki (6 meter)
                    - Selama 20 detik
                    
                    2. Jaga Jarak dengan Layar
                    - Posisikan layar komputer 50-60 cm dari mata
                    - Layar harus sedikit di bawah level mata
                    - Atur kecerahan layar yang nyaman
                    
                    3. Konsumsi Makanan Bergizi
                    - Vitamin A: wortel, bayam, ubi jalar
                    - Omega-3: ikan salmon, tuna
                    - Vitamin C dan E: jeruk, kacang-kacangan
                    
                    4. Lindungi Mata dari Sinar UV
                    - Gunakan kacamata hitam saat di luar ruangan
                    - Pilih kacamata dengan proteksi UV 100%
                    
                    5. Tidur yang Cukup
                    - Tidur 7-8 jam sehari
                    - Mata butuh istirahat untuk regenerasi
                    
                    6. Periksa Mata Secara Rutin
                    - Kunjungi dokter mata minimal 1 tahun sekali
                    - Deteksi dini masalah penglihatan
                    
                    Jangan abaikan kesehatan mata Anda. Pencegahan lebih baik daripada pengobatan!
                """.trimIndent()
            )
        }

        binding.cardArtikel3.setOnClickListener {
            openArticleDetail(
                title = "Pentingnya Pemeriksaan Rutin",
                icon = "💊",
                content = """
                    Medical check-up rutin adalah investasi kesehatan terbaik. Berikut alasan mengapa Anda perlu melakukannya:
                    
                    1. Deteksi Dini Penyakit
                    - Banyak penyakit tidak menunjukkan gejala di tahap awal
                    - Deteksi dini meningkatkan peluang kesembuhan
                    - Contoh: diabetes, hipertensi, kanker
                    
                    2. Pemeriksaan yang Direkomendasikan
                    
                    Usia 20-30 tahun:
                    - Tekanan darah: setiap 2 tahun
                    - Kolesterol: setiap 5 tahun
                    - Gula darah: setiap 3 tahun
                    
                    Usia 30-40 tahun:
                    - Semua pemeriksaan di atas lebih sering
                    - Pemeriksaan mata dan gigi rutin
                    
                    Usia 40+ tahun:
                    - Pemeriksaan jantung
                    - Skrining kanker (sesuai jenis kelamin)
                    - Pemeriksaan tulang (osteoporosis)
                    
                    3. Manfaat Medical Check-up
                    - Mengetahui kondisi kesehatan secara menyeluruh
                    - Mencegah komplikasi penyakit
                    - Menghemat biaya pengobatan jangka panjang
                    - Meningkatkan kualitas hidup
                    
                    4. Persiapan Medical Check-up
                    - Puasa 8-12 jam sebelum pemeriksaan
                    - Bawa riwayat kesehatan keluarga
                    - Catat obat-obatan yang sedang dikonsumsi
                    
                    Jangan tunggu sakit untuk periksa kesehatan. Lakukan medical check-up rutin sekarang!
                """.trimIndent()
            )
        }

        binding.cardArtikel4.setOnClickListener {
            openArticleDetail(
                title = "Manfaat Olahraga Teratur",
                icon = "🏃",
                content = """
                    Olahraga adalah kunci hidup sehat. Hanya 30 menit sehari dapat mengubah hidup Anda. Berikut manfaat lengkapnya:
                    
                    1. Manfaat untuk Jantung
                    - Memperkuat otot jantung
                    - Menurunkan tekanan darah
                    - Meningkatkan sirkulasi darah
                    - Mengurangi risiko penyakit jantung hingga 35%
                    
                    2. Kontrol Berat Badan
                    - Membakar kalori berlebih
                    - Meningkatkan metabolisme
                    - Membentuk massa otot
                    
                    3. Kesehatan Mental
                    - Mengurangi stres dan kecemasan
                    - Meningkatkan mood (endorfin)
                    - Mencegah depresi
                    - Meningkatkan kualitas tidur
                    
                    4. Mencegah Penyakit Kronis
                    - Diabetes tipe 2
                    - Stroke
                    - Beberapa jenis kanker
                    - Osteoporosis
                    
                    5. Jenis Olahraga yang Direkomendasikan
                    
                    Kardio (150 menit/minggu):
                    - Jalan cepat
                    - Jogging
                    - Bersepeda
                    - Berenang
                    
                    Kekuatan (2x/minggu):
                    - Angkat beban
                    - Push-up
                    - Squat
                    
                    6. Tips Memulai Olahraga
                    - Mulai dari yang ringan
                    - Tingkatkan intensitas secara bertahap
                    - Pilih olahraga yang Anda sukai
                    - Ajak teman agar lebih semangat
                    - Konsisten adalah kunci
                    
                    7. Waktu Terbaik Berolahraga
                    - Pagi: meningkatkan energi sepanjang hari
                    - Sore: performa fisik optimal
                    - Yang terpenting: konsisten di waktu yang sama
                    
                    Jangan tunda lagi! Mulai olahraga hari ini untuk hidup lebih sehat dan bahagia.
                """.trimIndent()
            )
        }
    }

    private fun openArticleDetail(title: String, icon: String, content: String) {
        val fragment = ArticleDetailFragment.newInstance(title, icon, content)
        (activity as? MainActivity)?.navigateToFragment(fragment)
    }

    private fun showContactOptions() {
        val options = arrayOf("Telepon", "WhatsApp")
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Hubungi RS")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> callPhone()
                    1 -> openWhatsApp()
                }
            }
            .show()
    }

    private fun callPhone() {
        try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phoneNumber")
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Tidak dapat membuka telepon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openWhatsApp() {
        try {
            val message = "Halo, saya ingin bertanya tentang layanan RS"
            val url = "https://wa.me/$whatsappNumber?text=${Uri.encode(message)}"
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "WhatsApp tidak terinstall", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
