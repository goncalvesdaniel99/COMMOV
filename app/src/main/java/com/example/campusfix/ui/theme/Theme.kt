package com.example.campusfix.ui.theme

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Paleta avelã / chocolate sobre brancos quentes
private val Chocolate = Color(0xFF6B4632)
private val ChocolateEscuro = Color(0xFF4A2F1F)
private val Avela = Color(0xFFB08968)
private val AvelaClara = Color(0xFFD9B99F)
private val CremeFundo = Color(0xFFFAF6F1)
private val CremeContainer = Color(0xFFF2E7DE)

private val LightColors = lightColorScheme(
    primary = Chocolate,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEADDD2),
    onPrimaryContainer = ChocolateEscuro,
    secondary = Color(0xFF8D6E63),
    onSecondary = Color.White,
    secondaryContainer = CremeContainer,
    onSecondaryContainer = ChocolateEscuro,
    tertiary = Avela,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFF6E3CF),
    onTertiaryContainer = Color(0xFF4E3018),
    background = CremeFundo,
    onBackground = Color(0xFF241A12),
    surface = Color.White,
    onSurface = Color(0xFF241A12),
    surfaceVariant = Color(0xFFF2EAE3),
    onSurfaceVariant = Color(0xFF6E5D52),
    outline = Color(0xFFD8CCC2),
    outlineVariant = Color(0xFFE9DFD6),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF7F1EA),
    surfaceContainer = Color(0xFFF2EAE1),
    surfaceContainerHigh = Color(0xFFECE3D9),
    surfaceContainerHighest = Color(0xFFE6DCD1)
)

private val DarkColors = darkColorScheme(
    primary = AvelaClara,
    onPrimary = Color(0xFF3E2818),
    primaryContainer = Color(0xFF5A3F2C),
    onPrimaryContainer = Color(0xFFF0E0D0),
    secondary = Color(0xFFC9A992),
    onSecondary = Color(0xFF3E2818),
    secondaryContainer = Color(0xFF4A382C),
    onSecondaryContainer = Color(0xFFF0E0D0),
    tertiary = Avela,
    onTertiary = Color(0xFF2E1D10),
    background = Color(0xFF201914),
    onBackground = Color(0xFFEFE5DC),
    surface = Color(0xFF29211B),
    onSurface = Color(0xFFEFE5DC),
    surfaceVariant = Color(0xFF3A2F27),
    onSurfaceVariant = Color(0xFFC5B5A8),
    outline = Color(0xFF5C4F45),
    outlineVariant = Color(0xFF453A31),
    surfaceContainerLowest = Color(0xFF1A140F),
    surfaceContainerLow = Color(0xFF241C16),
    surfaceContainer = Color(0xFF2A211A),
    surfaceContainerHigh = Color(0xFF342A22),
    surfaceContainerHighest = Color(0xFF3F332A)
)

// Cantos generosos, ao estilo iOS
private val CampusShapes = Shapes(
    extraSmall = RoundedCornerShape(12.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

// Títulos fortes, semelhantes aos "large titles" do iOS
private val CampusTypography = Typography().let { base ->
    base.copy(
        headlineMedium = base.headlineMedium.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.25).sp
        ),
        titleLarge = base.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            letterSpacing = (-0.25).sp
        ),
        titleMedium = base.titleMedium.copy(fontWeight = FontWeight.SemiBold)
    )
}

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
        shapes = CampusShapes,
        typography = CampusTypography,
        content = content
    )
}
