package cl.duoc.ferresenas.ui.screens

import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cl.duoc.ferresenas.data.Producto
import cl.duoc.ferresenas.data.SesionActual
import cl.duoc.ferresenas.ui.theme.AzulConfianza
import cl.duoc.ferresenas.ui.theme.NaranjoFerreteria

private data class ItemMenuInferior(val etiqueta: String, val icono: androidx.compose.ui.graphics.vector.ImageVector)

private val itemsMenuInferior = listOf(
    ItemMenuInferior("Inicio", Icons.Filled.Home),
    ItemMenuInferior("Historial", Icons.Filled.History),
    ItemMenuInferior("Perfil", Icons.Filled.Person)
)

@Composable
fun HomeScreen(
    onProductoSeleccionado: (Producto) -> Unit,
    onCerrarSesion: () -> Unit
) {
    var pestanaSeleccionada by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = { EncabezadoInicio() },
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

/**
 * Barra superior personalizada: avatar del usuario (con opción de elegir foto
 * desde la galería), su nombre y correo, y un acceso rápido de ayuda.
 */
@Composable
private fun EncabezadoInicio() {
    val contexto = LocalContext.current
    val usuario = SesionActual.usuarioActual

    val selectorFoto = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            SesionActual.fotoPerfilUri = uri
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.horizontalGradient(listOf(NaranjoFerreteria, AzulConfianza)))
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AvatarUsuario(
            nombre = usuario?.nombre ?: "Usuario",
            fotoUri = SesionActual.fotoPerfilUri,
            onClick = {
                selectorFoto.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "¡Hola, ${usuario?.nombre?.substringBefore(" ") ?: "Usuario"}!",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
            Text(
                text = usuario?.correo ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.85f)
            )
        }
        IconButton(onClick = {
            Toast.makeText(
                contexto,
                "Muéstrale la pantalla al vendedor para pedir ayuda",
                Toast.LENGTH_LONG
            ).show()
        }) {
            Icon(Icons.Filled.Notifications, contentDescription = "Ayuda", tint = Color.White)
        }
    }
}

@Composable
private fun AvatarUsuario(nombre: String, fotoUri: Uri?, onClick: () -> Unit) {
    val contexto = LocalContext.current
    val bitmap = remember(fotoUri) {
        fotoUri?.let { uri ->
            runCatching {
                contexto.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }
            }.getOrNull()
        }
    }

    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.25f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = nombre.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
        }
    }
}
