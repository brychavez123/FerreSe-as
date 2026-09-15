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

// extension sobre TextStyle que escala el tamaño de letra y el interlineado.
// si el estilo no tenia ese valor definido (isSpecified false) lo deja igual
private fun TextStyle.escalado(factor: Float): TextStyle = copy(
    fontSize = if (fontSize.isSpecified) fontSize * factor else fontSize,
    lineHeight = if (lineHeight.isSpecified) lineHeight * factor else lineHeight
)

// extension sobre Typography, aplica el escalado a los 15 estilos de
// Material 3 de una. Asi el ajuste de tamaño de letra se nota en toda la
// app y no solo en una pantalla
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
