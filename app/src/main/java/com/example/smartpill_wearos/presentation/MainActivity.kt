/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.example.smartpill_wearos.presentation


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.example.smartpill_wearos.presentation.theme.SmartpillwearosTheme
import io.github.jan.supabase.gotrue.auth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartpillwearosTheme {
                SmartPillApp()
            }
        }
    }
}

@Composable
fun SmartPillApp() {
    // Controlador de navegação específico para Wear OS
    val navController = rememberSwipeDismissableNavController()

    // Verifica se já existe um usuário logado no SupabaseClient
    // Se tiver, começa na Home. Se não, começa no Pairing.
    val startDestination = if (supabaseClient.auth.currentSessionOrNull() != null) "home" else "pairing"

    SwipeDismissableNavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        // Rota da Tela de Pareamento
        composable("pairing") {
            PairingScreen(
                onPairingSuccess = {
                    // Navega para a Home e REMOVE a tela de login da pilha
                    // Assim o usuário não volta pro QR Code se arrastar pra voltar
                    navController.navigate("home") {
                        popUpTo("pairing") { inclusive = true }
                    }
                }
            )
        }

        // Rota da Tela Principal (Home)
        composable("home") {
            HomeScreen() // Vamos criar essa tela agora
        }
    }
}