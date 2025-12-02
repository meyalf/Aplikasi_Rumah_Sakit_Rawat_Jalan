plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
    id("kotlin-kapt") // TAMBAHKAN INI untuk Room & annotation processing
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.aplikasi_rumah_sakit_rawat_jalan"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.aplikasi_rumah_sakit_rawat_jalan"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // AndroidX Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.0")) // Update ke versi terbaru
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")

    // Fragment & RecyclerView
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // ========== TAMBAHKAN DEPENDENCIES BARU DI SINI ========== //

    // Lifecycle & ViewModel (untuk MVVM Architecture)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    // Activity KTX (untuk viewModels delegate)
    implementation("androidx.activity:activity-ktx:1.8.2")

    // Coroutines (untuk async operations)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // WorkManager (untuk background tasks)
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // CardView (untuk UI cards)
    implementation("androidx.cardview:cardview:1.0.0")

    // CoordinatorLayout (untuk advanced UI layouts)
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")

    // SwipeRefreshLayout (optional - untuk pull to refresh)
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // Room Database (OPTIONAL - untuk offline caching)
    // Uncomment jika mau pakai Room
    // implementation("androidx.room:room-runtime:2.6.1")
    // implementation("androidx.room:room-ktx:2.6.1")
    // kapt("androidx.room:room-compiler:2.6.1")
}