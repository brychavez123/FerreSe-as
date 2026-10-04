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

// esta la comparten el constructor de mensaje y el mensaje personalizado,
// asi no repito el mismo codigo dos veces. es a pantalla casi completa
// (queda la barra de arriba con el boton de volver) para que se pueda
// leer de lejos cuando se le muestra al vendedor
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
        // card fija en vez de Toast, para que no se pierda el mensaje si el
        // usuario se demora en mirar la pantalla
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

        // ocupa el espacio que queda libre y se va achicando solo si el
        // mensaje es muy largo (ver MensajeAutoAjustable mas abajo)
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

        // este boton lo puede usar cualquiera, no solo los que prefieren hablar
        TextButton(
            onClick = { LectorDeVoz.leer(mensaje) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null)
            Text("Escuchar mensaje", modifier = Modifier.padding(start = 8.dp))
        }

        // y al reves, voz a texto: lo que responde el vendedor aparece escrito
        RespuestaDelVendedor(modifier = Modifier.padding(bottom = 8.dp))

        Button(
            onClick = onEditar,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Editar mensaje")
        }
    }
}

// arranca grande y si el texto no cabe entero lo va achicando de a poquito
// (2sp por vuelta) hasta que entre, sin bajar del minimo. Asi un mensaje
// corto se ve grande y uno largo no queda cortado
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
