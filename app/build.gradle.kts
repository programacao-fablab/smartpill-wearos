plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
}
android {
    namespace = "com.smartpillwearos"
    compileSdk = 36 // Você pode usar a SDK 34 ou 35

    defaultConfig {
        applicationId = "com.smartpillwearos"
        minSdk = 30 // SDK Mínima para Wear OS 3
        targetSdk = 34 // SDK Alvo
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false // Mantenha 'false' durante o desenvolvimento
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10" // Compatível com Kotlin 1.9.22
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
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.3")
    implementation("androidx.activity:activity-compose:1.9.0")
    // Necessário para 'multiDexEnabled = true'
    implementation("androidx.multidex:multidex:2.0.1")

    // --- INTERFACE (COMPOSE PARA WEAR OS) ---
    // Componentes principais de UI (botões, listas, etc)
    implementation("androidx.wear.compose:compose-material:1.3.1")
    // Fundação do Compose (colunas, linhas, etc)
    implementation("androidx.wear.compose:compose-foundation:1.3.1")
    // Navegação entre telas no Wear OS
    implementation("androidx.wear.compose:compose-navigation:1.3.1")
    // Ferramentas para pré-visualização no Android Studio
    debugImplementation("androidx.compose.ui:ui-tooling-preview:1.6.8")
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.8")

    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.wear.watchface:watchface-complications-data-source-ktx:1.2.1")
    // --- COMUNICAÇÃO (CELULAR <-> RELÓGIO) ---
    // A Data Layer API (MessageClient, etc.)
    implementation("com.google.android.gms:play-services-wearable:18.2.0")

    // --- BIBLIOTECA DO SUPABASE (RECOMENDADO) ---
    // Cliente principal do Supabase (use a versão mais recente)



//    implementation(platform("io.github.jan-tennert.supabase:bom:3.2.6"))
//    implementation("io.github.jan-tennert.supabase:postgrest-kt")
//    implementation("io.github.jan-tennert.supabase:auth-kt")
//    implementation("io.github.jan-tennert.supabase:realtime-kt")

    // --- SUPABASE (Versão 2.6.1) ---
    val supabaseVersion = "2.6.1"

    implementation("io.github.jan-tennert.supabase:gotrue-kt:${supabaseVersion}")
    implementation("io.github.jan-tennert.supabase:postgrest-kt:${supabaseVersion}")
    implementation("io.github.jan-tennert.supabase:functions-kt:${supabaseVersion}")
    implementation("io.github.jan-tennert.supabase:realtime-kt:${supabaseVersion}")

    // O motor Ktor (necessário para a rede)
    implementation("io.ktor:ktor-client-cio:2.3.12")


    // --- GERAÇÃO DO QR CODE ---
    // Biblioteca principal para gerar o QR Code a partir do token
    implementation("com.google.zxing:core:3.5.3")


    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
}