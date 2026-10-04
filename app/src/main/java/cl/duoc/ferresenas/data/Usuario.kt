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
    // nunca la contraseña tal cual, solo el hash (ver Seguridad.kt)
    val hashContrasena: String,
    val preferenciaComunicacion: PreferenciaComunicacion,
    val recibirNotificaciones: Boolean
)

// extension sobre Usuario para saber si hay que leerle el mensaje con
// LectorDeVoz, sin tener que agregar un campo nuevo a la clase
val Usuario.prefiereEscuchar: Boolean
    get() = preferenciaComunicacion == PreferenciaComunicacion.HABLAR ||
        preferenciaComunicacion == PreferenciaComunicacion.AMBAS

/**
 * Array con los datos de los usuarios.
 * Se precarga con 5 usuarios de prueba para poder iniciar sesión
 * sin tener que registrarse primero.
 */

object RepositorioUsuarios {
    val usuarios = mutableStateListOf(
        Usuario("Valentina Muñoz", "valentina@ferresenas.cl", Seguridad.hashContrasena("1234"), PreferenciaComunicacion.ESCRIBIR, true),
        Usuario("Roberto Fernández", "roberto@ferresenas.cl", Seguridad.hashContrasena("1234"), PreferenciaComunicacion.HABLAR, false),
        Usuario("Camila Reyes", "camila@ferresenas.cl", Seguridad.hashContrasena("1234"), PreferenciaComunicacion.AMBAS, true),
        Usuario("Diego Castro", "diego@ferresenas.cl", Seguridad.hashContrasena("1234"), PreferenciaComunicacion.ESCRIBIR, false),
        Usuario("Javiera Morales", "javiera@ferresenas.cl", Seguridad.hashContrasena("1234"), PreferenciaComunicacion.HABLAR, true)
    )

    fun registrar(usuario: Usuario) {
        usuarios.add(usuario)
    }

    // reemplaza los datos de un usuario que ya estaba registrado, por
    // ejemplo cuando cambia sus preferencias desde Perfil
    fun actualizar(usuarioActualizado: Usuario) {
        val indice = usuarios.indexOfFirst { it.correo.equals(usuarioActualizado.correo, ignoreCase = true) }
        if (indice != -1) usuarios[indice] = usuarioActualizado
    }

    fun existeCorreo(correo: String): Boolean =
        usuarios.any { it.correo.equals(correo, ignoreCase = true) }

    fun validarCredenciales(correo: String, contrasena: String): Boolean =
        usuarios.any {
            it.correo.equals(correo, ignoreCase = true) && Seguridad.verificarContrasena(contrasena, it.hashContrasena)
        }

    fun buscarPorCorreo(correo: String): Usuario? =
        usuarios.find { it.correo.equals(correo, ignoreCase = true) }
}

/** Usuario con sesión activa en la app (nulo si nadie ha iniciado sesión). */
object SesionActual {
    // esto tiene que ser mutableStateOf, si no cuando cambio las
    // preferencias desde Perfil la pantalla no se actualiza sola
    var usuarioActual: Usuario? by mutableStateOf(null)
    var fotoPerfilUri: Uri? by mutableStateOf(null)

    fun cerrarSesion() {
        usuarioActual = null
        fotoPerfilUri = null
    }
}
