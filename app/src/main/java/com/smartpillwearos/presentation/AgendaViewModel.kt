package com.smartpillwearos.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartpillwearos.domain.Medicine
import com.smartpillwearos.services.MedicineService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import com.smartpillwearos.services.supabaseClient
import io.github.jan.supabase.gotrue.auth

sealed class AgendaState {
    object Loading : AgendaState()
    data class Success(val medicines: List<Medicine>) : AgendaState()
    data class Error(val message: String) : AgendaState()
}

class AgendaViewModel(
    private val medicineService: MedicineService = MedicineService(supabaseClient)
) : ViewModel() {

    private val _state = MutableStateFlow<AgendaState>(AgendaState.Loading)
    val state: StateFlow<AgendaState> = _state.asStateFlow()

    // Mocked cache for resilience (Offline-first / Fallback)
    private var cachedMedicines: List<Medicine>? = null

    fun loadMedicines() {
        _state.value = AgendaState.Loading
        viewModelScope.launch {
            try {
                val user = com.smartpillwearos.services.supabaseClient.auth.currentSessionOrNull()?.user
                val userId = user?.id
                if (userId == null) {
                    _state.value = AgendaState.Error("Utilizador não autenticado.")
                    return@launch
                }
                val medicines = medicineService.fetchUserMedicines(userId)
                cachedMedicines = medicines
                _state.value = AgendaState.Success(medicines)
            } catch (e: Exception) {
                if (cachedMedicines != null) {
                    _state.value = AgendaState.Success(cachedMedicines!!)
                } else {
                    _state.value = AgendaState.Error("Falha na rede: ${e.message}. Nenhuma agenda em cache.")
                }
            }
        }
    }
}
