package cl.duoc.ferresenas.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.ferresenas.data.RepositorioMensajes

/**
 * Lista de elementos: historial de mensajes visuales generados en la sesión,
 * para que el usuario pueda volver a mostrarle uno al vendedor sin rehacerlo.
 */
@Composable
fun HistorialScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
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
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(RepositorioMensajes.historial) { item ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "${item.producto.emoji}  ${item.producto.nombre}",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = item.mensaje.replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Text(
                                text = item.fechaHora,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .align(Alignment.End)
                            )
                        }
                    }
                }
            }
        }
    }
}
