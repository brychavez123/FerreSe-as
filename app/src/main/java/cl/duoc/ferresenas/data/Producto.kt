package cl.duoc.ferresenas.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Producto(
    val id: Int,
    val nombre: String,
    val emoji: String,
    val categoria: String,
    val unidadesMedida: List<String>,
    val descripcion: String,
    val imagenNombre: String
)

object CatalogoProductos {
    const val CATEGORIA_TODOS = "Todos"

    val productos = listOf(
        Producto(
            1, "Tornillos", "🔩", "Fijación",
            listOf("1/4 de pulgada", "1/2 pulgada", "3/4 de pulgada", "1 pulgada"),
            "Para fijar madera, metal o plástico de forma firme y duradera.", "tornillos"
        ),
        Producto(
            2, "Clavos", "📌", "Fijación", listOf("1 pulgada", "2 pulgadas", "3 pulgadas"),
            "Fijaciones rápidas y económicas para trabajos en madera.", "clavos"
        ),
        Producto(
            3, "Pintura", "🎨", "Pintura", listOf("1/4 de galón", "1 galón", "4 litros"),
            "Cubre y protege superficies interiores y exteriores.", "pintura"
        ),
        Producto(
            4, "Cinta métrica", "📏", "Medición", listOf("3 metros", "5 metros", "8 metros"),
            "Mide distancias con precisión antes de comprar o instalar.", "cinta_metrica"
        ),
        Producto(
            5, "Llave inglesa", "🔧", "Herramientas", listOf("6 pulgadas", "8 pulgadas", "10 pulgadas"),
            "Ajusta tuercas y pernos de distintos tamaños.", "llave_inglesa"
        ),
        Producto(
            6, "Martillo", "🔨", "Herramientas", listOf("Estándar", "Grande"),
            "Para clavar, ajustar y desmontar piezas.", "martillo"
        ),
        Producto(
            7, "Cable eléctrico", "🔌", "Eléctrico", listOf("1 metro", "5 metros", "10 metros"),
            "Conduce energía de forma segura en instalaciones eléctricas.", "cable_electrico"
        ),
        Producto(
            8, "Foco / Ampolleta", "💡", "Eléctrico", listOf("Cálida", "Fría"),
            "Ilumina espacios interiores y exteriores.", "foco_ampolleta"
        )
    )

    fun buscarPorId(id: Int): Producto? = productos.find { it.id == id }
}

enum class TipoMensaje(val plantilla: String) {
    NECESITO_COMPRAR(
        "Hola, necesito comprar {cantidad} {producto}, de esta medida: {medida}. " +
            "¿Me puede ayudar, por favor?"
    ),
    CONSULTA_DISPONIBILIDAD(
        "Hola, quisiera consultar si tiene disponible {cantidad} {producto}, " +
            "de esta medida: {medida}. Muchas gracias."
    )
}

fun construirMensaje(
    tipo: TipoMensaje,
    producto: Producto,
    cantidad: String,
    medida: String
): String {
    return tipo.plantilla
        .replace("{cantidad}", cantidad.ifBlank { "1" })
        .replace("{producto}", producto.nombre.lowercase())
        .replace("{medida}", medida)
}

data class MensajeHistorial(
    // id de la fila en la tabla mensajes, para poder editarlo o borrarlo
    val id: Long,
    // queda en null cuando el mensaje es de los personalizados (los que
    // el usuario escribe libre, no vienen de un producto del catalogo)
    val producto: Producto?,
    val mensaje: String,
    val fechaHora: String
)

/**
 * Historial de mensajes visuales, ahora guardado en SQLite.
 * Siempre se pide el correo para traer o tocar solo los mensajes de quien
 * tiene la sesion abierta.
 */
class RepositorioMensajes(private val db: FerreSenasDbHelper) {

    suspend fun historialDe(correo: String): List<MensajeHistorial> = withContext(Dispatchers.IO) {
        // SimpleDateFormat no es seguro entre hilos, por eso uno por llamada
        val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "CL"))
        db.mensajesDeUsuario(correo).map { fila ->
            MensajeHistorial(
                id = fila.id,
                producto = fila.productoId?.let { CatalogoProductos.buscarPorId(it) },
                mensaje = fila.texto,
                fechaHora = formato.format(Date(fila.fecha))
            )
        }
    }

    suspend fun agregar(correo: String, producto: Producto?, mensaje: String): Boolean = withContext(Dispatchers.IO) {
        db.insertarMensaje(correo, producto?.id, mensaje) != -1L
    }

    suspend fun editar(correo: String, id: Long, nuevoTexto: String): Boolean = withContext(Dispatchers.IO) {
        db.actualizarTextoMensaje(id, correo, nuevoTexto) > 0
    }

    suspend fun eliminar(correo: String, id: Long): Boolean = withContext(Dispatchers.IO) {
        db.eliminarMensaje(id, correo) > 0
    }

    suspend fun vaciar(correo: String): Int = withContext(Dispatchers.IO) {
        db.eliminarMensajesDeUsuario(correo)
    }
}
