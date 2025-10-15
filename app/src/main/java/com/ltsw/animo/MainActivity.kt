package com.ltsw.animo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ltsw.animo.ui.navigation.AnimoApp
import com.ltsw.animo.ui.theme.AnimoTheme

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