package cl.duoc.ferresenas.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.ferresenas.data.LectorDeVoz
import cl.duoc.ferresenas.data.SesionActual
import cl.duoc.ferresenas.data.prefiereEscuchar
import cl.duoc.ferresenas.data.vibrarConfirmacion
import cl.duoc.ferresenas.ui.viewmodel.MensajesViewModel

// esta pantalla es para cuando el usuario quiere preguntar algo que no
// esta en el catalogo, puede escribir el mensaje que quiera a mano.
// reutiliza la misma pantalla completa del Constructor de mensaje
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MensajePersonalizadoScreen(
    onVolver: () -> Unit,
    mensajesViewModel: MensajesViewModel = viewModel()
) {
    val contexto = LocalContext.current
    var texto by rememberSaveable { mutableStateOf("") }
    var mensajeGenerado by rememberSaveable { mutableStateOf<String?>(null) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    AvisoDeError(mensajesViewModel.error) { mensajesViewModel.limpiarError() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mensaje personalizado") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver al catálogo")
                    }
                }
            )
        }
    ) { paddingInterno ->
        val mensajeActual = mensajeGenerado
        if (mensajeActual != null) {
            PantallaCompletaMensaje(
                mensaje = mensajeActual,
                paddingInterno = paddingInterno,
                onEditar = { mensajeGenerado = null }
            )
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingInterno)
                .padding(24.dp)
        ) {
            Text(
                "¿Necesitas preguntar otra cosa que no está en el catálogo? Escríbelo " +
                    "aquí y te lo mostramos grande, listo para el vendedor.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = texto,
                onValueChange = {
                    texto = it
                    error = null
                },
                label = { Text("Tu mensaje") },
                isError = error != null,
                supportingText = error?.let { mensajeError -> { Text(mensajeError) } },
                minLines = 5,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            Button(
                onClick = {
                    // que no venga vacio es una validacion normal, no algo que
                    // "falle", asi que basta con un if (antes era try/catch)
                    if (texto.isBlank()) {
                        error = "Escribe un mensaje antes de continuar."
                    } else {
                        val mensajeListo = texto.trim()
                        mensajeGenerado = mensajeListo
                        mensajesViewModel.guardar(producto = null, mensaje = mensajeListo)
                        contexto.vibrarConfirmacion()
                        // mismo comportamiento que en Constructor de mensaje
                        if (SesionActual.usuarioActual?.prefiereEscuchar == true) {
                            LectorDeVoz.leer(mensajeListo)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Mostrar mensaje")
            }
        }
    }
}
