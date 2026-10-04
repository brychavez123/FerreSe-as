package cl.duoc.ferresenas.ui.viewmodel

import android.app.Application
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

// todo lo de la cuenta: login, registro, recuperar contraseña, perfil y
// eliminar cuenta. las pantallas solo llaman a estas funciones y reciben
// el resultado por callback, asi no tocan la base de datos directo.
// AndroidViewModel porque necesito el contexto para abrir la base
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
            val usuario = repositorio.buscarPorCorreo(correo)
            if (usuario != null) SesionActual.usuarioActual = usuario else SesionGuardada.borrar()
            alTerminar()
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
            val usuario = repositorio.validarCredenciales(correo, contrasena)
            cargando = false
            if (usuario != null) {
                SesionActual.usuarioActual = usuario
                if (recordarme) SesionGuardada.guardar(usuario.correo) else SesionGuardada.borrar()
                onExito()
            } else {
                onError("Correo o contraseña incorrectos. Verifica tus datos o regístrate.")
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
            val creado = repositorio.registrar(nombre, correo, contrasena, preferencia, recibirNotificaciones)
            // lo vuelvo a leer de la base para quedar con el correo como quedo guardado
            val usuario = if (creado) repositorio.buscarPorCorreo(correo) else null
            cargando = false
            if (usuario != null) {
                SesionActual.usuarioActual = usuario
                onExito()
            } else {
                onError("Ese correo ya está registrado.")
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
            val cambiada = repositorio.cambiarContrasena(correo, nuevaContrasena)
            cargando = false
            if (cambiada) onExito() else onError("No encontramos una cuenta asociada a ese correo.")
        }
    }

    // para los cambios del Perfil (nombre, preferencia, notificaciones)
    fun actualizarPerfil(nuevo: Usuario, onListo: () -> Unit = {}) {
        viewModelScope.launch {
            if (repositorio.actualizar(nuevo)) {
                SesionActual.usuarioActual = nuevo
                onListo()
            }
        }
    }

    fun eliminarCuenta(onListo: () -> Unit) {
        val correo = SesionActual.usuarioActual?.correo ?: return
        viewModelScope.launch {
            repositorio.eliminar(correo)
            SesionActual.cerrarSesion()
            onListo()
        }
    }

    fun cerrarSesion() {
        SesionActual.cerrarSesion()
    }
}
