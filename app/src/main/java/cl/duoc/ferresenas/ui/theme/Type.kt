package cl.duoc.ferresenas.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.sp

// Tipografía con tamaños ampliados para favorecer la lectura
// (accesibilidad para personas con discapacidad auditiva que se apoyan
// fuertemente en la lectura para comunicarse).
val Typography = Typography(
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 26.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    )
)

/**
 * Función de extensión sobre TextStyle: escala el tamaño y el interlineado
 * por un factor, dejando "sin especificar" tal como estaba si el estilo no
 * definía ese valor.
 */
private fun TextStyle.escalado(factor: Float): TextStyle = copy(
    fontSize = if (fontSize.isSpecified) fontSize * factor else fontSize,
    lineHeight = if (lineHeight.isSpecified) lineHeight * factor else lineHeight
)

/**
 * Función de extensión sobre Typography: aplica el mismo factor de escala a
 * los 15 estilos de texto de Material Design 3, para que el ajuste de
 * tamaño de letra de Configuración se note en toda la aplicación y no solo
 * en una pantalla puntual.
 */
fun Typography.escalado(factor: Float): Typography = Typography(
    displayLarge = displayLarge.escalado(factor),
    displayMedium = displayMedium.escalado(factor),
    displaySmall = displaySmall.escalado(factor),
    headlineLarge = headlineLarge.escalado(factor),
    headlineMedium = headlineMedium.escalado(factor),
    headlineSmall = headlineSmall.escalado(factor),
    titleLarge = titleLarge.escalado(factor),
    titleMedium = titleMedium.escalado(factor),
    titleSmall = titleSmall.escalado(factor),
    bodyLarge = bodyLarge.escalado(factor),
    bodyMedium = bodyMedium.escalado(factor),
    bodySmall = bodySmall.escalado(factor),
    labelLarge = labelLarge.escalado(factor),
    labelMedium = labelMedium.escalado(factor),
    labelSmall = labelSmall.escalado(factor)
)
