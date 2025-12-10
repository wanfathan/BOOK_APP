plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // ==================== FIREBASE PLUGIN ====================
    // Required for Firebase to function
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.book_app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.book_app"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // ==================== FIREBASE DEPENDENCIES ====================
    // Import the Firebase BoM (Bill of Materials)
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    // Firebase Authentication
    implementation("com.google.firebase:firebase-auth")
    // Cloud Firestore
    implementation("com.google.firebase:firebase-firestore")

    implementation("com.github.bumptech.glide:glide:4.16.0")

}