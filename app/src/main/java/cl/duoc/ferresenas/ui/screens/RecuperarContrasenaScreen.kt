package cl.duoc.ferresenas.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.ferresenas.data.primerError
import cl.duoc.ferresenas.data.vibrarConfirmacion
import cl.duoc.ferresenas.ui.viewmodel.SesionViewModel

// antes solo decia "enviamos las instrucciones" pero no hacia nada. ahora
// permite poner una contraseña nueva para un correo que exista en la base
@Composable
fun RecuperarContrasenaScreen(
    onVolverLogin: () -> Unit,
    sesionViewModel: SesionViewModel = viewModel()
) {
    val contexto = LocalContext.current
    var correo by remember { mutableStateOf("") }
    var nuevaContrasena by remember { mutableStateOf("") }
    var confirmarContrasena by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text("Recuperar contraseña", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Ingresa tu correo y define una contraseña nueva. " +
                "Si prefieres, puedes pedirle ayuda al vendedor mostrándole esta pantalla.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
        )

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo electrónico") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = nuevaContrasena,
            onValueChange = { nuevaContrasena = it },
            label = { Text("Contraseña nueva") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        OutlinedTextField(
            value = confirmarContrasena,
            onValueChange = { confirmarContrasena = it },
            label = { Text("Repite la contraseña nueva") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        mensaje?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        Button(
            onClick = {
                // mismas validaciones con primerError que en Registro
                mensaje = primerError(
                    (correo.isNotBlank() && nuevaContrasena.isNotBlank()) to { "Completa todos los campos." },
                    (nuevaContrasena.length >= 4) to { "La contraseña debe tener al menos 4 caracteres." },
                    (nuevaContrasena == confirmarContrasena) to { "Las contraseñas no coinciden." }
                )
                if (mensaje == null) {
                    sesionViewModel.recuperarContrasena(
                        correo = correo,
                        nuevaContrasena = nuevaContrasena,
                        onExito = {
                            contexto.vibrarConfirmacion()
                            Toast.makeText(
                                contexto,
                                "Contraseña actualizada. Ya puedes iniciar sesión.",
                                Toast.LENGTH_LONG
                            ).show()
                            onVolverLogin()
                        },
                        onError = { mensaje = it }
                    )
                }
            },
            enabled = !sesionViewModel.cargando,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Cambiar contraseña")
        }

        TextButton(onClick = onVolverLogin) {
            Text("Volver al inicio de sesión")
        }
    }
}
