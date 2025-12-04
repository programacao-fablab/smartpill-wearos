package com.example.smartpill_wearos.presentation

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.functions.Functions

const val SUPABASE_URL = "https://onkvljxfsawyigkdrmkg.supabase.co"
const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im9ua3Zsanhmc2F3eWlna2RybWtnIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc1OTM1MDk0MiwiZXhwIjoyMDc0OTI2OTQyfQ.zdAgu6IzcM5gADuEUQ-11s1QVNlmCswNfUiSv3PPL7U"

val supabase = createSupabaseClient(
    supabaseUrl = SUPABASE_URL,
    supabaseKey = SUPABASE_KEY
) {
    install(Auth)
    install(Postgrest)
    install(Functions)
}