package cl.duoc.ferresenas.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import cl.duoc.ferresenas.data.PreferenciasApp

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

// Alto contraste: fondo negro, texto blanco y acentos amarillos, muy por
// encima del contraste mínimo recomendado para accesibilidad visual.
private val EsquemaAltoContraste = darkColorScheme(
    primary = AmarilloAltoContraste,
    onPrimary = NegroAltoContraste,
    secondary = AmarilloAltoContraste,
    onSecondary = NegroAltoContraste,
    background = NegroAltoContraste,
    onBackground = BlancoAltoContraste,
    surface = NegroAltoContraste,
    onSurface = BlancoAltoContraste,
    error = RojoErrorAltoContraste,
    onError = NegroAltoContraste
)

@Composable
fun FerreSenasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // PreferenciasApp.altoContraste es un estado de Compose: al cambiar en
    // Configuración, este bloque se recompone solo, sin recibir parámetros extra.
    val colorScheme = when {
        PreferenciasApp.altoContraste -> EsquemaAltoContraste
        darkTheme -> EsquemaOscuro
        else -> EsquemaClaro
    }

    // Igual que el color, el tamaño de letra es un estado de Compose: al
    // cambiar en Configuración, toda la app (no solo una pantalla) usa el
    // nuevo tamaño de inmediato.
    val factorTexto = PreferenciasApp.tamanoTexto.factor
    val tipografia = remember(factorTexto) { Typography.escalado(factorTexto) }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = tipografia,
        content = content
    )
}
