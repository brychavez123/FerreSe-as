package cl.duoc.ferresenas

import cl.duoc.ferresenas.data.CatalogoProductos
import cl.duoc.ferresenas.data.TipoMensaje
import cl.duoc.ferresenas.data.construirMensaje
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class ConstruirMensajeTest {
    private val tornillos = CatalogoProductos.buscarPorId(1)!!

    @Test
    fun mensajeDeCompra_armaLaOracionCompleta() {
        assertEquals(
            "Hola, necesito comprar 10 tornillos, de esta medida: 1/2 pulgada. ¿Me puede ayudar, por favor?",
            construirMensaje(TipoMensaje.NECESITO_COMPRAR, tornillos, "10", "1/2 pulgada")
        )
    }

    @Test
    fun mensajeDeConsulta_usaLaOtraPlantilla() {
        assertEquals(
            "Hola, quisiera consultar si tiene disponible 3 tornillos, de esta medida: 1 pulgada. Muchas gracias.",
            construirMensaje(TipoMensaje.CONSULTA_DISPONIBILIDAD, tornillos, "3", "1 pulgada")
        )
    }

    @Test
    fun cantidadVacia_quedaEnUno() {
        val mensaje = construirMensaje(TipoMensaje.NECESITO_COMPRAR, tornillos, "  ", "1 pulgada")
        assertEquals(true, mensaje.contains("comprar 1 tornillos"))
    }

    @Test
    fun nombreDelProducto_vaEnMinuscula() {
        val foco = CatalogoProductos.buscarPorId(8)!!
        val mensaje = construirMensaje(TipoMensaje.NECESITO_COMPRAR, foco, "2", "Fría")
        assertEquals(true, mensaje.contains("foco / ampolleta"))
    }

    @Test
    fun noQuedanMarcadoresSinReemplazar() {
        CatalogoProductos.productos.forEach { producto ->
            TipoMensaje.entries.forEach { tipo ->
                val mensaje = construirMensaje(tipo, producto, "1", producto.unidadesMedida.first())
                assertFalse("Quedó un {..} en: $mensaje", mensaje.contains("{"))
            }
        }
    }
}
