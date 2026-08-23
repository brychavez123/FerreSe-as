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
 * Se precarga con 5 usuarios de prueba para poder iniciar sesión de inmediato
 * sin tener que registrarse primero.
 */
object RepositorioUsuarios {
    const val CUPO_MAXIMO = 5

    val usuarios = mutableStateListOf(
        Usuario("Ana Torres", "ana", "1234", PreferenciaComunicacion.ESCRIBIR, true),
        Usuario("Carlos Pérez", "carlos", "1234", PreferenciaComunicacion.HABLAR, false),
        Usuario("María Soto", "maria", "1234", PreferenciaComunicacion.AMBAS, true),
        Usuario("Luis Rojas", "luis", "1234", PreferenciaComunicacion.ESCRIBIR, false),
        Usuario("Sofía Díaz", "sofia", "1234", PreferenciaComunicacion.HABLAR, true)
    )

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

    fun buscarPorCorreo(correo: String): Usuario? =
        usuarios.find { it.correo.equals(correo, ignoreCase = true) }
}

/** Usuario con sesión activa en la app (nulo si nadie ha iniciado sesión). */
object SesionActual {
    var usuarioActual: Usuario? = null

    fun cerrarSesion() {
        usuarioActual = null
    }
}
