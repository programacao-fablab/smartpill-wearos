package com.smartpillwearos.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.smartpillwearos.ui.MainClockScreen
import com.smartpillwearos.ui.MainViewModel

@Composable
fun HomeScreen(
    onLoggedOut: () -> Unit = {},
    vm: MainViewModel = viewModel()
) {
    val userName by vm.userName.collectAsState()
    val loggedOut by vm.loggedOut.collectAsState()

    // Reacts to logout and ejects back to PairingScreen
    LaunchedEffect(loggedOut) {
        if (loggedOut) {
            onLoggedOut()
        }
    }

    MainClockScreen(
        userName = userName,
        onLogout = { vm.logout() }
    )
}