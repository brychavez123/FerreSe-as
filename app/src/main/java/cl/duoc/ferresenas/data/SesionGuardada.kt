package cl.duoc.ferresenas.data

import android.content.Context
import android.content.SharedPreferences

// lo del checkbox "Recordarme": guarda el correo con SharedPreferences
// para entrar directo al Home la proxima vez que se abra la app.
// la contraseña NO se guarda aca, solo el correo
object SesionGuardada {
    private const val ARCHIVO = "ferresenas_sesion"
    private const val CLAVE_CORREO = "correo_recordado"

    private var preferencias: SharedPreferences? = null

    // igual que PreferenciasApp, se llama una vez desde MainActivity.onCreate
    fun inicializar(contexto: Context) {
        if (preferencias != null) return
        preferencias = contexto.applicationContext.getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE)
    }

    val correoGuardado: String?
        get() = preferencias?.getString(CLAVE_CORREO, null)

    fun guardar(correo: String) {
        preferencias?.edit()?.putString(CLAVE_CORREO, correo)?.apply()
    }

    fun borrar() {
        preferencias?.edit()?.remove(CLAVE_CORREO)?.apply()
    }
}
