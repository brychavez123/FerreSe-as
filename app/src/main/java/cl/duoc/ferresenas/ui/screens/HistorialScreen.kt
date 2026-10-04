package cl.duoc.ferresenas.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.ferresenas.data.MensajeHistorial
import cl.duoc.ferresenas.data.vibrarConfirmacion
import cl.duoc.ferresenas.ui.viewmodel.MensajesViewModel

/**
 * Tabla con el historial de mensajes visuales guardados en SQLite,
 * para que el usuario pueda volver a mostrarle uno al vendedor sin rehacerlo.
 * Solo muestra los mensajes del usuario con sesión activa, no de otros.
 * Desde aca se puede editar el texto, borrar un mensaje o vaciar todo.
 */
@Composable
fun HistorialScreen(mensajesViewModel: MensajesViewModel = viewModel()) {
    val contexto = LocalContext.current

    // cada vez que se entra a la pestaña se vuelve a leer de la base, asi
    // aparecen los mensajes que se generaron recien en el Constructor
    LaunchedEffect(Unit) { mensajesViewModel.cargar() }
    val historial = mensajesViewModel.historial

    // que dialogo esta abierto (null = ninguno)
    var mensajeAEditar by remember { mutableStateOf<MensajeHistorial?>(null) }
    var mensajeABorrar by remember { mutableStateOf<MensajeHistorial?>(null) }
    var confirmarVaciar by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Historial de mensajes", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Aquí quedan guardados los mensajes que has generado, aunque cierres la app",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
        )

        if (historial.isEmpty()) {
            Text(
                "Todavía no has generado ningún mensaje. Ve a Inicio y elige un producto.",
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            OutlinedButton(
                onClick = { confirmarVaciar = true },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Icon(Icons.Filled.DeleteSweep, contentDescription = null)
                Text("Vaciar historial", modifier = Modifier.padding(start = 8.dp))
            }

            // Tabla: producto, mensaje generado y fecha/hora
            Row(modifier = Modifier.fillMaxWidth()) {
                Text("Producto", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelLarge)
                Text("Mensaje", modifier = Modifier.weight(2f), style = MaterialTheme.typography.labelLarge)
                Text("Fecha", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelLarge)
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                historial.forEach { item ->
                    Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                        Text(
                            item.producto?.let { "${it.emoji} ${it.nombre}" } ?: "✍️ Personalizado",
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            item.mensaje.replaceFirstChar { it.uppercase() },
                            modifier = Modifier.weight(2f)
                        )
                        Text(item.fechaHora, modifier = Modifier.weight(1f))
                    }
                    // botones con texto y no solo icono, se entienden mejor
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { mensajeAEditar = item }) {
                            Icon(Icons.Filled.Edit, contentDescription = null)
                            Text("Editar", modifier = Modifier.padding(start = 4.dp))
                        }
                        TextButton(
                            onClick = { mensajeABorrar = item },
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Filled.Delete, contentDescription = null)
                            Text("Borrar", modifier = Modifier.padding(start = 4.dp))
                        }
                    }
                    HorizontalDivider()
                }
            }
        }
    }

    mensajeAEditar?.let { item ->
        DialogoEditarTexto(
            titulo = "Editar mensaje",
            etiqueta = "Texto del mensaje",
            textoInicial = item.mensaje,
            minLineas = 3,
            onGuardar = { nuevoTexto ->
                mensajesViewModel.editar(item.id, nuevoTexto)
                mensajeAEditar = null
                contexto.vibrarConfirmacion()
                Toast.makeText(contexto, "Mensaje actualizado", Toast.LENGTH_SHORT).show()
            },
            onCancelar = { mensajeAEditar = null }
        )
    }

    mensajeABorrar?.let { item ->
        DialogoConfirmacion(
            titulo = "¿Borrar este mensaje?",
            mensaje = "\"${item.mensaje}\"\n\nEsta acción no se puede deshacer.",
            textoConfirmar = "Borrar",
            onConfirmar = {
                mensajesViewModel.eliminar(item.id)
                mensajeABorrar = null
                contexto.vibrarConfirmacion()
                Toast.makeText(contexto, "Mensaje borrado", Toast.LENGTH_SHORT).show()
            },
            onCancelar = { mensajeABorrar = null }
        )
    }

    if (confirmarVaciar) {
        DialogoConfirmacion(
            titulo = "¿Vaciar el historial?",
            mensaje = "Se borrarán los ${historial.size} mensajes guardados. Esta acción no se puede deshacer.",
            textoConfirmar = "Vaciar",
            onConfirmar = {
                mensajesViewModel.vaciar()
                confirmarVaciar = false
                contexto.vibrarConfirmacion()
                Toast.makeText(contexto, "Historial vaciado", Toast.LENGTH_SHORT).show()
            },
            onCancelar = { confirmarVaciar = false }
        )
    }
}
