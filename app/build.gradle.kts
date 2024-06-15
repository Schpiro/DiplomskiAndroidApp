plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.hiltAndroid)
}

android {
    namespace = "com.bbilandzi.diplomskiandroidapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.bbilandzi.diplomskiandroidapp"
        minSdk = 33
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    viewBinding {
        enable = true
    }
    dataBinding {
        enable = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.retrofit)
    implementation(libs.logging.interceptor)
    implementation(libs.converter.gson)
    implementation(libs.hilt.android)
    implementation(libs.annotation)
    implementation(libs.databinding.runtime)
    annotationProcessor(libs.hilt.android.compiler)
    compileOnly(libs.android)
}