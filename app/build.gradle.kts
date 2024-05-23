plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("dagger.hilt.android.plugin")
    id ("kotlin-kapt")
    id("com.google.devtools.ksp")

}

android {
    namespace = "com.example.priceflux"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.priceflux"
        minSdk = 26
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.play.services.analytics.impl)
    implementation(libs.androidx.hilt.common)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

//    extended icons
    implementation (libs.androidx.material.icons.extended)

//    scrape website
    implementation (libs.jsoup)

//    barcode
    implementation (libs.barcode.scanning)

//    camera
    implementation (libs.androidx.camera.camera2)
    implementation (libs.androidx.camera.lifecycle)
    implementation (libs.androidx.camera.view)

    // Zxing
    implementation ("com.google.zxing:core:3.3.3")

    //Dagger - Hilt
    implementation (libs.hilt.android)
    kapt (libs.hilt.android.compiler)
    kapt (libs.androidx.hilt.compiler)
    implementation (libs.androidx.hilt.navigation.compose)

////    retrofit
//    implementation(libs.retrofit)
//    implementation (libs.converter.moshi)
//    implementation (libs.logging.interceptor)

//    implementation (libs.androidx.lifecycle.viewmodel.compose)

    // Room components
    implementation (libs.androidx.room.runtime)
    implementation (libs.androidx.room.ktx)
    kapt (libs.androidx.room.compiler)

//    implementation (libs.accompanist.swiperefresh)
//    coil
    implementation(libs.coil.compose)

//    lottie
    implementation (libs.lottie.compose)

//    swipe
    implementation (libs.swipe)

//work manager
    implementation (libs.androidx.work.runtime.ktx)
    implementation (libs.androidx.hilt.work)




}