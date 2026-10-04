package cl.duoc.ferresenas

import cl.duoc.ferresenas.data.CatalogoProductos
import cl.duoc.ferresenas.data.Producto
import cl.duoc.ferresenas.data.categoriasDisponibles
import cl.duoc.ferresenas.data.primerError
import cl.duoc.ferresenas.data.textoACantidadSegura
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

// pruebas de las funciones de Utilidades.kt (las que ya se evaluaron antes)
class UtilidadesTest {

    // ---------- textoACantidadSegura ----------

    @Test
    fun cantidadValida_devuelveElNumero() {
        assertEquals(10, textoACantidadSegura("10").getOrNull())
    }

    @Test
    fun cantidadConEspacios_seLimpia() {
        assertEquals(7, textoACantidadSegura("  7 ").getOrNull())
    }

    @Test
    fun cantidadVacia_daError() {
        val resultado = textoACantidadSegura("")
        assertTrue(resultado.isFailure)
        assertEquals("Ingresa una cantidad válida (solo números).", resultado.exceptionOrNull()?.message)
    }

    @Test
    fun cantidadConLetras_daError() {
        assertTrue(textoACantidadSegura("diez").isFailure)
    }

    @Test
    fun cantidadCeroONegativa_daError() {
        assertEquals("La cantidad debe ser mayor a 0.", textoACantidadSegura("0").exceptionOrNull()?.message)
        assertEquals("La cantidad debe ser mayor a 0.", textoACantidadSegura("-3").exceptionOrNull()?.message)
    }

    @Test
    fun elFinallySiempreCorre() {
        // alFinalizar se tiene que llamar tanto si funciona como si falla
        var veces = 0
        textoACantidadSegura("5") { veces++ }
        textoACantidadSegura("abc") { veces++ }
        assertEquals(2, veces)
    }

    // ---------- primerError ----------

    @Test
    fun primerError_todoOk_devuelveNull() {
        assertNull(primerError(true to { "a" }, true to { "b" }))
    }

    @Test
    fun primerError_devuelveSoloElPrimeroQueFalla() {
        val error = primerError(true to { "a" }, false to { "b" }, false to { "c" })
        assertEquals("b", error)
    }

    @Test
    fun primerError_noArmaMensajesQueNoSeUsan() {
        // la lambda de una validacion que se cumple no se debe ejecutar
        var seEjecuto = false
        primerError(true to { seEjecuto = true; "x" })
        assertTrue(!seEjecuto)
    }

    // ---------- categoriasDisponibles ----------

    @Test
    fun categorias_delCatalogo_sinRepetirYOrdenadas() {
        assertEquals(
            listOf("Eléctrico", "Fijación", "Herramientas", "Medición", "Pintura"),
            CatalogoProductos.productos.categoriasDisponibles
        )
    }

    @Test
    fun categorias_listaVacia_devuelveVacia() {
        assertEquals(emptyList<String>(), emptyList<Producto>().categoriasDisponibles)
    }
}
