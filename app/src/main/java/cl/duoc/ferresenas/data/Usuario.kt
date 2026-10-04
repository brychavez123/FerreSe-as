package cl.duoc.ferresenas.data

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
 * Usuarios guardados en SQLite (antes era una lista en memoria).
 * Los 5 usuarios de prueba ahora se precargan en FerreSenasDbHelper.onCreate.
 * Todo corre en Dispatchers.IO para no trabar la pantalla.
 */
class RepositorioUsuarios(private val db: FerreSenasDbHelper) {

    // false si el correo ya estaba registrado. el hash se calcula aca
    // adentro (en IO) porque PBKDF2 se demora un poco a proposito
    suspend fun registrar(
        nombre: String,
        correo: String,
        contrasena: String,
        preferencia: PreferenciaComunicacion,
        recibirNotificaciones: Boolean
    ): Boolean = withContext(Dispatchers.IO) {
        db.insertarUsuario(
            Usuario(nombre, correo, Seguridad.hashContrasena(contrasena), preferencia, recibirNotificaciones)
        )
    }

    // reemplaza los datos de un usuario que ya estaba registrado, por
    // ejemplo cuando cambia sus preferencias desde Perfil
    suspend fun actualizar(usuarioActualizado: Usuario): Boolean = withContext(Dispatchers.IO) {
        db.actualizarUsuario(usuarioActualizado) > 0
    }

    suspend fun existeCorreo(correo: String): Boolean = withContext(Dispatchers.IO) {
        db.buscarUsuario(correo) != null
    }

    // devuelve el usuario si el correo y la clave calzan, si no null
    suspend fun validarCredenciales(correo: String, contrasena: String): Usuario? = withContext(Dispatchers.IO) {
        db.buscarUsuario(correo)?.takeIf { Seguridad.verificarContrasena(contrasena, it.hashContrasena) }
    }

    suspend fun buscarPorCorreo(correo: String): Usuario? = withContext(Dispatchers.IO) {
        db.buscarUsuario(correo)
    }

    suspend fun cambiarContrasena(correo: String, nuevaContrasena: String): Boolean = withContext(Dispatchers.IO) {
        db.actualizarHashContrasena(correo, Seguridad.hashContrasena(nuevaContrasena)) > 0
    }

    // sus mensajes se borran solos por el ON DELETE CASCADE
    suspend fun eliminar(correo: String): Boolean = withContext(Dispatchers.IO) {
        db.eliminarUsuario(correo) > 0
    }
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
        // si habia marcado "Recordarme" tambien se olvida
        SesionGuardada.borrar()
    }
}
