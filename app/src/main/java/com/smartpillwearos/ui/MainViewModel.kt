package com.smartpillwearos.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartpillwearos.services.SupabaseAuthManager
import com.smartpillwearos.services.supabaseClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val manager = SupabaseAuthManager(supabase = supabaseClient)

    private val _userName = MutableStateFlow("Usuário")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _loggedOut = MutableStateFlow(false)
    val loggedOut: StateFlow<Boolean> = _loggedOut.asStateFlow()

    init {
        fetchUserProfile()
    }

    private fun fetchUserProfile() {
        viewModelScope.launch {
            _userName.value = manager.getUserProfile()
        }
    }

    fun logout() {
        viewModelScope.launch {
            manager.logout()
            _loggedOut.value = true
        }
    }
}
