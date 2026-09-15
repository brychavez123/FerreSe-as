package cl.duoc.ferresenas.data

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

// uso el TextToSpeech de Android para leer el mensaje en voz alta.
// se activa solo si el usuario eligio "Hablar" o "Ambas" en su perfil,
// pero el boton de escuchar mensaje lo puede usar cualquiera igual
object LectorDeVoz {
    private var tts: TextToSpeech? = null
    private var listoParaHablar = false

    // se llama una vez desde MainActivity.onCreate
    fun inicializar(contexto: Context) {
        if (tts != null) return
        tts = TextToSpeech(contexto.applicationContext) { estado ->
            if (estado == TextToSpeech.SUCCESS) {
                tts?.language = Locale("es", "CL")
                listoParaHablar = true
            }
        }
    }

    // QUEUE_FLUSH corta lo que estuviera leyendo antes y arranca con este
    fun leer(texto: String) {
        if (!listoParaHablar || texto.isBlank()) return
        tts?.speak(texto, TextToSpeech.QUEUE_FLUSH, null, "ferresenas_mensaje")
    }

    // hay que llamar esto al cerrar la app, si no queda el motor de voz prendido
    fun liberar() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        listoParaHablar = false
    }
}
