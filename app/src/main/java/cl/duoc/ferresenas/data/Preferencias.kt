package cl.duoc.ferresenas.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.math.roundToInt

/**
 * Niveles de tamaño de letra disponibles para toda la aplicación. El factor
 * multiplica el tamaño base de cada estilo de texto (ver Type.kt).
 */
enum class TamanoTexto(val factor: Float, val etiqueta: String) {
    PEQUENO(0.85f, "Pequeño"),
    NORMAL(1f, "Normal"),
    GRANDE(1.15f, "Grande"),
    MUY_GRANDE(1.3f, "Muy grande")
}

/**
 * Función de extensión sobre TamanoTexto: da el siguiente nivel disponible
 * sin pasarse del máximo, para el botón "A+" de Configuración.
 */
fun TamanoTexto.aumentado(): TamanoTexto {
    val siguiente = ordinal + 1
    return TamanoTexto.entries.getOrElse(siguiente) { this }
}

/**
 * Función de extensión sobre TamanoTexto: da el nivel anterior sin bajar del
 * mínimo, para el botón "A-" de Configuración.
 */
fun TamanoTexto.disminuido(): TamanoTexto {
    val anterior = ordinal - 1
    return TamanoTexto.entries.getOrElse(anterior) { this }
}

/**
 * Preferencias de accesibilidad de la app (alto contraste, tamaño de letra y
 * vibración). Se guardan con SharedPreferences para que se mantengan aunque
 * el usuario cierre y vuelva a abrir la aplicación, y se exponen como estado
 * de Compose para que las pantallas se redibujen solas cuando cambian.
 */
object PreferenciasApp {
    private const val ARCHIVO = "ferresenas_preferencias"
    private const val CLAVE_ALTO_CONTRASTE = "alto_contraste"
    private const val CLAVE_TAMANO_TEXTO = "tamano_texto"
    private const val CLAVE_VIBRACION = "vibracion_activa"

    private var preferencias: SharedPreferences? = null

    var altoContraste by mutableStateOf(false)
        private set

    var tamanoTexto by mutableStateOf(TamanoTexto.NORMAL)
        private set

    var vibracionActiva by mutableStateOf(true)
        private set

    /** Debe llamarse una vez, al iniciar la app (MainActivity.onCreate). */
    fun inicializar(contexto: Context) {
        if (preferencias != null) return
        val prefs = contexto.applicationContext.getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE)
        preferencias = prefs
        altoContraste = prefs.getBoolean(CLAVE_ALTO_CONTRASTE, false)
        vibracionActiva = prefs.getBoolean(CLAVE_VIBRACION, true)

        val indiceGuardado = prefs.getInt(CLAVE_TAMANO_TEXTO, TamanoTexto.NORMAL.ordinal)
        tamanoTexto = TamanoTexto.entries.getOrElse(indiceGuardado) { TamanoTexto.NORMAL }
    }

    fun cambiarAltoContraste(activo: Boolean) {
        altoContraste = activo
        preferencias?.edit()?.putBoolean(CLAVE_ALTO_CONTRASTE, activo)?.apply()
    }

    fun cambiarTamanoTexto(nuevo: TamanoTexto) {
        tamanoTexto = nuevo
        preferencias?.edit()?.putInt(CLAVE_TAMANO_TEXTO, nuevo.ordinal)?.apply()
    }

    fun aumentarTamanoTexto() = cambiarTamanoTexto(tamanoTexto.aumentado())

    fun disminuirTamanoTexto() = cambiarTamanoTexto(tamanoTexto.disminuido())

    fun cambiarVibracion(activo: Boolean) {
        vibracionActiva = activo
        preferencias?.edit()?.putBoolean(CLAVE_VIBRACION, activo)?.apply()
    }
}

/**
 * Propiedad de extensión sobre PreferenciasApp: calcula a qué tamaño en sp
 * corresponde el mensaje grande que se le muestra al vendedor, proporcional
 * al tamaño de letra elegido en Configuración, sin modificar la clase
 * original ni guardar ese número aparte en SharedPreferences.
 */
val PreferenciasApp.tamanoMensajeSp: Int
    get() = (26 * tamanoTexto.factor).roundToInt()
