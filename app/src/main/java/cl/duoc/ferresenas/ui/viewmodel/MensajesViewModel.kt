package cl.duoc.ferresenas.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.ferresenas.data.FerreSenasDbHelper
import cl.duoc.ferresenas.data.MensajeHistorial
import cl.duoc.ferresenas.data.Producto
import cl.duoc.ferresenas.data.RepositorioMensajes
import cl.duoc.ferresenas.data.SesionActual
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// historial de mensajes del usuario con sesion activa. despues de cada
// cambio (guardar, editar, borrar, vaciar) se vuelve a leer la lista desde
// la base para que la pantalla quede igual a lo que hay guardado
class MensajesViewModel(app: Application) : AndroidViewModel(app) {
    private val repositorio = RepositorioMensajes(FerreSenasDbHelper.obtener(app))

    var historial by mutableStateOf<List<MensajeHistorial>>(emptyList())
        private set

    private val correoActual: String?
        get() = SesionActual.usuarioActual?.correo

    fun cargar() {
        val correo = correoActual
        if (correo == null) {
            historial = emptyList()
            return
        }
        viewModelScope.launch { historial = repositorio.historialDe(correo) }
    }

    fun guardar(producto: Producto?, mensaje: String) {
        val correo = correoActual ?: return
        viewModelScope.launch {
            // NonCancellable para que se alcance a guardar aunque el usuario
            // vuelva atras justo en ese momento (ahi se cancela el viewModelScope)
            withContext(NonCancellable) { repositorio.agregar(correo, producto, mensaje) }
            historial = repositorio.historialDe(correo)
        }
    }

    fun editar(id: Long, nuevoTexto: String) {
        val correo = correoActual ?: return
        viewModelScope.launch {
            repositorio.editar(correo, id, nuevoTexto.trim())
            historial = repositorio.historialDe(correo)
        }
    }

    fun eliminar(id: Long) {
        val correo = correoActual ?: return
        viewModelScope.launch {
            repositorio.eliminar(correo, id)
            historial = repositorio.historialDe(correo)
        }
    }

    fun vaciar() {
        val correo = correoActual ?: return
        viewModelScope.launch {
            repositorio.vaciar(correo)
            historial = repositorio.historialDe(correo)
        }
    }
}
