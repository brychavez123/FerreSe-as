package cl.duoc.ferresenas.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.ferresenas.data.RepositorioMensajes

/**
 * Tabla con el historial de mensajes visuales generados en la sesión,
 * para que el usuario pueda volver a mostrarle uno al vendedor sin rehacerlo.
 * Solo muestra información propia de la sesión, no de otros usuarios.
 */
@Composable
fun HistorialScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Historial de mensajes", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Aquí quedan guardados los mensajes que has generado en esta sesión",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
        )

        if (RepositorioMensajes.historial.isEmpty()) {
            Text(
                "Todavía no has generado ningún mensaje. Ve a Inicio y elige un producto.",
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            // Tabla: producto, mensaje generado y fecha/hora
            Row(modifier = Modifier.fillMaxWidth()) {
                Text("Producto", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelLarge)
                Text("Mensaje", modifier = Modifier.weight(2f), style = MaterialTheme.typography.labelLarge)
                Text("Fecha", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelLarge)
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                RepositorioMensajes.historial.forEach { item ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                        Text(
                            "${item.producto.emoji} ${item.producto.nombre}",
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            item.mensaje.replaceFirstChar { it.uppercase() },
                            modifier = Modifier.weight(2f)
                        )
                        Text(item.fechaHora, modifier = Modifier.weight(1f))
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}
