package com.ltsw.animo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ltsw.animo.ui.navigation.AnimoApp
import com.ltsw.animo.ui.screens.LoginScreen
import com.ltsw.animo.ui.theme.AnimoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val application = applicationContext as AnimoApplication
            val isDarkMode by application.themePreferences.isDarkMode.collectAsState(initial = false)

            // Check if user is logged in
            val loggedInUser by application.userRepository.loggedInUser.collectAsState(initial = null)

            AnimoTheme(darkTheme = isDarkMode) {
                if (loggedInUser == null) {
                    // Show login screen if no user is logged in
                    LoginScreen(onLoginSuccess = {
                        // User logged in, the state will update automatically
                        // and the main app will be displayed
                    })
                } else {
                    // User is logged in, show main app
                    AnimoApp()
                }
            }
        }
    }
}