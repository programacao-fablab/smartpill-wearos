package com.example.smartpill_wearos.presentation

import android.graphics.Bitmap
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import io.github.jan.supabase.functions.functions

import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString // <--- Importante
import kotlinx.serialization.Serializable // <--- Importante

import io.ktor.client.statement.bodyAsText

// Classe para receber a resposta JSON da Edge Function
@Serializable
data class TokenResponse(val token: String)

@Composable
fun PairingScreen() {
    val context = LocalContext.current

    // Estados da Tela
    var qrCodeBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var pairingToken by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Este bloco roda assim que a tela abre
    LaunchedEffect(Unit) {
        try {
            // 1. Pegar o ID do Relógio
            val deviceId = DeviceUtils.getDeviceId(context)

            // 2. Chamar a Edge Function
            // A 'response' aqui é o pacote HTTP completo
            val response = supabase.functions.invoke("create-pairing-token", mapOf("deviceId" to deviceId))

            // 3. Extrair o texto JSON de dentro do pacote
            val jsonString = response.bodyAsText() // <--- AQUI ESTÁ A CORREÇÃO

            // 4. Agora sim, decodificamos o texto
            val data = Json.decodeFromString<TokenResponse>(jsonString)

            pairingToken = data.token

            // 5. Gerar o QR Code a partir do token
            qrCodeBitmap = QRCodeUtils.generateQRCode(data.token)

            // TODO: Aqui iniciaremos o Realtime (Próximo passo)

            isLoading = false

        } catch (e: Exception) {
            e.printStackTrace()
            errorMessage = "Erro: ${e.message}"
            isLoading = false
        }
    }

    // UI (Interface)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(8.dp))
                Text("Gerando QR Code...")
            }
        } else if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = Color.Red,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        } else if (qrCodeBitmap != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize().padding(20.dp)
            ) {
                Text(
                    text = "Escaneie no Celular",
                    fontSize = 12.sp,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Exibe o QR Code
                Image(
                    bitmap = qrCodeBitmap!!.asImageBitmap(),
                    contentDescription = "QR Code de Pareamento",
                    modifier = Modifier.size(140.dp)
                )

                // Mostra o código em texto também (caso a câmera falhe)
                Text(
                    text = pairingToken ?: "",
                    fontSize = 16.sp,
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier.padding(top = 8.dp),
                    style = MaterialTheme.typography.title2
                )
            }
        }
    }
}