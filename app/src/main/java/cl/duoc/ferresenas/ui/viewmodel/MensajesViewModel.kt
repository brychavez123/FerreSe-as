package cl.duoc.ferresenas.ui.viewmodel

import android.app.Application
import android.database.sqlite.SQLiteException
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

    // si falla algo con la base queda aca el mensaje, y la pantalla lo
    // muestra como Toast (ver AvisoDeError en Dialogos.kt)
    var error by mutableStateOf<String?>(null)
        private set

    private val correoActual: String?
        get() = SesionActual.usuarioActual?.correo

    fun limpiarError() {
        error = null
    }

    // funcion de orden superior: recibe la operacion como lambda y le pone
    // el try/catch alrededor, asi no repito el mismo bloque en cada funcion.
    // solo atrapo SQLiteException, que es lo que de verdad puede fallar aca
    private fun ejecutarEnBase(operacion: suspend (correo: String) -> Unit) {
        val correo = correoActual ?: return
        viewModelScope.launch {
            try {
                operacion(correo)
            } catch (e: SQLiteException) {
                error = ERROR_BASE_DATOS
            }
        }
    }

    fun cargar() {
        if (correoActual == null) {
            historial = emptyList()
            return
        }
        ejecutarEnBase { correo -> historial = repositorio.historialDe(correo) }
    }

    fun guardar(producto: Producto?, mensaje: String) = ejecutarEnBase { correo ->
        // NonCancellable para que se alcance a guardar aunque el usuario
        // vuelva atras justo en ese momento (ahi se cancela el viewModelScope)
        val guardado = withContext(NonCancellable) { repositorio.agregar(correo, producto, mensaje) }
        // insert no tira excepcion, devuelve -1 si falla, por eso lo reviso aparte
        if (!guardado) error = "El mensaje se mostró, pero no se pudo guardar en el historial."
        historial = repositorio.historialDe(correo)
    }

    fun editar(id: Long, nuevoTexto: String) = ejecutarEnBase { correo ->
        repositorio.editar(correo, id, nuevoTexto.trim())
        historial = repositorio.historialDe(correo)
    }

    fun eliminar(id: Long) = ejecutarEnBase { correo ->
        repositorio.eliminar(correo, id)
        historial = repositorio.historialDe(correo)
    }

    fun vaciar() = ejecutarEnBase { correo ->
        repositorio.vaciar(correo)
        historial = repositorio.historialDe(correo)
    }
}
