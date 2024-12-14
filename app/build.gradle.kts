import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

fun getIpAddress(): String? {
    val properties = Properties()
    val localPropertiesFile = File("local.properties")

    try {
        FileInputStream(localPropertiesFile).use { inputStream ->
            properties.load(inputStream)
        }
        return properties.getProperty("ip_addr") // Replace "ipAddress" with the key you want to retrieve
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return null
}

android {
    namespace = "com.example.eventure"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.eventure"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        val ipAddress = getIpAddress()
        buildConfigField("String", "IP_ADDR", "\"$ipAddress\"")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.viewpager2)
    implementation(libs.recyclerview)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:3.12.1")
    implementation("com.google.code.gson:gson:2.8.8")
    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation("androidx.paging:paging-runtime:2.1.2")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")
}