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

// esquema de alto contraste: fondo negro, texto blanco, acentos amarillos.
// deberia ser bastante mas facil de leer que el esquema normal
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
    // altoContraste es mutableStateOf, entonces con solo leerlo aca ya
    // se recompone solo cuando cambia en Configuracion
    val colorScheme = when {
        PreferenciasApp.altoContraste -> EsquemaAltoContraste
        darkTheme -> EsquemaOscuro
        else -> EsquemaClaro
    }

    // lo mismo con el tamaño de letra, cambia en toda la app al tiro
    val factorTexto = PreferenciasApp.tamanoTexto.factor
    val tipografia = remember(factorTexto) { Typography.escalado(factorTexto) }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = tipografia,
        content = content
    )
}
