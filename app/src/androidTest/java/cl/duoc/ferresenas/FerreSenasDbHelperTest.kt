package cl.duoc.ferresenas

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import cl.duoc.ferresenas.data.FerreSenasDbHelper
import cl.duoc.ferresenas.data.PreferenciaComunicacion
import cl.duoc.ferresenas.data.Seguridad
import cl.duoc.ferresenas.data.Usuario
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

// CRUD de FerreSenasDbHelper contra una base EN MEMORIA (nombreBd = null),
// asi no se toca la base real de la app y cada prueba parte limpia
@RunWith(AndroidJUnit4::class)
class FerreSenasDbHelperTest {
    private lateinit var db: FerreSenasDbHelper

    private val usuarioPrueba = Usuario(
        nombre = "Prueba Test",
        correo = "prueba@test.cl",
        hashContrasena = Seguridad.hashContrasena("abcd"),
        preferenciaComunicacion = PreferenciaComunicacion.AMBAS,
        recibirNotificaciones = true
    )

    @Before
    fun crearBase() {
        val contexto = InstrumentationRegistry.getInstrumentation().targetContext
        db = FerreSenasDbHelper(contexto, null)
    }

    @After
    fun cerrarBase() {
        db.close()
    }

    @Test
    fun onCreate_precargaLos5UsuariosDePrueba() {
        listOf("valentina", "roberto", "camila", "diego", "javiera").forEach { nombre ->
            val usuario = db.buscarUsuario("$nombre@ferresenas.cl")
            assertNotNull("Falta $nombre", usuario)
            assertTrue(Seguridad.verificarContrasena("1234", usuario!!.hashContrasena))
        }
    }

    @Test
    fun crearYConsultarUsuario() {
        assertTrue(db.insertarUsuario(usuarioPrueba))
        val leido = db.buscarUsuario("prueba@test.cl")
        assertEquals("Prueba Test", leido?.nombre)
        assertEquals(PreferenciaComunicacion.AMBAS, leido?.preferenciaComunicacion)
        assertEquals(true, leido?.recibirNotificaciones)
    }

    @Test
    fun correoRepetido_noSeInserta() {
        assertTrue(db.insertarUsuario(usuarioPrueba))
        assertFalse(db.insertarUsuario(usuarioPrueba.copy(nombre = "Otro")))
        // tampoco cambiando mayusculas, porque se guardan en minuscula
        assertFalse(db.insertarUsuario(usuarioPrueba.copy(correo = "PRUEBA@test.cl")))
    }

    @Test
    fun buscarUsuario_ignoraMayusculasYEspacios() {
        db.insertarUsuario(usuarioPrueba)
        assertNotNull(db.buscarUsuario("  Prueba@Test.CL "))
    }

    @Test
    fun textoConComillas_noRompeLaConsulta() {
        // si el SQL se armara concatenando texto esto fallaria o haria otra cosa
        assertNull(db.buscarUsuario("' OR '1'='1"))
    }

    @Test
    fun modificarUsuario() {
        db.insertarUsuario(usuarioPrueba)
        val cambiado = usuarioPrueba.copy(
            nombre = "Nombre Nuevo",
            preferenciaComunicacion = PreferenciaComunicacion.ESCRIBIR,
            recibirNotificaciones = false
        )
        assertEquals(1, db.actualizarUsuario(cambiado))
        val leido = db.buscarUsuario(usuarioPrueba.correo)!!
        assertEquals("Nombre Nuevo", leido.nombre)
        assertEquals(PreferenciaComunicacion.ESCRIBIR, leido.preferenciaComunicacion)
        assertFalse(leido.recibirNotificaciones)
    }

    @Test
    fun cambiarContrasena() {
        db.insertarUsuario(usuarioPrueba)
        assertEquals(1, db.actualizarHashContrasena(usuarioPrueba.correo, Seguridad.hashContrasena("nueva")))
        val leido = db.buscarUsuario(usuarioPrueba.correo)!!
        assertTrue(Seguridad.verificarContrasena("nueva", leido.hashContrasena))
        assertFalse(Seguridad.verificarContrasena("abcd", leido.hashContrasena))
        // un correo que no existe no actualiza nada
        assertEquals(0, db.actualizarHashContrasena("noexiste@test.cl", "x:y"))
    }

    @Test
    fun crearYConsultarMensajes_soloLosDelUsuario() {
        db.insertarUsuario(usuarioPrueba)
        db.insertarMensaje(usuarioPrueba.correo, 1, "mensaje viejo", fecha = 1000)
        db.insertarMensaje(usuarioPrueba.correo, null, "mensaje nuevo", fecha = 2000)
        db.insertarMensaje("valentina@ferresenas.cl", 2, "de otra persona")

        val mensajes = db.mensajesDeUsuario(usuarioPrueba.correo)
        assertEquals(2, mensajes.size)
        // el mas nuevo primero
        assertEquals("mensaje nuevo", mensajes[0].texto)
        assertNull(mensajes[0].productoId)
        assertEquals(1, mensajes[1].productoId)
    }

    @Test
    fun mensajeDeUsuarioInexistente_loRechazaLaClaveForanea() {
        assertEquals(-1L, db.insertarMensaje("fantasma@test.cl", null, "hola"))
    }

    @Test
    fun modificarTextoDeMensaje() {
        db.insertarUsuario(usuarioPrueba)
        val id = db.insertarMensaje(usuarioPrueba.correo, 1, "original")
        assertEquals(1, db.actualizarTextoMensaje(id, usuarioPrueba.correo, "editado"))
        assertEquals("editado", db.mensajesDeUsuario(usuarioPrueba.correo).first().texto)
        // otro usuario no puede editar ese mensaje
        assertEquals(0, db.actualizarTextoMensaje(id, "valentina@ferresenas.cl", "hackeado"))
    }

    @Test
    fun eliminarUnMensajeYVaciarHistorial() {
        db.insertarUsuario(usuarioPrueba)
        val id = db.insertarMensaje(usuarioPrueba.correo, 1, "uno")
        db.insertarMensaje(usuarioPrueba.correo, 2, "dos")
        db.insertarMensaje(usuarioPrueba.correo, 3, "tres")

        assertEquals(1, db.eliminarMensaje(id, usuarioPrueba.correo))
        assertEquals(2, db.mensajesDeUsuario(usuarioPrueba.correo).size)

        assertEquals(2, db.eliminarMensajesDeUsuario(usuarioPrueba.correo))
        assertTrue(db.mensajesDeUsuario(usuarioPrueba.correo).isEmpty())
    }

    @Test
    fun eliminarCuenta_borraSusMensajesEnCascada() {
        db.insertarUsuario(usuarioPrueba)
        db.insertarMensaje(usuarioPrueba.correo, 1, "uno")
        db.insertarMensaje(usuarioPrueba.correo, null, "dos")
        db.insertarMensaje("valentina@ferresenas.cl", 1, "de valentina")

        assertEquals(1, db.eliminarUsuario(usuarioPrueba.correo))
        assertNull(db.buscarUsuario(usuarioPrueba.correo))
        assertTrue(db.mensajesDeUsuario(usuarioPrueba.correo).isEmpty())
        // los mensajes de los demas siguen ahi
        assertEquals(1, db.mensajesDeUsuario("valentina@ferresenas.cl").size)
    }
}
