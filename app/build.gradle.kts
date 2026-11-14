
plugins {
    // Android Application
    alias(libs.plugins.android.application)

    // Jetpack Compose
    alias(libs.plugins.kotlin.compose)

    // Kotlin Symbol Processing for Room
    id("com.google.devtools.ksp") version "2.2.10-2.0.2"
}

android {
    namespace = "com.example.zentask"

    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.zentask"

        minSdk = 24
        targetSdk = 37

        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11

        // Supports java.time APIs on older Android versions.
        // Needed for calendar and date-related features.
        isCoreLibraryDesugaringEnabled = true
    }

    buildFeatures {
        compose = true
    }
}

dependencies {

    // ================================================
    // JAVA TIME / CORE LIBRARY DESUGARING
    // ================================================

    coreLibraryDesugaring(
        "com.android.tools:desugar_jdk_libs:2.1.5"
    )

    // ================================================
    // JETPACK COMPOSE
    // ================================================

    implementation(
        platform(libs.androidx.compose.bom)
    )

    implementation(
        libs.androidx.activity.compose
    )

    implementation(
        libs.androidx.compose.material3
    )

    implementation(
        libs.androidx.compose.material.icons.extended
    )

    implementation(
        libs.androidx.navigation.compose
    )

    implementation(
        libs.androidx.lifecycle.viewmodel.compose
    )

    implementation(
        libs.androidx.compose.ui
    )

    implementation(
        libs.androidx.compose.ui.graphics
    )

    implementation(
        libs.androidx.compose.ui.tooling.preview
    )

    // ================================================
    // ANDROIDX CORE AND LIFECYCLE
    // ================================================

    implementation(
        libs.androidx.core.ktx
    )

    implementation(
        libs.androidx.lifecycle.runtime.ktx
    )

    // ================================================
    // ROOM DATABASE
    // ================================================

    val roomVersion = "2.8.5"

    // Room database runtime
    implementation(
        "androidx.room:room-runtime:$roomVersion"
    )

    // Room integration with Kotlin Coroutines
    // and Flow
    implementation(
        "androidx.room:room-ktx:$roomVersion"
    )

    // Room Kotlin code generation
    ksp(
        "androidx.room:room-compiler:$roomVersion"
    )

    // Room database testing
    androidTestImplementation(
        "androidx.room:room-testing:$roomVersion"
    )

    // ================================================
    // UNIT TESTING
    // ================================================

    testImplementation(
        libs.junit
    )

    // ================================================
    // ANDROID INSTRUMENTATION TESTING
    // ================================================

    androidTestImplementation(
        platform(libs.androidx.compose.bom)
    )

    androidTestImplementation(
        libs.androidx.compose.ui.test.junit4
    )

    androidTestImplementation(
        libs.androidx.espresso.core
    )

    androidTestImplementation(
        libs.androidx.junit
    )

    // ================================================
    // DEBUG TOOLS
    // ================================================

    debugImplementation(
        libs.androidx.compose.ui.test.manifest
    )

    debugImplementation(
        libs.androidx.compose.ui.tooling
    )

    // ================================================
    // CAMERAX
    // ================================================

    implementation(
        "androidx.camera:camera-core:1.4.2"
    )

    implementation(
        "androidx.camera:camera-camera2:1.4.2"
    )

    implementation(
        "androidx.camera:camera-lifecycle:1.4.2"
    )

    implementation(
        "androidx.camera:camera-view:1.4.2"
    )

    // ================================================
    // IMAGE METADATA
    // ================================================

    implementation(
        "androidx.exifinterface:exifinterface:1.3.7"
    )

    // ================================================
    // SPLASH SCREEN
    // ================================================

    implementation(
        "androidx.core:core-splashscreen:1.0.1"
    )

    // ================================================
    // GRAPHICS PATH
    // ================================================

    implementation(
        "androidx.graphics:graphics-path:1.0.1"
    )
}
