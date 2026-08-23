package cl.duoc.ferresenas.data

import androidx.compose.runtime.mutableStateListOf

enum class PreferenciaComunicacion {
    ESCRIBIR, HABLAR, AMBAS
}

data class Usuario(
    val nombre: String,
    val correo: String,
    val contrasena: String,
    val preferenciaComunicacion: PreferenciaComunicacion,
    val recibirNotificaciones: Boolean
)

/**
 * Repositorio en memoria para el registro solicitado por la actividad:
 * arreglo con los datos de hasta 5 usuarios capturados en la vista de Registro.
 */
object RepositorioUsuarios {
    const val CUPO_MAXIMO = 5

    val usuarios = mutableStateListOf<Usuario>()

    val hayCupoDisponible: Boolean
        get() = usuarios.size < CUPO_MAXIMO

    fun registrar(usuario: Usuario): Boolean {
        if (!hayCupoDisponible) return false
        usuarios.add(usuario)
        return true
    }

    fun existeCorreo(correo: String): Boolean =
        usuarios.any { it.correo.equals(correo, ignoreCase = true) }

    fun validarCredenciales(correo: String, contrasena: String): Boolean =
        usuarios.any { it.correo.equals(correo, ignoreCase = true) && it.contrasena == contrasena }
}
