package com.example.zentask.ui.screens

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DashboardLayoutTest {

    @Test
    fun shouldUseLandscapeDashboard_returns_true_for_wide_screens() {
        assertTrue(shouldUseLandscapeDashboard(900))
        assertTrue(shouldUseLandscapeDashboard(700))
    }

    @Test
    fun shouldUseLandscapeDashboard_returns_false_for_phone_width() {
        assertFalse(shouldUseLandscapeDashboard(390))
        assertFalse(shouldUseLandscapeDashboard(320))
    }
}
