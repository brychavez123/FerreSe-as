package cl.duoc.ferresenas.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.ferresenas.data.PreferenciasApp

// el AlertDialog por defecto usa un gris de fondo que no esta en el esquema
// de alto contraste, asi que le pongo el mismo fondo de la app (negro en
// alto contraste) y un borde amarillo para que se note donde empieza.
// el tamaño de letra lo toma solo de MaterialTheme.typography
@Composable
private fun modificadorDialogo(): Modifier =
    if (PreferenciasApp.altoContraste) {
        Modifier.border(2.dp, MaterialTheme.colorScheme.primary, AlertDialogDefaults.shape)
    } else {
        Modifier
    }

// para todo lo que borra datos (mensaje, historial, cuenta). el boton de
// confirmar va en color de error para que quede claro que no se puede deshacer
@Composable
fun DialogoConfirmacion(
    titulo: String,
    mensaje: String,
    textoConfirmar: String,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(titulo, style = MaterialTheme.typography.titleLarge) },
        text = { Text(mensaje, style = MaterialTheme.typography.bodyMedium) },
        confirmButton = {
            TextButton(onClick = onConfirmar) {
                Text(textoConfirmar, color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurface,
        modifier = modificadorDialogo()
    )
}

// dialogo con un campo de texto, para editar un mensaje guardado o el nombre
@Composable
fun DialogoEditarTexto(
    titulo: String,
    etiqueta: String,
    textoInicial: String,
    onGuardar: (String) -> Unit,
    onCancelar: () -> Unit,
    minLineas: Int = 1
) {
    var texto by remember { mutableStateOf(textoInicial) }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(titulo, style = MaterialTheme.typography.titleLarge) },
        text = {
            OutlinedTextField(
                value = texto,
                onValueChange = { texto = it },
                label = { Text(etiqueta) },
                singleLine = minLineas == 1,
                minLines = minLineas,
                isError = texto.isBlank(),
                supportingText = if (texto.isBlank()) {
                    { Text("No puede quedar vacío.") }
                } else null,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            // no deja guardar un texto vacio
            TextButton(onClick = { onGuardar(texto.trim()) }, enabled = texto.isNotBlank()) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurface,
        modifier = modificadorDialogo()
    )
}
