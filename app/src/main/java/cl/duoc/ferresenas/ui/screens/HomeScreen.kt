package cl.duoc.ferresenas.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import cl.duoc.ferresenas.data.Producto
import cl.duoc.ferresenas.data.SesionActual

private data class ItemMenuInferior(val etiqueta: String, val icono: androidx.compose.ui.graphics.vector.ImageVector)

private val itemsMenuInferior = listOf(
    ItemMenuInferior("Inicio", Icons.Filled.Home),
    ItemMenuInferior("Historial", Icons.Filled.History),
    ItemMenuInferior("Perfil", Icons.Filled.Person)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onProductoSeleccionado: (Producto) -> Unit,
    onCerrarSesion: () -> Unit
) {
    var pestanaSeleccionada by remember { mutableIntStateOf(0) }
    val contexto = LocalContext.current
    val nombreUsuario = SesionActual.usuarioActual?.nombre?.substringBefore(" ") ?: "Usuario"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("¡Hola, $nombreUsuario!") },
                actions = {
                    IconButton(onClick = {
                        Toast.makeText(
                            contexto,
                            "Muéstrale la pantalla al vendedor para pedir ayuda",
                            Toast.LENGTH_LONG
                        ).show()
                    }) {
                        Icon(Icons.Filled.Notifications, contentDescription = "Ayuda")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                itemsMenuInferior.forEachIndexed { indice, item ->
                    NavigationBarItem(
                        selected = pestanaSeleccionada == indice,
                        onClick = { pestanaSeleccionada = indice },
                        icon = { Icon(item.icono, contentDescription = item.etiqueta) },
                        label = { Text(item.etiqueta) }
                    )
                }
            }
        }
    ) { paddingInterno ->
        Box(modifier = Modifier.padding(paddingInterno)) {
            when (pestanaSeleccionada) {
                0 -> CatalogoScreen(onProductoSeleccionado = onProductoSeleccionado)
                1 -> HistorialScreen()
                else -> PerfilScreen(onCerrarSesion = onCerrarSesion)
            }
        }
    }
}
