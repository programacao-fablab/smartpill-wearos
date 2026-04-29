package com.smartpillwearos.services

import com.smartpillwearos.domain.AuthState
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.realtime.broadcastFlow
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.realtime
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

@Serializable
private data class TokenResponse(val token: String)

class SupabaseAuthManager(private val supabase: SupabaseClient? = null) {
    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state.asStateFlow()

    suspend fun startPairingFlow(deviceId: String = "dummy_device", mockedTokens: Boolean = false) {
        if (mockedTokens) {
            _state.value = AuthState.GeneratingQR
            delay(50)
            _state.value = AuthState.WaitingForMobileScan("mock_qr_token_123")
            delay(50)
            _state.value = AuthState.Authenticating
            delay(50)
            _state.value = AuthState.Success("mocked_user_id_999")
            return
        }

        generatePairingToken(deviceId)
    }

    private suspend fun generatePairingToken(deviceId: String) {
        if (supabase == null) return
        _state.value = AuthState.GeneratingQR
        try {
            val response = supabase.functions.invoke("create-pairing-token", mapOf("deviceId" to deviceId))
            val jsonString = response.bodyAsText()
            val data = Json.decodeFromString<TokenResponse>(jsonString)
            _state.value = AuthState.WaitingForMobileScan(qrToken = data.token)
            
            listenForMobileApproval(deviceId)
        } catch (e: Exception) {
            _state.value = AuthState.Error(e.message ?: "Erro ao gerar token")
        }
    }

    private suspend fun listenForMobileApproval(deviceId: String) {
        if (supabase == null) return
        try {
            val channel = supabase.realtime.channel("auth-device:$deviceId")
            channel.subscribe()
            
            val flow = channel.broadcastFlow<JsonObject>("login-token")
            val payload = flow.first() // Blocks until the first emission
            
            channel.unsubscribe() // Unsubscribe immediately after receiving the payload
            
            _state.value = AuthState.Authenticating
            
            val accessToken = payload["access_token"]?.jsonPrimitive?.contentOrNull
            val refreshToken = payload["refresh_token"]?.jsonPrimitive?.contentOrNull
            
            if (accessToken != null && refreshToken != null) {
                try {
                    supabase.auth.importAuthToken(
                        accessToken = accessToken,
                        refreshToken = refreshToken,
                        autoRefresh = true
                    )
                    
                    val user = supabase.auth.currentUserOrNull()
                    val userId = user?.id ?: "unknown_id"
                    _state.value = AuthState.Success(userId)
                } catch (e: Exception) {
                    _state.value = AuthState.Error("Falha ao importar token: ${e.message}")
                }
            } else {
                _state.value = AuthState.Error("Payload sem tokens válidos")
            }
        } catch (e: Exception) {
            _state.value = AuthState.Error("Erro no servidor de tempo real: ${e.message}")
        }
    }
}
