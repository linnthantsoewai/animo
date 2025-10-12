package com.example.animo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.animo.ui.navigation.AnimoApp
import com.example.animo.ui.theme.AnimoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AnimoTheme {
                AnimoApp()
            }
        }
    }
}
