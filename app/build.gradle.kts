plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // Đặt plugin google-services ở cuối là một thói quen tốt
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.enlearn"
    compileSdk = 34 // Khuyến nghị dùng 34 (bản ổn định) thay vì 35 (preview)

    defaultConfig {
        applicationId = "com.example.enlearn"
        minSdk = 24
        targetSdk = 34 // Nên đồng bộ với compileSdk
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        // Java 17 là tiêu chuẩn mới cho các dự án Android gần đây
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    // THÊM KHỐI NÀY: Chỉ định phiên bản compiler cho Compose
    composeOptions {
        kotlinCompilerExtensionVersion = "2.0.0" // Thay bằng version bạn dùng, ví dụ libs.versions.kotlin.compose.compiler.get()
    }
}

dependencies {
    // --- Core & Splash Screen ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation("androidx.core:core-splashscreen:1.0.0")

    // --- Firebase ---
    implementation(platform("com.google.firebase:firebase-bom:33.1.0")) // Cập nhật phiên bản mới nhất
    implementation("com.google.firebase:firebase-analytics")

    // --- Jetpack Compose ---
    // BOM (Bill of Materials) để quản lý phiên bản các thư viện Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3) // ĐÚNG: Thư viện Material Design cho Compose
    implementation(libs.androidx.navigation.compose)
    // SỬA LẠI: Dùng thư viện foundation chính, không phải foundation-android
    // Bạn cần đảm bảo trong libs.versions.toml có dòng:
    // androidx-foundation = { group = "androidx.compose.foundation", name = "foundation" }
    implementation(libs.androidx.foundation)

    // XÓA BỎ: Các thư viện cho hệ thống View cũ
    // implementation(libs.androidx.appcompat)
    // implementation(libs.material)
    // implementation(libs.androidx.activity)
    // implementation(libs.androidx.constraintlayout)

    // --- Testing ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}