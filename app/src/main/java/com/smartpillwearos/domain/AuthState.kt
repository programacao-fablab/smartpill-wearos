package com.smartpillwearos.domain

sealed class AuthState {
    object Idle : AuthState()
    object GeneratingQR : AuthState()
    data class WaitingForMobileScan(val qrToken: String) : AuthState()
    object Authenticating : AuthState()
    data class Success(val userId: String) : AuthState()
    data class Error(val message: String) : AuthState()
}
