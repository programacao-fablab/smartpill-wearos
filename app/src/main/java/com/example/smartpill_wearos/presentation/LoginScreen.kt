package com.example.smartpill_wearos.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.rememberScalingLazyListState
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.launch

@Composable
fun LoginTestScreen() {
    var statusMessage by remember { mutableStateOf("Toque para logar") }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val listState = rememberScalingLazyListState()

    Scaffold(
        positionIndicator = { PositionIndicator(scalingLazyListState = listState) }
    ) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            autoCentering = null
        ) {
            item {
                Text(
                    text = "SmartPill Login",
                    color = MaterialTheme.colors.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Text(
                    text = statusMessage,
                    textAlign = TextAlign.Center,
                    color = if (statusMessage.contains("Erro")) Color.Red else Color.Green,
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    Chip(
                        label = { Text("Fazer Login") },
                        onClick = {
                            scope.launch {
                                isLoading = true
                                statusMessage = "Conectando..."
                                try {
                                    // *** IMPORTANTE: Use um email/senha real criado no seu Supabase ***
                                    supabase.auth.signInWith(Email) {
                                        email = "rmda.eai25@uea.edu.br"
                                        password = "roger0304"
                                    }

                                    val user = supabase.auth.currentUserOrNull()
                                    statusMessage = "Sucesso! ID:\n${user?.id?.take(5)}..."
                                } catch (e: Exception) {
                                    statusMessage = "Erro: ${e.message}"
                                    e.printStackTrace()
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        colors = ChipDefaults.primaryChipColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}