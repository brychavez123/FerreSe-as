package cl.duoc.ferresenas.data

data class Producto(
    val id: Int,
    val nombre: String,
    val emoji: String,
    val unidadesMedida: List<String>
)

/**
 * Catálogo de ejemplo de una ferretería, usado para construir mensajes
 * visuales que el cliente sordo/hipoacúsico puede mostrar al vendedor.
 */
object CatalogoProductos {
    val productos = listOf(
        Producto(1, "Tornillos", "🔩", listOf("1/4\"", "1/2\"", "3/4\"", "1\"")),
        Producto(2, "Clavos", "📌", listOf("1\"", "2\"", "3\"")),
        Producto(3, "Pintura", "🎨", listOf("1/4 galón", "1 galón", "4 litros")),
        Producto(4, "Cinta métrica", "📏", listOf("3 m", "5 m", "8 m")),
        Producto(5, "Llave inglesa", "🔧", listOf("6\"", "8\"", "10\"")),
        Producto(6, "Martillo", "🔨", listOf("Estándar", "Grande")),
        Producto(7, "Cable eléctrico", "🔌", listOf("1 metro", "5 metros", "10 metros")),
        Producto(8, "Foco / Ampolleta", "💡", listOf("Cálida", "Fría"))
    )

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
