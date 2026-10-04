package cl.duoc.ferresenas.ui.viewmodel

import android.app.Application
import android.database.sqlite.SQLiteException
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.ferresenas.data.FerreSenasDbHelper
import cl.duoc.ferresenas.data.PreferenciaComunicacion
import cl.duoc.ferresenas.data.RepositorioUsuarios
import cl.duoc.ferresenas.data.SesionActual
import cl.duoc.ferresenas.data.SesionGuardada
import cl.duoc.ferresenas.data.Usuario
import kotlinx.coroutines.launch

// mensaje para cuando falla la base de datos (disco lleno, base bloqueada, etc)
const val ERROR_BASE_DATOS = "No se pudo acceder a los datos. Intenta de nuevo."

// todo lo de la cuenta: login, registro, recuperar contraseña, perfil y
// eliminar cuenta. las pantallas solo llaman a estas funciones y reciben
// el resultado por callback, asi no tocan la base de datos directo.
// AndroidViewModel porque necesito el contexto para abrir la base
//
// las consultas a SQLite si pueden fallar de verdad, por eso cada una va en
// try/catch con SQLiteException (el error especifico de la base) y en el
// finally se vuelve a habilitar el boton, haya funcionado o no
class SesionViewModel(app: Application) : AndroidViewModel(app) {
    private val repositorio = RepositorioUsuarios(FerreSenasDbHelper.obtener(app))

    // para deshabilitar el boton mientras consulta y que no lo aprieten 2 veces
    var cargando by mutableStateOf(false)
        private set

    // si el usuario marco "Recordarme" la vez anterior, lo vuelve a cargar
    // desde la base. si la cuenta ya no existe, se olvida el correo
    fun restaurarSesion(alTerminar: () -> Unit) {
        val correo = SesionGuardada.correoGuardado
        if (SesionActual.usuarioActual != null || correo == null) {
            alTerminar()
            return
        }
        viewModelScope.launch {
            try {
                val usuario = repositorio.buscarPorCorreo(correo)
                if (usuario != null) SesionActual.usuarioActual = usuario else SesionGuardada.borrar()
            } catch (e: SQLiteException) {
                // si no se pudo leer, parte en el Login y listo. no borro el
                // correo guardado porque la cuenta puede seguir existiendo
            } finally {
                alTerminar()
            }
        }
    }

    fun iniciarSesion(
        correo: String,
        contrasena: String,
        recordarme: Boolean,
        onExito: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (cargando) return
        cargando = true
        viewModelScope.launch {
            try {
                val usuario = repositorio.validarCredenciales(correo, contrasena)
                if (usuario != null) {
                    SesionActual.usuarioActual = usuario
                    if (recordarme) SesionGuardada.guardar(usuario.correo) else SesionGuardada.borrar()
                    onExito()
                } else {
                    onError("Correo o contraseña incorrectos. Verifica tus datos o regístrate.")
                }
            } catch (e: SQLiteException) {
                onError(ERROR_BASE_DATOS)
            } finally {
                cargando = false
            }
        }
    }

    fun registrar(
        nombre: String,
        correo: String,
        contrasena: String,
        preferencia: PreferenciaComunicacion,
        recibirNotificaciones: Boolean,
        onExito: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (cargando) return
        cargando = true
        viewModelScope.launch {
            try {
                val creado = repositorio.registrar(nombre, correo, contrasena, preferencia, recibirNotificaciones)
                // lo vuelvo a leer de la base para quedar con el correo como quedo guardado
                val usuario = if (creado) repositorio.buscarPorCorreo(correo) else null
                if (usuario != null) {
                    SesionActual.usuarioActual = usuario
                    onExito()
                } else {
                    onError("Ese correo ya está registrado.")
                }
            } catch (e: SQLiteException) {
                onError(ERROR_BASE_DATOS)
            } finally {
                cargando = false
            }
        }
    }

    fun recuperarContrasena(
        correo: String,
        nuevaContrasena: String,
        onExito: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (cargando) return
        cargando = true
        viewModelScope.launch {
            try {
                val cambiada = repositorio.cambiarContrasena(correo, nuevaContrasena)
                if (cambiada) onExito() else onError("No encontramos una cuenta asociada a ese correo.")
            } catch (e: SQLiteException) {
                onError(ERROR_BASE_DATOS)
            } finally {
                cargando = false
            }
        }
    }

    // para los cambios del Perfil (nombre, preferencia, notificaciones)
    fun actualizarPerfil(nuevo: Usuario, onListo: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                if (repositorio.actualizar(nuevo)) {
                    SesionActual.usuarioActual = nuevo
                    onListo()
                }
            } catch (e: SQLiteException) {
                onError(ERROR_BASE_DATOS)
            }
        }
    }

    // si falla no cierro la sesion, asi el usuario ve el error y puede reintentar
    fun eliminarCuenta(onListo: () -> Unit, onError: (String) -> Unit) {
        val correo = SesionActual.usuarioActual?.correo ?: return
        viewModelScope.launch {
            try {
                repositorio.eliminar(correo)
                SesionActual.cerrarSesion()
                onListo()
            } catch (e: SQLiteException) {
                onError(ERROR_BASE_DATOS)
            }
        }
    }

    fun cerrarSesion() {
        SesionActual.cerrarSesion()
    }
}
