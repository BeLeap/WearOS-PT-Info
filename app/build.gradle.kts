import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("com.google.protobuf") version "0.8.19"
    kotlin("plugin.serialization") version "1.5.31"
}

val apiKeyPropertiesFile = rootProject.file("apiKey.properties")
val apiKeyProperties = Properties()
apiKeyProperties.load(FileInputStream(apiKeyPropertiesFile))

android {
    compileSdk = 32

    defaultConfig {
        applicationId = "codes.beleap.wearos_pt_info"
        minSdk = 30
        targetSdk = 32
        versionCode = 13
        versionName = "0.0.13"

        buildConfigField("String", "SUBWAY_INFO_API_KEY",
            apiKeyProperties["SUBWAY_INFO_API_KEY"] as String
        )
    }

    buildFeatures {
        compose = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            setProguardFiles(listOf("proguard-android-optimize.txt", "proguard-rules.pro"))
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildToolsVersion = "30.0.3"
}

dependencies {
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("com.google.android.gms:play-services-wearable:17.1.0")
    implementation("androidx.percentlayout:percentlayout:1.0.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.2.1")

    // General Jetpack Compose
    implementation("androidx.activity:activity-compose:1.5.1")
    implementation("androidx.compose.ui:ui-tooling-preview:1.2.0")
    implementation("androidx.compose.material:material-icons-extended:1.2.0")

    // WearOS Jetpack Compose
    implementation("androidx.wear.compose:compose-foundation:1.0.0")
    implementation("androidx.wear.compose:compose-material:1.0.0")
    implementation("androidx.wear.compose:compose-navigation:1.0.0")

    // Retrofit2
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.9.3")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")

    // Settings
    implementation("androidx.datastore:datastore:1.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
}