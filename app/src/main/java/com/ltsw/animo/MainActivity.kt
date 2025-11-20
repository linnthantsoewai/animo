package com.ltsw.animo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ltsw.animo.ui.navigation.AnimoApp
import com.ltsw.animo.ui.theme.AnimoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val application = applicationContext as AnimoApplication
            val isDarkMode by application.themePreferences.isDarkMode.collectAsState(initial = false)

            AnimoTheme(darkTheme = isDarkMode) {
                AnimoApp()
            }
        }
    }
}