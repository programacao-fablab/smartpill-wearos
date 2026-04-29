package com.smartpillwearos.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartpillwearos.domain.AuthState
import com.smartpillwearos.presentation.utils.DeviceUtils
import com.smartpillwearos.services.SupabaseAuthManager
import com.smartpillwearos.services.supabaseClient
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PairingViewModel : ViewModel() {

    private val manager = SupabaseAuthManager(supabase = supabaseClient)

    val authState: StateFlow<AuthState> = manager.state

    fun startPairing(context: Context) {
        val deviceId = DeviceUtils.getDeviceId(context)
        viewModelScope.launch {
            manager.startPairingFlow(deviceId = deviceId)
        }
    }

    fun retry(context: Context) {
        startPairing(context)
    }
}
