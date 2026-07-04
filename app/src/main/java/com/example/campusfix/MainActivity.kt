package com.example.campusfix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.campusfix.ui.theme.CampusFixTheme
import com.example.campusfix.ui.theme.ThemePrefs

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemePrefs.carregar(this)
        enableEdgeToEdge()
        setContent {
            CampusFixTheme {
                AppNavigation()
            }
        }
    }
}
