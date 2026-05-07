package com.smartpillwearos.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import org.junit.Rule
import org.junit.Test

class MainClockScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun div1_shouldDisplayInjectedUserName_Roger() {
        // Arrange: mock userName = "Roger" — avoids network call to Supabase
        val fixedClock = Clock.fixed(
            Instant.parse("2026-04-28T16:30:00Z"),
            ZoneId.of("America/Manaus")
        )

        // Act
        composeTestRule.setContent {
            MainClockScreen(clock = fixedClock, userName = "Roger")
        }

        // Assert: Div 1 must show "Roger" (regression contract)
        composeTestRule.onNodeWithText("Roger").assertIsDisplayed()
    }

    @Test
    fun div1_shouldDisplayInjectedTime() {
        val instant = Instant.parse("2026-04-28T16:30:00Z") // 12:30 in GMT-4 (Manaus)
        val fixedClock = Clock.fixed(instant, ZoneId.of("America/Manaus"))

        composeTestRule.setContent {
            MainClockScreen(clock = fixedClock, userName = "Roger")
        }

        composeTestRule.onNodeWithText("12:30").assertIsDisplayed()
    }
}
