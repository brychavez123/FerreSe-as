package cl.duoc.ferresenas.data

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

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
 * Array con los datos de los usuarios.
 * Se precarga con 5 usuarios de prueba para poder iniciar sesión
 * sin tener que registrarse primero.
 */

object RepositorioUsuarios {
    val usuarios = mutableStateListOf(
        Usuario("Valentina Muñoz", "valentina@ferresenas.cl", "1234", PreferenciaComunicacion.ESCRIBIR, true),
        Usuario("Roberto Fernández", "roberto@ferresenas.cl", "1234", PreferenciaComunicacion.HABLAR, false),
        Usuario("Camila Reyes", "camila@ferresenas.cl", "1234", PreferenciaComunicacion.AMBAS, true),
        Usuario("Diego Castro", "diego@ferresenas.cl", "1234", PreferenciaComunicacion.ESCRIBIR, false),
        Usuario("Javiera Morales", "javiera@ferresenas.cl", "1234", PreferenciaComunicacion.HABLAR, true)
    )

    fun registrar(usuario: Usuario) {
        usuarios.add(usuario)
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
    var fotoPerfilUri: Uri? by mutableStateOf(null)

    fun cerrarSesion() {
        usuarioActual = null
        fotoPerfilUri = null
    }
}
