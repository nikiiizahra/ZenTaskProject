package com.example.zentask.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.zentask.viewmodel.DemoAuthStore
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AuthRepositoryTest {

    @Test
    fun register_then_login_should_succeed_with_same_credentials() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val database = Room.inMemoryDatabaseBuilder(
            context,
            ZenTaskDatabase::class.java
        ).allowMainThreadQueries().build()

        try {
            val repository = AuthRepository(database)

            val registerResult = repository.register(
                name = "Ayu Putri",
                email = "ayu@example.com",
                password = "password123"
            )

            assertTrue(registerResult is AuthRepository.AuthResult.Success)

            val loginResult = repository.login(
                email = "ayu@example.com",
                password = "password123"
            )

            assertTrue(loginResult is AuthRepository.AuthResult.Success)
            assertEquals("Ayu Putri", (loginResult as AuthRepository.AuthResult.Success).user.name)
        } finally {
            database.close()
        }
    }

    @Test
    fun demo_auth_store_should_allow_logout_and_login_again_with_same_account() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        DemoAuthStore.initialize(context)

        val firstRegister = DemoAuthStore.registerLocal(
            fullName = "Ayu Putri",
            email = "ayu@example.com",
            password = "password123",
            weeklySummary = true
        )

        assertTrue(firstRegister is AuthRepository.AuthResult.Success)

        DemoAuthStore.logout()

        val secondLogin = DemoAuthStore.loginLocal(
            email = "ayu@example.com",
            password = "password123"
        )

        assertTrue(secondLogin is AuthRepository.AuthResult.Success)
        assertEquals("Ayu Putri", (secondLogin as AuthRepository.AuthResult.Success).user.name)
    }
}
