package cl.duoc.ferresenas.data

/**
 * Función de orden superior e inline: recibe otra función (una lambda) como
 * parámetro y la ejecuta solo si se cumple la condición. Al ser "inline", el
 * compilador copia el cuerpo de la lambda en el lugar donde se llama, en vez
 * de crear un objeto función, por lo que no agrega costo en tiempo de
 * ejecución.
 */
inline fun ejecutarSi(condicion: Boolean, accion: () -> Unit) {
    if (condicion) accion()
}

/**
 * Función de orden superior e inline que retorna el primer mensaje de error
 * encontrado (o null si todas las validaciones pasan). Cada validación es un
 * par (se cumple o no) a (función que arma el mensaje), así el mensaje solo
 * se construye si realmente hace falta mostrarlo.
 */
inline fun primerError(vararg validaciones: Pair<Boolean, () -> String>): String? {
    for ((cumple, mensaje) in validaciones) {
        if (!cumple) return mensaje()
    }
    return null
}

/**
 * Función de extensión sobre String: agrega esta capacidad al tipo String sin
 * heredar de él ni modificar su código fuente (String es una clase final de
 * Kotlin/Java).
 */
fun String.capitalizarPrimeraLetra(): String =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

/**
 * Función de extensión sobre Int: da un valor de respaldo legible cuando la
 * cantidad ingresada no es válida.
 */
fun Int.aTextoCantidad(): String = if (this <= 0) "1" else toString()

/**
 * Propiedad de extensión sobre List<Producto>: usa funciones lambda con
 * colecciones (map, distinct, sortedBy) para calcular las categorías
 * disponibles a partir del catálogo, sin tocar la clase Producto.
 */
val List<Producto>.categoriasDisponibles: List<String>
    get() = map { it.categoria }.distinct().sortedBy { it }

/**
 * Propiedad de extensión sobre List<MensajeHistorial>: como
 * RepositorioMensajes.agregar() inserta cada mensaje nuevo al inicio, el más
 * reciente es siempre el primero de la lista.
 */
val List<MensajeHistorial>.masReciente: MensajeHistorial?
    get() = firstOrNull()

/**
 * Convierte el texto de cantidad ingresado por el usuario en un entero
 * válido, manejando errores con try/catch/finally para que un valor
 * inesperado (vacío, cero, no numérico) nunca detenga la aplicación.
 *
 * @param alFinalizar se ejecuta siempre al terminar, se haya podido
 * convertir la cantidad o no (por ejemplo, para registrar el intento).
 */
fun textoACantidadSegura(texto: String, alFinalizar: () -> Unit = {}): Result<Int> {
    return try {
        val cantidad = texto.trim().toInt()
        require(cantidad > 0) { "La cantidad debe ser mayor a 0." }
        Result.success(cantidad)
    } catch (e: NumberFormatException) {
        Result.failure(IllegalArgumentException("Ingresa una cantidad válida (solo números)."))
    } catch (e: IllegalArgumentException) {
        Result.failure(e)
    } finally {
        alFinalizar()
    }
}
