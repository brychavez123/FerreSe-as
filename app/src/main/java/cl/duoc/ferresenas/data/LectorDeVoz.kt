package cl.duoc.ferresenas.data

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

/**
 * Envoltorio simple sobre TextToSpeech de Android: lee mensajes en voz alta
 * para las personas cuya preferencia de comunicación es Hablar o Ambas,
 * o para cualquiera que toque el botón de escuchar el mensaje.
 */
object LectorDeVoz {
    private var tts: TextToSpeech? = null
    private var listoParaHablar = false

    /** Debe llamarse una vez, al iniciar la app (MainActivity.onCreate). */
    fun inicializar(contexto: Context) {
        if (tts != null) return
        tts = TextToSpeech(contexto.applicationContext) { estado ->
            if (estado == TextToSpeech.SUCCESS) {
                tts?.language = Locale("es", "CL")
                listoParaHablar = true
            }
        }
    }

    /** Lee el texto en voz alta, interrumpiendo cualquier lectura anterior. */
    fun leer(texto: String) {
        if (!listoParaHablar || texto.isBlank()) return
        tts?.speak(texto, TextToSpeech.QUEUE_FLUSH, null, "ferresenas_mensaje")
    }

    /** Libera los recursos del motor de voz. Debe llamarse al cerrar la app. */
    fun liberar() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        listoParaHablar = false
    }
}
