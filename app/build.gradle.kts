// ARQUIVO: smartpill-wearos/app/build.gradle.kts
// Este é o arquivo de build do MÓDULO do seu aplicativo.

// 1. Bloco de PLUGINS
// Aplica as ferramentas de build para este módulo específico.
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

// 2. Bloco ANDROID
// Define as configurações específicas do seu app Android (Wear OS).
android {
    // MUDE "com.seu.pacote.smartpillwearos" para o seu pacote real.
    namespace = "com.seu.pacote.smartpillwearos"
    compileSdk = 34 // Você pode usar a SDK 34 ou 35

    defaultConfig {
        // MUDE "com.seu.pacote.smartpillwearos" para o seu pacote real.
        applicationId = "com.seu.pacote.smartpillwearos"
        minSdk = 30 // SDK Mínima para Wear OS 3
        targetSdk = 34 // SDK Alvo
        versionCode = 1
        versionName = "1.0"

        // Necessário para bibliotecas mais antigas do Android
        multiDexEnabled = true
        //wearApp = true // Informa ao Android que este é um app Wear OS
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

    // Habilita o Jetpack Compose
    buildFeatures {
        compose = true
    }

    // Define a versão do compilador do Compose
    // Esta versão deve ser compatível com a sua versão do Kotlin
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10" // Compatível com Kotlin 1.9.22
    }

    // Configurações de compilação
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    packaging {
        resources {
            // Evita conflitos de duplicação de arquivos
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

// 3. Bloco DEPENDENCIES
// Lista de todas as bibliotecas que seu app utilizará.
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

    // --- COMUNICAÇÃO (CELULAR <-> RELÓGIO) ---
    // A Data Layer API (MessageClient, etc.)
    implementation("com.google.android.gms:play-services-wearable:18.2.0")

    // --- BIBLIOTECA DO SUPABASE (RECOMENDADO) ---
    // Cliente principal do Supabase (use a versão mais recente)

    implementation(platform("io.github.jan-tennert.supabase:bom:3.2.6"))
    implementation("io.github.jan-tennert.supabase:postgrest-kt")
    implementation("io.github.jan-tennert.supabase:auth-kt")
    implementation("io.github.jan-tennert.supabase:realtime-kt")

    // O motor Ktor (necessário para a rede)
    implementation("io.ktor:ktor-client-cio:2.3.12")


    // --- GERAÇÃO DO QR CODE ---
    // Biblioteca principal para gerar o QR Code a partir do token
    implementation("com.google.zxing:core:3.5.3")
}