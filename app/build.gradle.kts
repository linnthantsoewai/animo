plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.animo"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.animo"
        minSdk = 24
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    // Core Android KTX libraries - Essential for any modern Android app
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Jetpack Compose - The UI Toolkit
    // The BOM (Bill of Materials) ensures all your Compose libraries are compatible versions
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)

    // Jetpack Navigation - For navigating between screens
    implementation(libs.androidx.navigation.compose)

    // Material3 for XML themes compatibility
    implementation("com.google.android.material:material:1.12.0")

    // Unnecessary for a pure Compose app - This was the conflicting dependency
    // implementation(libs.androidx.appcompat) // Optional, but can be removed
    // implementation(libs.material)          // Should be removed

    // Desugaring for older Android versions (keep this if you had it)
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    // Testing libraries (no changes needed here)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}