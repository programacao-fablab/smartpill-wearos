package com.example.smartpill_wearos.presentation

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.smartpill_wearos.presentation.utils.DeviceUtils
import com.example.smartpill_wearos.presentation.utils.QRCodeUtils

// --- IMPORTS CORRIGIDOS ---
// Importa a sua variável global do outro arquivo
import com.example.smartpill_wearos.presentation.supabaseClient

import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.realtime.realtime // Acesso ao Realtime via .realtime
import io.github.jan.supabase.realtime.RealtimeChannel
import io.github.jan.supabase.realtime.broadcastFlow
import io.github.jan.supabase.realtime.channel

// Imports de Serialização (Essenciais para corrigir o erro de 'null')
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.coroutines.launch
import io.ktor.client.statement.bodyAsText

@Serializable
data class TokenResponse(val token: String)

@Composable
fun PairingScreen(
    onPairingSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // CORREÇÃO 1: Usamos a variável global 'supabaseClient' que você definiu
    val supabase = remember { supabaseClient }

    var qrCodeBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var pairingToken by remember { mutableStateOf<String?>(null) }
    var deviceId by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var statusMessage by remember { mutableStateOf("Gerando código...") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // 1. Gerar Código (Edge Function)
    LaunchedEffect(Unit) {
        try {
            val id = DeviceUtils.getDeviceId(context)
            deviceId = id
            val response = supabase.functions.invoke("create-pairing-token", mapOf("deviceId" to id))
            val jsonString = response.bodyAsText()
            val data = Json.decodeFromString<TokenResponse>(jsonString)

            pairingToken = data.token
            qrCodeBitmap = QRCodeUtils.generateQRCode(deviceId.toString())
            statusMessage = "Aguardando celular..."
            isLoading = false
        } catch (e: Exception) {
            e.printStackTrace()
            errorMessage = "Erro: ${e.message}"
            isLoading = false
        }
    }

    // 2. Realtime (Ouvir o Login)
    if (deviceId != null) {
        DisposableEffect(deviceId) {
            var channel: RealtimeChannel? = null

            try {

                channel = supabase.realtime.channel("auth-device:$deviceId")
                Log.d("LOGSS", " Ouvindo o canal: $deviceId")
                Log.d("LOGSS", " Ouvindo o canal: auth-device:$deviceId")

                scope.launch {
                    channel.status.collect { status ->
                        Log.d("SmartPillDebug", ">>> STATUS DO CANAL: $status")
                        // Se ficar só em 'CONNECTING' e nunca 'SUBSCRIBED', é problema de internet no relógio
                    }
                }

                scope.launch {

                    channel
                        .broadcastFlow<JsonObject>("login-token")
                        .collect { payload ->
                            Log.d("LOGSS", "Mensagem recebida no canal")

                            try {

                                Log.d("SmartPillDebug", "Entrou aqui")

                                val refreshToken =
                                    payload["refresh_token"]
                                        ?.jsonPrimitive
                                        ?.contentOrNull

                                if (refreshToken != null) {
                                    Log.d("SmartPillDebug", "Entrou acola")

                                    statusMessage = "Autenticando..."
                                    isLoading = true

                                    supabase.auth.refreshSession(
                                        refreshToken = refreshToken
                                    )

                                    onPairingSuccess()
                                }

                            } catch (e: Exception) {
                                Log.d("LOGSS", " AAAAAAAAAAAAA ${e.message}")

                                Log.e("Auth", "Erro no login: ${e.message}")
                            }
                        }
                }

                scope.launch {
                    channel.subscribe()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("LOGSS", "BBBBBBBBBBB ${e.message}")

                errorMessage = "Erro Realtime: ${e.message}"
            }

            onDispose {
                scope.launch {
                    channel?.unsubscribe()
                }
            }
        }
}

    // UI (Mantém igual)
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(8.dp))
                Text(statusMessage, fontSize = 12.sp)
            }
        } else if (errorMessage != null) {
            Text(errorMessage!!, color = Color.Red, textAlign = TextAlign.Center)
        } else if (qrCodeBitmap != null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Escaneie no App", fontSize = 12.sp, color = Color.White)
                Image(qrCodeBitmap!!.asImageBitmap(), "QR", Modifier.size(120.dp))
                Text(pairingToken ?: "", fontSize = 14.sp, color = MaterialTheme.colors.primary)
            }
        }
    }
}