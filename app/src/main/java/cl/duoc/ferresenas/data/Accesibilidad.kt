package cl.duoc.ferresenas.data

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Función de extensión sobre Context: le agrega a Context la capacidad de
 * vibrar el dispositivo como confirmación, sin modificar la clase Context ni
 * repetir en cada pantalla la lógica de VibrationEffect según la versión de
 * Android. Complementa la confirmación visual, nunca la reemplaza, y respeta
 * la preferencia de vibración configurada en Configuración.
 */
fun Context.vibrarConfirmacion(duracionMs: Long = 150) {
    if (!PreferenciasApp.vibracionActiva) return
    try {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val gestor = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            gestor.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(duracionMs, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(duracionMs)
        }
    } catch (e: Exception) {
        // Si el dispositivo no tiene vibrador o niega el permiso, se ignora
        // en silencio: la confirmación visual ya cumplió su función.
    }
}
