package cl.duoc.ferresenas.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import cl.duoc.ferresenas.data.PreferenciaComunicacion
import cl.duoc.ferresenas.data.RepositorioUsuarios
import cl.duoc.ferresenas.data.SesionActual
import cl.duoc.ferresenas.data.Usuario
import cl.duoc.ferresenas.data.capitalizarPrimeraLetra
import cl.duoc.ferresenas.data.ejecutarSi
import cl.duoc.ferresenas.data.primerError
import cl.duoc.ferresenas.data.vibrarConfirmacion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    onRegistroExitoso: () -> Unit,
    onVolverLogin: () -> Unit,
    onVerPoliticaPrivacidad: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var aceptaTerminos by remember { mutableStateOf(false) }
    var recibirNotificaciones by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf<String?>(null) }
    val contexto = LocalContext.current

    // Combo box: preferencia de comunicación
    var comboExpandido by remember { mutableStateOf(false) }
    val opcionesComunicacion = PreferenciaComunicacion.values().toList()
    var preferenciaSeleccionada by remember { mutableStateOf(opcionesComunicacion.first()) }

    // Radio buttons: forma en la que prefiere recibir la respuesta del vendedor
    val opcionesRespuesta = listOf("Texto escrito en pantalla", "Mensaje con voz (texto a voz)")
    var respuestaSeleccionada by remember { mutableStateOf(opcionesRespuesta.first()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text("Crear cuenta", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Completa tus datos para comenzar a comunicarte en la ferretería",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre completo") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo electrónico") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        // Combo box
        ExposedDropdownMenuBox(
            expanded = comboExpandido,
            onExpandedChange = { comboExpandido = it },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            OutlinedTextField(
                value = when (preferenciaSeleccionada) {
                    PreferenciaComunicacion.ESCRIBIR -> "Prefiero escribir"
                    PreferenciaComunicacion.HABLAR -> "Prefiero hablar"
                    PreferenciaComunicacion.AMBAS -> "Ambas por igual"
                },
                onValueChange = {},
                readOnly = true,
                label = { Text("Preferencia de comunicación") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = comboExpandido) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = comboExpandido,
                onDismissRequest = { comboExpandido = false }
            ) {
                opcionesComunicacion.forEach { opcion ->
                    val etiqueta = when (opcion) {
                        PreferenciaComunicacion.ESCRIBIR -> "Prefiero escribir"
                        PreferenciaComunicacion.HABLAR -> "Prefiero hablar"
                        PreferenciaComunicacion.AMBAS -> "Ambas por igual"
                    }
                    DropdownMenuItem(
                        text = { Text(etiqueta) },
                        onClick = {
                            preferenciaSeleccionada = opcion
                            comboExpandido = false
                        }
                    )
                }
            }
        }

        // Radio buttons
        Text(
            "¿Cómo prefieres recibir la respuesta del vendedor?",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 20.dp, bottom = 4.dp)
        )
        Column(modifier = Modifier.selectableGroup()) {
            opcionesRespuesta.forEach { opcion ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (opcion == respuestaSeleccionada),
                            onClick = { respuestaSeleccionada = opcion },
                            role = Role.RadioButton
                        )
                ) {
                    RadioButton(selected = (opcion == respuestaSeleccionada), onClick = null)
                    Text(opcion, modifier = Modifier.padding(start = 8.dp))
                }
            }
        }

        // Checklist
        Text(
            "Antes de continuar",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = aceptaTerminos, onCheckedChange = { aceptaTerminos = it })
            Text("Acepto los términos y condiciones")
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = recibirNotificaciones, onCheckedChange = { recibirNotificaciones = it })
            Text("Quiero recibir notificaciones de la app")
        }
        TextButton(onClick = onVerPoliticaPrivacidad) {
            Text("Políticas de privacidad")
        }

        mensaje?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Button(
            onClick = {
                // primerError esta en Utilidades.kt, corta apenas encuentra
                // la primera validacion que falla
                val error = primerError(
                    (nombre.isNotBlank() && correo.isNotBlank() && contrasena.isNotBlank()) to
                        { "Completa todos los campos obligatorios." },
                    aceptaTerminos to { "Debes aceptar los términos y condiciones." },
                    (!RepositorioUsuarios.existeCorreo(correo)) to { "Ese correo ya está registrado." }
                )
                mensaje = error

                ejecutarSi(error == null) {
                    val nuevoUsuario = Usuario(
                        nombre = nombre.capitalizarPrimeraLetra(),
                        correo = correo,
                        contrasena = contrasena,
                        preferenciaComunicacion = preferenciaSeleccionada,
                        recibirNotificaciones = recibirNotificaciones
                    )
                    RepositorioUsuarios.registrar(nuevoUsuario)
                    SesionActual.usuarioActual = nuevoUsuario
                    contexto.vibrarConfirmacion()
                    Toast.makeText(contexto, "Cuenta creada. ¡Bienvenido/a $nombre!", Toast.LENGTH_SHORT).show()
                    onRegistroExitoso()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Registrar")
        }

        TextButton(onClick = onVolverLogin) {
            Text("Ya tengo cuenta, volver al inicio de sesión")
        }
    }
}
