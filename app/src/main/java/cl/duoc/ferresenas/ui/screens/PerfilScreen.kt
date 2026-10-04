package cl.duoc.ferresenas.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.ferresenas.data.PreferenciaComunicacion
import cl.duoc.ferresenas.data.SesionActual
import cl.duoc.ferresenas.data.Usuario
import cl.duoc.ferresenas.data.masReciente
import cl.duoc.ferresenas.data.vibrarConfirmacion
import cl.duoc.ferresenas.ui.viewmodel.MensajesViewModel
import cl.duoc.ferresenas.ui.viewmodel.SesionViewModel

@Composable
fun PerfilScreen(
    onCerrarSesion: () -> Unit,
    sesionViewModel: SesionViewModel = viewModel(),
    mensajesViewModel: MensajesViewModel = viewModel()
) {
    val contexto = LocalContext.current
    val usuario = SesionActual.usuarioActual

    // para el contador de mensajes y el ultimo mensaje
    LaunchedEffect(Unit) { mensajesViewModel.cargar() }
    val historial = mensajesViewModel.historial

    // el ViewModel lo guarda en la base y despues actualiza la sesion
    // actual, asi queda guardado para la proxima vez que inicie sesion
    fun actualizarUsuario(nuevo: Usuario) {
        sesionViewModel.actualizarPerfil(nuevo) { contexto.vibrarConfirmacion() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.AccountCircle,
            contentDescription = "Perfil",
            modifier = Modifier.padding(top = 8.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = usuario?.nombre ?: "Invitado",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = usuario?.correo ?: "",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Tabla simple con los datos del usuario
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                FilaDato("Preferencia de comunicación", usuario?.preferenciaComunicacion?.name ?: "-")
                FilaDato("Notificaciones", if (usuario?.recibirNotificaciones == true) "Activadas" else "Desactivadas")
                FilaDato("Mensajes generados", historial.size.toString())
                // masReciente esta en Utilidades.kt, es una extension de la lista
                FilaDato("Último mensaje", historial.masReciente?.mensaje ?: "Ninguno todavía")
            }
        }

        if (usuario != null) {
            Text(
                "Mis preferencias",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp, bottom = 8.dp)
            )

            Text(
                "¿Cómo prefieres comunicarte?",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val opciones = listOf(
                    PreferenciaComunicacion.ESCRIBIR to "Escribir",
                    PreferenciaComunicacion.HABLAR to "Hablar",
                    PreferenciaComunicacion.AMBAS to "Ambas"
                )
                opciones.forEach { (opcion, etiqueta) ->
                    FilterChip(
                        selected = usuario.preferenciaComunicacion == opcion,
                        onClick = { actualizarUsuario(usuario.copy(preferenciaComunicacion = opcion)) },
                        label = { Text(etiqueta) }
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "Notificaciones",
                    tint = MaterialTheme.colorScheme.primary
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp, end = 8.dp)
                ) {
                    Text("Recibir notificaciones", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Avisos de la app, como confirmaciones de mensajes generados.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Switch(
                    checked = usuario.recibirNotificaciones,
                    onCheckedChange = { activo ->
                        actualizarUsuario(usuario.copy(recibirNotificaciones = activo))
                    }
                )
            }
        }

        Button(
            onClick = {
                // tambien borra el "Recordarme" guardado
                sesionViewModel.cerrarSesion()
                onCerrarSesion()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
        ) {
            Text("Cerrar sesión")
        }
    }
}

@Composable
private fun FilaDato(etiqueta: String, valor: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(etiqueta, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Text(
            valor,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )
    }
}
