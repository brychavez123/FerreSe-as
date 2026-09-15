package cl.duoc.ferresenas.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cl.duoc.ferresenas.data.PreferenciasApp
import cl.duoc.ferresenas.data.TamanoTexto
import cl.duoc.ferresenas.data.vibrarConfirmacion


@Composable
fun ConfiguracionScreen() {
    val contexto = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            "Configuración de accesibilidad",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            "Ajusta la app a lo que te resulte más cómodo de ver y sentir.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        FilaAjuste(
            icono = Icons.Filled.Contrast,
            descripcionIcono = "Alto contraste",
            titulo = "Alto contraste",
            descripcion = "Fondo negro y textos en blanco/amarillo, más fáciles de distinguir.",
            activo = PreferenciasApp.altoContraste,
            onCambio = { activo ->
                PreferenciasApp.cambiarAltoContraste(activo)
                contexto.vibrarConfirmacion()
            }
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

        FilaTamanoTexto(
            tamanoActual = PreferenciasApp.tamanoTexto,
            onDisminuir = {
                PreferenciasApp.disminuirTamanoTexto()
                contexto.vibrarConfirmacion()
            },
            onAumentar = {
                PreferenciasApp.aumentarTamanoTexto()
                contexto.vibrarConfirmacion()
            }
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

        FilaAjuste(
            icono = Icons.Filled.Vibration,
            descripcionIcono = "Vibración",
            titulo = "Vibración de confirmación",
            descripcion = "Vibra al generar, guardar o mostrar un mensaje. Complementa el aviso visual, no lo reemplaza.",
            activo = PreferenciasApp.vibracionActiva,
            onCambio = { activo -> PreferenciasApp.cambiarVibracion(activo) }
        )
    }
}

@Composable
private fun FilaAjuste(
    icono: ImageVector,
    descripcionIcono: String,
    titulo: String,
    descripcion: String,
    activo: Boolean,
    onCambio: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icono,
            contentDescription = descripcionIcono,
            tint = MaterialTheme.colorScheme.primary
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp, end = 8.dp)
        ) {
            Text(titulo, style = MaterialTheme.typography.titleMedium)
            Text(descripcion, style = MaterialTheme.typography.bodySmall)
        }
        Switch(checked = activo, onCheckedChange = onCambio)
    }
}

@Composable
private fun FilaTamanoTexto(
    tamanoActual: TamanoTexto,
    onDisminuir: () -> Unit,
    onAumentar: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.TextFields,
            contentDescription = "Tamaño de letra",
            tint = MaterialTheme.colorScheme.primary
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp, end = 8.dp)
        ) {
            Text("Tamaño de letra", style = MaterialTheme.typography.titleMedium)
            Text(
                "Agranda o achica el texto de toda la aplicación, incluido el mensaje al vendedor.",
                style = MaterialTheme.typography.bodySmall
            )
        }

        OutlinedIconButton(
            onClick = onDisminuir,
            enabled = tamanoActual != TamanoTexto.entries.first()
        ) {
            Icon(Icons.Filled.Remove, contentDescription = "Disminuir tamaño de letra")
        }
        Text(
            tamanoActual.etiqueta,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(64.dp)
                .padding(horizontal = 4.dp)
        )
        IconButton(
            onClick = onAumentar,
            enabled = tamanoActual != TamanoTexto.entries.last()
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Aumentar tamaño de letra")
        }
    }
}
