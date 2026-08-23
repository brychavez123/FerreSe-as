package cl.duoc.ferresenas.data

import androidx.compose.runtime.mutableStateListOf
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
            1, "Tornillos", "🔩", "Fijación", listOf("1/4\"", "1/2\"", "3/4\"", "1\""),
            "Para fijar madera, metal o plástico de forma firme y duradera.", "tornillos"
        ),
        Producto(
            2, "Clavos", "📌", "Fijación", listOf("1\"", "2\"", "3\""),
            "Fijaciones rápidas y económicas para trabajos en madera.", "clavos"
        ),
        Producto(
            3, "Pintura", "🎨", "Pintura", listOf("1/4 galón", "1 galón", "4 litros"),
            "Cubre y protege superficies interiores y exteriores.", "pintura"
        ),
        Producto(
            4, "Cinta métrica", "📏", "Medición", listOf("3 m", "5 m", "8 m"),
            "Mide distancias con precisión antes de comprar o instalar.", "cinta_metrica"
        ),
        Producto(
            5, "Llave inglesa", "🔧", "Herramientas", listOf("6\"", "8\"", "10\""),
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

    val categorias: List<String> = listOf(CATEGORIA_TODOS) + productos.map { it.categoria }.distinct()

    fun buscarPorId(id: Int): Producto? = productos.find { it.id == id }
}

enum class TipoMensaje(val plantilla: String) {
    NECESITO_COMPRAR("Necesito {cantidad} {producto} de esta medida: {medida}"),
    CONSULTA_DISPONIBILIDAD("¿Tiene disponible {producto} en esta medida: {medida}?")
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
    val producto: Producto,
    val mensaje: String,
    val fechaHora: String
)

/** Historial de mensajes visuales generados durante la sesión. */
object RepositorioMensajes {
    private val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "CL"))

    val historial = mutableStateListOf<MensajeHistorial>()

    fun agregar(producto: Producto, mensaje: String) {
        historial.add(0, MensajeHistorial(producto, mensaje, formato.format(Date())))
    }
}
