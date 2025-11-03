plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android") // Kotlin
    id("org.jetbrains.kotlin.plugin.compose") // required for Kotlin 2.0+
    id("com.google.devtools.ksp") // for Hilt + Room annotation processing
    id("com.google.dagger.hilt.android") // Hilt plugin
}

android {
    namespace = "com.example.musicplayerapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.musicplayer"
        minSdk = 23
        targetSdk = 36
        versionCode = 1
        versionName = "0.1"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    packaging {
        resources {
            excludes += setOf("META-INF/AL2.0", "META-INF/LGPL2.1")
        }
    }
}

dependencies {
    // Compose BOM (keeps compose libs aligned)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose.v193)

    // Jetpack Compose UI (BOM will provide specific versions)
    implementation(libs.ui)
    implementation(libs.material3)
    implementation(libs.ui.tooling.preview)
    debugImplementation(libs.ui.tooling)

    // Lifecycle / ViewModel
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Media3 (ExoPlayer via Media3)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.session) // for sessions/notifications if later needed

    // Room (DB for playlists/favorites)
    implementation(libs.androidx.room.runtime.v272)
    implementation(libs.androidx.room.ktx.v272)
    ksp(libs.androidx.room.compiler.v272)

    // Hilt (DI)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Coil (Compose) for album art
    implementation(libs.coil.compose)

    // Optional: Accompanist (system UI control, insets)
    implementation(libs.accompanist.systemuicontroller)

    // Testing / debug helpers
    testImplementation(libs.junit)
}