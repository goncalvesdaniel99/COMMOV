package com.example.campusfix.ui.theme

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color

private val Azul = Color(0xFF1565C0)
private val AzulEscuro = Color(0xFF0D47A1)
private val Amarelo = Color(0xFFFFC107)

private val LightColors = lightColorScheme(
    primary = Azul,
    secondary = AzulEscuro,
    tertiary = Amarelo
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF90CAF9),
    secondary = Color(0xFF64B5F6),
    tertiary = Amarelo
)

/** Preferência de modo escuro guardada em SharedPreferences (null = seguir o sistema). */
object ThemePrefs {
    private const val PREFS = "campusfix_prefs"
    private const val KEY = "modo_escuro"

    val modoEscuro = mutableStateOf<Boolean?>(null)

    fun carregar(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        modoEscuro.value = if (prefs.contains(KEY)) prefs.getBoolean(KEY, false) else null
    }

    fun guardar(context: Context, escuro: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY, escuro).apply()
        modoEscuro.value = escuro
    }
}

@Composable
fun CampusFixTheme(content: @Composable () -> Unit) {
    val escuro = ThemePrefs.modoEscuro.value ?: isSystemInDarkTheme()
    MaterialTheme(
        colorScheme = if (escuro) DarkColors else LightColors,
        content = content
    )
}
