package com.smartpillwearos.services

import com.smartpillwearos.domain.AuthState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SupabaseAuthManagerTest {

    @Test
    fun `auth state classes are properly defined and accessible`() {
        val states = listOf(
            AuthState.Idle,
            AuthState.GeneratingQR,
            AuthState.WaitingForMobileScan("token123"),
            AuthState.Authenticating,
            AuthState.Success("user123"),
            AuthState.Error("error msg")
        )
        
        assertEquals(6, states.size)
        assertTrue(states[0] is AuthState.Idle)
        assertTrue(states[2] is AuthState.WaitingForMobileScan)
        assertEquals("token123", (states[2] as AuthState.WaitingForMobileScan).qrToken)
    }
}
