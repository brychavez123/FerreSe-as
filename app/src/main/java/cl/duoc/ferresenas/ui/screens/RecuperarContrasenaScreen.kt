package cl.duoc.ferresenas.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cl.duoc.ferresenas.data.RepositorioUsuarios

@Composable
fun RecuperarContrasenaScreen(
    onVolverLogin: () -> Unit
) {
    var correo by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Recuperar contraseña", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Ingresa tu correo y te indicaremos los pasos a seguir. " +
                "Si prefieres, puedes pedirle ayuda al vendedor mostrándole esta pantalla.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
        )

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Usuario") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        mensaje?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        Button(
            onClick = {
                mensaje = if (RepositorioUsuarios.existeCorreo(correo)) {
                    "Listo. Enviamos las instrucciones de recuperación a $correo."
                } else {
                    "No encontramos una cuenta asociada a ese correo."
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Enviar instrucciones")
        }

        TextButton(onClick = onVolverLogin) {
            Text("Volver al inicio de sesión")
        }
    }
}
