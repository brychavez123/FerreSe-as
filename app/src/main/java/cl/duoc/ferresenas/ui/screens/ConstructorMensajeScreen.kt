package cl.duoc.ferresenas.ui.screens

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
import androidx.compose.material3.Card
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.ferresenas.data.Producto
import cl.duoc.ferresenas.data.TipoMensaje
import cl.duoc.ferresenas.data.construirMensaje

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConstructorMensajeScreen(
    producto: Producto,
    onVolverCatalogo: () -> Unit
) {
    var cantidad by remember { mutableStateOf("1") }

    var comboExpandido by remember { mutableStateOf(false) }
    var medidaSeleccionada by remember { mutableStateOf(producto.unidadesMedida.first()) }

    val tiposMensaje = listOf(
        TipoMensaje.NECESITO_COMPRAR to "Quiero comprar este producto",
        TipoMensaje.CONSULTA_DISPONIBILIDAD to "Solo quiero preguntar si hay disponible"
    )
    var tipoSeleccionado by remember { mutableStateOf(tiposMensaje.first().first) }

    var mensajeGenerado by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(text = producto.emoji, style = MaterialTheme.typography.headlineLarge)
        Text(text = producto.nombre, style = MaterialTheme.typography.headlineMedium)

        // Input: cantidad
        OutlinedTextField(
            value = cantidad,
            onValueChange = { cantidad = it.filter(Char::isDigit) },
            label = { Text("Cantidad") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
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
                mensajeGenerado = construirMensaje(
                    tipo = tipoSeleccionado,
                    producto = producto,
                    cantidad = cantidad,
                    medida = medidaSeleccionada
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
        ) {
            Text("Generar mensaje")
        }

        // Mensaje visual grande, pensado para mostrarse al vendedor
        mensajeGenerado?.let { mensaje ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = mensaje.replaceFirstChar { it.uppercase() },
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }

        TextButton(
            onClick = onVolverCatalogo,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Elegir otro producto")
        }
    }
}
