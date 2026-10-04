package cl.duoc.ferresenas.data

// funcion de orden superior (recibe una lambda) e inline, para no repetir
// el mismo if en cada validacion del formulario de registro
inline fun ejecutarSi(condicion: Boolean, accion: () -> Unit) {
    if (condicion) accion()
}

// otra de orden superior: recorre las validaciones y tira el primer error
// que encuentre, o null si esta todo ok. cada validacion trae su propia
// lambda con el mensaje para que no se arme el string si no hace falta
inline fun primerError(vararg validaciones: Pair<Boolean, () -> String>): String? {
    for ((cumple, mensaje) in validaciones) {
        if (!cumple) return mensaje()
    }
    return null
}

// extension sobre String para poner la primera letra en mayuscula (no se
// puede heredar de String porque es una clase final, por eso extension)
fun String.capitalizarPrimeraLetra(): String =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

// extension sobre Int, nada mas para que no quede en 0 o negativo
fun Int.aTextoCantidad(): String = if (this <= 0) "1" else toString()

// extension sobre List<Producto>, con map + distinct + sortedBy saca las
// categorias que hay en el catalogo sin tener que escribirlas a mano
val List<Producto>.categoriasDisponibles: List<String>
    get() = map { it.categoria }.distinct().sortedBy { it }

// esta es sobre List<MensajeHistorial>. como la consulta a la base los
// ordena por fecha descendente, el primero es el mas reciente
val List<MensajeHistorial>.masReciente: MensajeHistorial?
    get() = firstOrNull()

// pasa el texto de cantidad a numero. uso try/catch/finally porque si el
// campo queda vacio o el usuario escribe cualquier cosa no quiero que la
// app se caiga, solo que muestre el error. el finally corre siempre, se
// haya podido convertir o no
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
