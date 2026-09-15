package cl.duoc.ferresenas.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.math.roundToInt

// el factor multiplica el tamaño base de letra que esta en Type.kt,
// segun el nivel que haya elegido el usuario
enum class TamanoTexto(val factor: Float, val etiqueta: String) {
    PEQUENO(0.85f, "Pequeño"),
    NORMAL(1f, "Normal"),
    GRANDE(1.15f, "Grande"),
    MUY_GRANDE(1.3f, "Muy grande")
}

// para el boton "A+", pasa al siguiente nivel y si ya esta en el mas
// grande se queda ahi no mas
fun TamanoTexto.aumentado(): TamanoTexto {
    val siguiente = ordinal + 1
    return TamanoTexto.entries.getOrElse(siguiente) { this }
}

// lo mismo pero para "A-"
fun TamanoTexto.disminuido(): TamanoTexto {
    val anterior = ordinal - 1
    return TamanoTexto.entries.getOrElse(anterior) { this }
}

// alto contraste, tamaño de letra y vibracion, guardados con
// SharedPreferences para que no se borren al cerrar la app. Los deje como
// mutableStateOf para que las pantallas se actualicen solas y no tener
// que andar pasando callback por todos lados
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

    // esto se llama una vez sola, desde MainActivity.onCreate
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

// tamaño en sp del mensaje grande segun el nivel elegido (parte de una
// base de 26sp y se escala con el mismo factor de TamanoTexto)
val PreferenciasApp.tamanoMensajeSp: Int
    get() = (26 * tamanoTexto.factor).roundToInt()
