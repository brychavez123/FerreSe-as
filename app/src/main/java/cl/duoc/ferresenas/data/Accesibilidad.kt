package cl.duoc.ferresenas.data

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

// extension sobre Context para no repetir el if de la version de Android
// cada vez que necesito vibrar. Esto es solo un extra, no reemplaza el
// aviso visual (por eso si algo falla no hago nada, ver el catch de abajo)
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
        // si el celular no tiene vibrador o no da permiso no pasa nada,
        // igual ya se mostro el aviso visual
    }
}
