plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "aki.pawar.qr"
    compileSdk = 36

    defaultConfig {
        applicationId = "aki.pawar.qr"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core Android
    implementation(libs.androidx.core.ktx)
    
    // Compose
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    
    // Testing
    testImplementation(libs.junit)
    testImplementation("org.mockito:mockito-core:5.8.0")
    testImplementation("org.mockito:mockito-inline:5.2.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    
    // Debug
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    
    // Amplify core dependency
    implementation("com.amplifyframework:core:2.30.4")

    // Support for modern Java features
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")

    implementation ("com.amplifyframework.ui:liveness:1.5.0")

    // Amplify Auth dependency (unnecessary if using your own credentials provider)
    implementation ("com.amplifyframework:aws-auth-cognito:2.29.0")

    // Retrofit for API calls
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    
    // QR Code Generation - ZXing
    implementation("com.google.zxing:core:3.5.3")
    
    // QR Code Scanning - ML Kit Barcode Scanning
    implementation("com.google.mlkit:barcode-scanning:17.3.0")
    
    // CameraX for camera preview
    implementation("androidx.camera:camera-camera2:1.4.1")
    implementation("androidx.camera:camera-lifecycle:1.4.1")
    implementation("androidx.camera:camera-view:1.4.1")
    
    // Material Icons Extended for icons
    implementation("androidx.compose.material:material-icons-extended:1.7.6")
    
    // Navigation for Compose
    implementation("androidx.navigation:navigation-compose:2.8.5")
    
    // Accompanist for permissions handling
    implementation("com.google.accompanist:accompanist-permissions:0.36.0")
    
    // ViewModel and Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
}
