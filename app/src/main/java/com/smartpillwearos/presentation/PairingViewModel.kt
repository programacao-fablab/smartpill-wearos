package com.smartpillwearos.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartpillwearos.domain.AuthState
import com.smartpillwearos.presentation.utils.DeviceUtils
import com.smartpillwearos.services.SupabaseAuthManager
import com.smartpillwearos.services.supabaseClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PairingViewModel : ViewModel() {

    private val manager = SupabaseAuthManager(supabase = supabaseClient)

    val authState: StateFlow<AuthState> = manager.state

    private val _deviceId = MutableStateFlow<String?>(null)
    val deviceId: StateFlow<String?> = _deviceId.asStateFlow()

    fun startPairing(context: Context) {
        val id = DeviceUtils.getDeviceId(context)
        _deviceId.value = id
        viewModelScope.launch {
            manager.startPairingFlow(deviceId = id)
        }
    }

    fun retry(context: Context) {
        startPairing(context)
    }
}
