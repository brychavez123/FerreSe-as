package cl.duoc.ferresenas.ui.screens

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.ferresenas.data.LectorDeVoz
import cl.duoc.ferresenas.data.Producto
import cl.duoc.ferresenas.data.SesionActual
import cl.duoc.ferresenas.data.TipoMensaje
import cl.duoc.ferresenas.data.aTextoCantidad
import cl.duoc.ferresenas.data.construirMensaje
import cl.duoc.ferresenas.data.prefiereEscuchar
import cl.duoc.ferresenas.data.textoACantidadSegura
import cl.duoc.ferresenas.data.vibrarConfirmacion
import cl.duoc.ferresenas.ui.viewmodel.MensajesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConstructorMensajeScreen(
    producto: Producto,
    onVolverCatalogo: () -> Unit,
    mensajesViewModel: MensajesViewModel = viewModel()
) {
    val contexto = LocalContext.current
    var cantidad by remember { mutableStateOf("1") }

    var comboExpandido by remember { mutableStateOf(false) }
    var medidaSeleccionada by remember { mutableStateOf(producto.unidadesMedida.first()) }

    val tiposMensaje = listOf(
        TipoMensaje.NECESITO_COMPRAR to "Quiero comprar este producto",
        TipoMensaje.CONSULTA_DISPONIBILIDAD to "Solo quiero preguntar si hay disponible"
    )
    var tipoSeleccionado by remember { mutableStateOf(tiposMensaje.first().first) }

    var mensajeGenerado by remember { mutableStateOf<String?>(null) }
    var errorCantidad by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(producto.nombre) },
                navigationIcon = {
                    IconButton(onClick = onVolverCatalogo) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver al catálogo")
                    }
                }
            )
        }
    ) { paddingInterno ->
    val mensajeActual = mensajeGenerado
    if (mensajeActual != null) {
        // cuando ya hay mensaje generado, se muestra la pantalla completa
        // y se esconde el formulario
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
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(text = producto.emoji, style = MaterialTheme.typography.headlineLarge)

        // Input: cantidad
        OutlinedTextField(
            value = cantidad,
            onValueChange = {
                cantidad = it.filter(Char::isDigit)
                errorCantidad = null
            },
            label = { Text("Cantidad") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            isError = errorCantidad != null,
            supportingText = errorCantidad?.let { mensaje -> { Text(mensaje) } },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )

        // Combo box: medida
        ExposedDropdownMenuBox(
            expanded = comboExpandido,
            onExpandedChange = { comboExpandido = it },
            modifier = Modifier.padding(top = 12.dp)
        ) {
            OutlinedTextField(
                value = medidaSeleccionada,
                onValueChange = {},
                readOnly = true,
                label = { Text("Medida / tamaño") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = comboExpandido) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = comboExpandido,
                onDismissRequest = { comboExpandido = false }
            ) {
                producto.unidadesMedida.forEach { medida ->
                    DropdownMenuItem(
                        text = { Text(medida) },
                        onClick = {
                            medidaSeleccionada = medida
                            comboExpandido = false
                        }
                    )
                }
            }
        }

        // Radio buttons: tipo de mensaje
        Text(
            "¿Qué quieres decirle al vendedor?",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 20.dp, bottom = 4.dp)
        )
        Column(modifier = Modifier.selectableGroup()) {
            tiposMensaje.forEach { (tipo, etiqueta) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (tipo == tipoSeleccionado),
                            onClick = { tipoSeleccionado = tipo },
                            role = Role.RadioButton
                        )
                ) {
                    RadioButton(selected = (tipo == tipoSeleccionado), onClick = null)
                    Text(etiqueta, modifier = Modifier.padding(start = 8.dp))
                }
            }
        }

        Button(
            onClick = {
                // try/catch/finally aca, para que si la cantidad no es un
                // numero valido no se caiga la app, solo marque el error
                // en el campo
                var seGeneroMensaje = false
                try {
                    val resultado = textoACantidadSegura(cantidad)
                    val cantidadValida = resultado.getOrThrow()
                    mensajeGenerado = construirMensaje(
                        tipo = tipoSeleccionado,
                        producto = producto,
                        cantidad = cantidadValida.aTextoCantidad(),
                        medida = medidaSeleccionada
                    )
                    // se guarda en SQLite en segundo plano (ver MensajesViewModel)
                    mensajesViewModel.guardar(producto, mensajeGenerado!!)
                    seGeneroMensaje = true
                } catch (e: IllegalArgumentException) {
                    mensajeGenerado = null
                    errorCantidad = e.message ?: "Ingresa una cantidad válida."
                } finally {
                    if (seGeneroMensaje) {
                        errorCantidad = null
                        contexto.vibrarConfirmacion()
                        // si prefiere hablar (o ambas) se lo lee solo, sin
                        // que tenga que apretar el boton de escuchar
                        if (SesionActual.usuarioActual?.prefiereEscuchar == true) {
                            LectorDeVoz.leer(mensajeGenerado!!)
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
        ) {
            Text("Generar mensaje")
        }
    }
    }
}
