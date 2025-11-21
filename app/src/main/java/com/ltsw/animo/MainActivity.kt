package com.ltsw.animo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ltsw.animo.ui.navigation.AnimoApp
import com.ltsw.animo.ui.screens.LoginScreen
import com.ltsw.animo.ui.theme.AnimoTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val application = applicationContext as AnimoApplication
            val isDarkMode by application.themePreferences.isDarkMode.collectAsState(initial = false)

            // State to track if we're checking authentication
            var isCheckingAuth by remember { mutableStateOf(true) }
            var isLoggedIn by remember { mutableStateOf(false) }

            // Check authentication status once on startup
            LaunchedEffect(Unit) {
                val user = application.userRepository.getLoggedInUserOnce()
                isLoggedIn = user != null
                // Small delay to ensure smooth transition
                delay(100)
                isCheckingAuth = false
            }

            // After initial check, observe for changes (for logout functionality)
            val loggedInUser by application.userRepository.loggedInUser.collectAsState(initial = null)

            // Update logged in state when user logs out
            LaunchedEffect(loggedInUser) {
                if (!isCheckingAuth) {
                    isLoggedIn = loggedInUser != null
                }
            }

            AnimoTheme(darkTheme = isDarkMode) {
                when {
                    isCheckingAuth -> {
                        // Show loading screen while checking authentication
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.background),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_animo_logo),
                                contentDescription = "Animo Logo",
                                modifier = Modifier.size(80.dp)
                            )
                        }
                    }
                    !isLoggedIn -> {
                        // Show login screen if no user is logged in
                        LoginScreen(onLoginSuccess = {
                            isLoggedIn = true
                        })
                    }
                    else -> {
                        // User is logged in, show main app
                        AnimoApp()
                    }
                }
            }
        }
    }
}