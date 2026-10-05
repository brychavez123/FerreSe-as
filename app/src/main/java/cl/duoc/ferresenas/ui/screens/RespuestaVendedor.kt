package cl.duoc.ferresenas.ui.screens

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.ferresenas.data.PreferenciasApp
import cl.duoc.ferresenas.data.capitalizarPrimeraLetra
import cl.duoc.ferresenas.data.tamanoMensajeSp
import cl.duoc.ferresenas.data.vibrarConfirmacion

// voz a texto: despues de mostrarle el mensaje, el vendedor responde
// hablando y la app le muestra la respuesta escrita al usuario. uso el
// reconocedor de voz que ya trae Android (RecognizerIntent), asi no hace
// falta ninguna libreria ni pedir permiso de microfono (lo maneja la app
// de voz del sistema)
@Composable
fun RespuestaDelVendedor(modifier: Modifier = Modifier) {
    val contexto = LocalContext.current
    var respuesta by rememberSaveable { mutableStateOf<String?>(null) }
    var aviso by rememberSaveable { mutableStateOf<String?>(null) }

    val reconocedor = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { resultado ->
        // el reconocedor devuelve una lista de posibles frases, la primera
        // es la mas probable
        val texto = resultado.data
            ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            ?.firstOrNull()
        if (resultado.resultCode == Activity.RESULT_OK && !texto.isNullOrBlank()) {
            respuesta = texto
            aviso = null
            // vibra para avisarle al usuario que ya llego la respuesta
            contexto.vibrarConfirmacion()
        } else {
            aviso = "No se entendió la respuesta. Pídele al vendedor que lo intente de nuevo."
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        respuesta?.let { texto ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                // con alto maximo para que una respuesta larga no tape el
                // mensaje de arriba (adentro tiene scroll)
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 220.dp)
                    .padding(bottom = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(12.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text("El vendedor dijo:", style = MaterialTheme.typography.labelLarge)
                    Text(
                        texto.capitalizarPrimeraLetra(),
                        fontSize = PreferenciasApp.tamanoMensajeSp.sp,
                        lineHeight = (PreferenciasApp.tamanoMensajeSp * 1.25f).sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        aviso?.let {
            Text(
                it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        OutlinedButton(
            onClick = {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-CL")
                    // este texto lo lee el vendedor en la ventana del microfono
                    putExtra(RecognizerIntent.EXTRA_PROMPT, "Responda hablando, su respuesta aparecerá escrita")
                }
                // esto si puede fallar de verdad: hay celulares sin ninguna
                // app de reconocimiento de voz instalada
                try {
                    reconocedor.launch(intent)
                } catch (e: ActivityNotFoundException) {
                    aviso = "Este celular no tiene reconocimiento de voz. Pídele al vendedor que escriba la respuesta."
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.Mic, contentDescription = null)
            Text(
                if (respuesta == null) "Que el vendedor responda hablando" else "Escuchar otra respuesta",
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}
