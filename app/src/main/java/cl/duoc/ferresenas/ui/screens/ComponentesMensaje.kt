package cl.duoc.ferresenas.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.ferresenas.data.LectorDeVoz
import cl.duoc.ferresenas.data.PreferenciasApp
import cl.duoc.ferresenas.data.capitalizarPrimeraLetra
import cl.duoc.ferresenas.data.tamanoMensajeSp
import cl.duoc.ferresenas.ui.theme.VerdeExito

/**
 * Mensaje a pantalla completa: ocupa casi todo el espacio disponible bajo la
 * barra superior (el botón de volver sigue visible) para que sea fácil de
 * leer de lejos al mostrárselo al vendedor. Se usa tanto para el mensaje
 * generado desde un producto como para el mensaje personalizado.
 * "Editar mensaje" vuelve al formulario sin perder lo que ya se había escrito.
 */
@Composable
fun PantallaCompletaMensaje(
    mensaje: String,
    paddingInterno: PaddingValues,
    onEditar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingInterno)
            .padding(24.dp)
    ) {
        // Confirmación visual persistente: no depende de un Toast que
        // desaparece solo, así el usuario siempre puede volver a verla.
        Card(
            colors = CardDefaults.cardColors(containerColor = VerdeExito.copy(alpha = 0.12f)),
            border = BorderStroke(1.dp, VerdeExito),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Confirmación",
                    tint = VerdeExito
                )
                Text(
                    "Mensaje generado correctamente. Muéstraselo al vendedor.",
                    color = VerdeExito,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        // El mensaje toma todo el espacio que sobra entre la confirmación y
        // los botones de abajo, y se autoajusta: arranca grande (para leerse
        // de lejos) y va achicándose solo hasta que el texto completo entra
        // sin cortarse, sin importar cuán largo sea el mensaje.
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            MensajeAutoAjustable(
                texto = mensaje.capitalizarPrimeraLetra(),
                tamanoMaximoSp = PreferenciasApp.tamanoMensajeSp * 1.6f,
                tamanoMinimoSp = PreferenciasApp.tamanoMensajeSp.toFloat()
            )
        }

        // Botón manual: cualquier persona puede pedir que se lea el mensaje
        // en voz alta, prefiera hablar o no.
        TextButton(
            onClick = { LectorDeVoz.leer(mensaje) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null)
            Text("Escuchar mensaje", modifier = Modifier.padding(start = 8.dp))
        }

        Button(
            onClick = onEditar,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Editar mensaje")
        }
    }
}

/**
 * Texto que se autoajusta al espacio disponible: empieza en [tamanoMaximoSp]
 * y, si no entra completo (queda cortado), va bajando de a 2sp hasta caber
 * o hasta llegar a [tamanoMinimoSp]. Así un mensaje corto se ve grande y uno
 * largo no se corta ni queda apretado.
 */
@Composable
private fun MensajeAutoAjustable(
    texto: String,
    tamanoMaximoSp: Float,
    tamanoMinimoSp: Float,
    modifier: Modifier = Modifier
) {
    var tamanoActual by remember(texto, tamanoMaximoSp) { mutableFloatStateOf(tamanoMaximoSp) }

    Text(
        text = texto,
        fontSize = tamanoActual.sp,
        lineHeight = (tamanoActual * 1.25f).sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = modifier,
        onTextLayout = { resultado ->
            if (resultado.hasVisualOverflow && tamanoActual > tamanoMinimoSp) {
                tamanoActual = (tamanoActual - 2f).coerceAtLeast(tamanoMinimoSp)
            }
        }
    )
}
