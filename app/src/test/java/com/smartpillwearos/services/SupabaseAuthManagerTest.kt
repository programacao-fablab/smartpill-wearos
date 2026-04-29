package com.smartpillwearos.services

import com.smartpillwearos.domain.AuthState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SupabaseAuthManagerTest {

    @Test
    fun `startPairingFlow with mockedTokens should transition to Success`() = runBlocking {
        val manager = SupabaseAuthManager(supabase = null)

        val states = mutableListOf<AuthState>()
        val job = launch(Dispatchers.Unconfined) {
            manager.state.collect { state ->
                println("⏳ [TRANSITION]: $state")
                states.add(state)
            }
        }

        manager.startPairingFlow(mockedTokens = true)

        assertTrue(states.isNotEmpty())
        
        val finalState = states.last()
        assertTrue("Expected final state to be Success, but was $finalState", finalState is AuthState.Success)
        assertEquals("mocked_user_id_999", (finalState as AuthState.Success).userId)

        job.cancel()
    }
}
