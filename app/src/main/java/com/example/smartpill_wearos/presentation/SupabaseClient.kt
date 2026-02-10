package com.example.smartpill_wearos.presentation

import com.smartpillwearos.BuildConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.functions.Functions
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.serialization.json.Json

const val SUPABASE_URL = BuildConfig.SUPABASE_URL
const val SUPABASE_KEY = BuildConfig.SUPABASE_KEY


val supabaseClient = createSupabaseClient(
    supabaseUrl = SUPABASE_URL,
    supabaseKey = SUPABASE_KEY
) {
    install(Auth)
    install(Postgrest)
    install(Functions)
    install(Realtime)

    defaultSerializer = KotlinXSerializer(Json {
        ignoreUnknownKeys = true
    })
}