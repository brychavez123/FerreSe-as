package cl.duoc.ferresenas.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private data class FilaPolitica(
    val dato: String,
    val finalidad: String,
    val conservacion: String
)

private val TABLA_POLITICA_PRIVACIDAD = listOf(
    FilaPolitica("Nombre completo", "Identificar al usuario dentro de la app", "Mientras dure la sesión"),
    FilaPolitica("Correo electrónico", "Iniciar sesión y recuperar la contraseña", "Mientras dure la sesión"),
    FilaPolitica("Contraseña", "Autenticar el acceso a la cuenta", "Mientras dure la sesión"),
    FilaPolitica("Preferencia de comunicación", "Adaptar cómo se le muestra la respuesta del vendedor", "Mientras dure la sesión"),
    FilaPolitica("Historial de mensajes", "Permitir volver a mostrar un mensaje ya generado", "Mientras dure la sesión")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoliticaPrivacidadScreen(onVolver: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Políticas de privacidad") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingInterno ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingInterno)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text(
                "En FerreSeñas nos tomamos en serio la privacidad de tus datos. " +
                    "Esta política explica qué información recopilamos durante el uso de la " +
                    "aplicación y para qué la usamos.",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                "En esta versión, todos los datos se guardan solo en la memoria del " +
                    "dispositivo mientras usas la app: no se envían a ningún servidor externo " +
                    "ni se comparten con terceros, y se eliminan automáticamente al cerrar la aplicación.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 12.dp, bottom = 20.dp)
            )

            Text(
                "Datos que recopilamos",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Tabla: dato recopilado, finalidad y tiempo de conservación
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Dato",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Finalidad",
                    modifier = Modifier.weight(1.4f),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Conservación",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            TABLA_POLITICA_PRIVACIDAD.forEach { fila ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Text(fila.dato, modifier = Modifier.weight(1f))
                    Text(fila.finalidad, modifier = Modifier.weight(1.4f))
                    Text(fila.conservacion, modifier = Modifier.weight(1f))
                }
                HorizontalDivider()
            }

            Text(
                "Puedes solicitar la eliminación de tus datos cerrando sesión o desinstalando " +
                    "la aplicación. Para dudas sobre esta política, contacta a Ferretería San Andrés.",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 20.dp)
            )
        }
    }
}
