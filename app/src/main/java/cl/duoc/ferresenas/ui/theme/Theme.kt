package cl.duoc.ferresenas.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EsquemaClaro = lightColorScheme(
    primary = NaranjoFerreteria,
    onPrimary = Color.White,
    secondary = AzulConfianza,
    onSecondary = Color.White,
    background = FondoClaro,
    onBackground = TextoAltoContraste,
    surface = Color.White,
    onSurface = TextoAltoContraste
)

private val EsquemaOscuro = darkColorScheme(
    primary = NaranjoFerreteria,
    secondary = AzulConfianza
)

@Composable
fun FerreSenasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) EsquemaOscuro else EsquemaClaro

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
