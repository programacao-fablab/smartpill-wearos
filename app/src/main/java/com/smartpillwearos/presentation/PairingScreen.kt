package com.smartpillwearos.presentation

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.smartpillwearos.domain.AuthState
import com.smartpillwearos.presentation.utils.QRCodeUtils

@Composable
fun PairingScreen(
    onAuthenticationSuccess: () -> Unit,
    vm: PairingViewModel = viewModel()
) {
    val context = LocalContext.current
    val authState by vm.authState.collectAsState()
    val deviceId by vm.deviceId.collectAsState()

    // Dispara o fluxo apenas uma vez quando a tela entra no estado Idle
    LaunchedEffect(Unit) {
        vm.startPairing(context)
    }

    // Reage ao estado Success navegando para a Home e destruindo esta tela
    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onAuthenticationSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        when (val state = authState) {
            is AuthState.Idle, is AuthState.GeneratingQR, is AuthState.Authenticating -> {
                val label = when (state) {
                    is AuthState.GeneratingQR -> "Gerando código..."
                    is AuthState.Authenticating -> "Autenticando..."
                    else -> "Aguarde..."
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(label, fontSize = 12.sp, color = Color.White)
                }
            }

            is AuthState.WaitingForMobileScan -> {
                val qrContent = deviceId ?: state.qrToken
                val qrBitmap = remember(qrContent) {
                    QRCodeUtils.generateQRCode(qrContent)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Escaneie no App", fontSize = 12.sp, color = Color.White)
                    qrBitmap?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "QR Code",
                            modifier = Modifier.size(120.dp)
                        )
                    }
                    Text(
                        text = qrContent,
                        fontSize = 10.sp,
                        color = MaterialTheme.colors.primary
                    )
                }
            }

            is AuthState.Success -> {
                Log.d("SmartPillDebug", "SUCESSO")
                // LaunchedEffect acima cuida da navegação — UI permanece vazia enquanto transita
                Box {}
            }

            is AuthState.Error -> {
                Log.d("SmartPillDebug", state.message)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = state.message,
                        color = Color.Red,
                        textAlign = TextAlign.Center,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { vm.retry(context) }) {
                        Text("Tentar Novamente", fontSize = 10.sp)
                    }
                }
            }
        }
    }
}