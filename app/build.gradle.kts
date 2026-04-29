import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

android {
    namespace = "com.smartpillwearos"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.smartpillwearos"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true

        val supabaseUrl = localProperties.getProperty("SUPABASE_URL") ?: ""
        val supabaseKey = localProperties.getProperty("SUPABASE_KEY") ?: ""

        buildConfigField("String", "SUPABASE_URL", "\"$supabaseUrl\"")
        buildConfigField("String", "SUPABASE_KEY", "\"$supabaseKey\"")
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

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}
dependencies {
    // --- BASE DO ANDROID E KOTLIN ---
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.3")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.multidex:multidex:2.0.1")

    // --- INTERFACE (COMPOSE PARA WEAR OS) ---
    implementation("androidx.wear.compose:compose-material:1.3.1")
    implementation("androidx.wear.compose:compose-foundation:1.3.1")
    implementation("androidx.wear.compose:compose-navigation:1.3.1")
    debugImplementation("androidx.compose.ui:ui-tooling-preview:1.6.8")
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.8")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.wear.watchface:watchface-complications-data-source-ktx:1.2.1")

    // --- COMUNICAÇÃO ---
    implementation("com.google.android.gms:play-services-wearable:18.2.0")

    // --- SUPABASE ---
    val supabaseVersion = "2.6.1"
    val ktorVersion = "2.3.12"

    implementation("io.github.jan-tennert.supabase:gotrue-kt:$supabaseVersion")
    implementation("io.github.jan-tennert.supabase:postgrest-kt:$supabaseVersion")
    implementation("io.github.jan-tennert.supabase:realtime-kt:$supabaseVersion")
    implementation("io.github.jan-tennert.supabase:functions-kt:$supabaseVersion")

    // --- MOTOR DE REDE (KTOR) ---
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")

    // --- SERIALIZAÇÃO ---
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // --- QR CODE ---
    implementation("com.google.zxing:core:3.5.3")

    // --- TESTING ---
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.compose.ui:ui-test:1.6.8")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.8")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.6.8")
}
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}
