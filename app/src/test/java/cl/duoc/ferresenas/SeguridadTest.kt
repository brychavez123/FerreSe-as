package cl.duoc.ferresenas

import cl.duoc.ferresenas.data.Seguridad
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SeguridadTest {

    @Test
    fun elHash_noContieneLaContrasena() {
        val hash = Seguridad.hashContrasena("1234")
        assertFalse(hash.contains("1234"))
        // formato "sal:hash" en hex (16 bytes de sal y 32 de hash)
        assertTrue(hash.matches(Regex("[0-9a-f]{32}:[0-9a-f]{64}")))
    }

    @Test
    fun contrasenaCorrecta_seVerifica() {
        val hash = Seguridad.hashContrasena("clave segura")
        assertTrue(Seguridad.verificarContrasena("clave segura", hash))
    }

    @Test
    fun contrasenaIncorrecta_noSeVerifica() {
        val hash = Seguridad.hashContrasena("1234")
        assertFalse(Seguridad.verificarContrasena("12345", hash))
        assertFalse(Seguridad.verificarContrasena("", hash))
    }

    @Test
    fun mismaContrasena_daHashDistintoPorLaSal() {
        val hash1 = Seguridad.hashContrasena("1234")
        val hash2 = Seguridad.hashContrasena("1234")
        assertNotEquals(hash1, hash2)
        assertTrue(Seguridad.verificarContrasena("1234", hash1))
        assertTrue(Seguridad.verificarContrasena("1234", hash2))
    }

    @Test
    fun hashMalFormado_devuelveFalseSinCaerse() {
        assertFalse(Seguridad.verificarContrasena("1234", "1234"))
        assertFalse(Seguridad.verificarContrasena("1234", "zz:zz"))
        assertFalse(Seguridad.verificarContrasena("1234", "abc:def"))
    }
}
